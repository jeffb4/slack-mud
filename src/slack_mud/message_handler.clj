(ns slack-mud.message-handler)

(require '[cheshire.core :refer :all]
         '[manifold.stream :as s]
         '[clojure.tools.logging :as log]
         '[clojure.string :as str]
         '[slack-mud.users :as users]
         '[slack-mud.config :as config]
         '[slack-mud.commands :as commands])

(defmulti handler
  (fn[x y e]
    (let [t (x :type)
          ok (x :ok)]
      (if (and (not t)
               ok)
        :ok
        t))))

(defmethod handler "message" [message conn error]
  (println "Matched message type, parsing as command")
  (prn message)
  (if-not (contains? @users/users (:user message))
    (do
      (prn @users/users)
      (users/load-user (java.io.File.
                          (str "resources/users/" (:user message))))))
  (if-not (or
            (= (get message :reply_to nil) 0)
            (re-matches #"is currently in Do Not Disturb mode" (:text message)))
    (do
      (commands/parse-command
        (:text message)
        (assoc (get @users/users (:user message))
              :conn conn
              :channel (:channel message)))

      ; stupid echo debugger
      (s/put! conn
              (generate-string
                {:type "message"
                  :channel (:channel message)
                  :text (:text message)})))))

(defmethod handler "hello" [message conn error]
  (log/info "Received hello"))

(defmethod handler "presence_change" [message conn error]
  (log/info "Received presence change"))

(defmethod handler "reconnect_url" [message conn error]
  (log/info "Received new reconnect URL")
  (config/update-wsurl (:url message)))

(defmethod handler "desktop_notification" [message conn error]
  (log/debug "Discarding desktop_notification"))

(defmethod handler :ok [message conn error]
  (log/info "Received :ok"))

(defmethod handler :default [message conn error]
  (log/warn "Receive unknown message type:" message conn error)
  (if (= message {})
    (do
      (log/error "Received empty message, error")
      (deliver error "Empty"))))

(defn send_message
  [message user]
  (log/info "In send_message" [message user])
  (let [conn (:conn user)
        channel (:channel user)]
    (s/put!
      conn
      (generate-string
        {:type "message"
          :channel channel
          :text message}))))
