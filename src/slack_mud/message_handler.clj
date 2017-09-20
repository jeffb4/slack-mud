(ns slack-mud.message-handler)

(require '[cheshire.core :refer :all]
         '[manifold.stream :as s]
         '[clojure.tools.logging :as log]
         '[clojure.string :as str]
         '[slack-mud.users :as users]
         '[slack-mud.config :as config]
         '[slack-mud.commands :as commands])

(defmulti handler
  (fn[x y e] (x :type)))

(defmethod handler "message" [message conn error]
  (println "Matched message type, parsing as command")
  (prn message)
  (if-not (contains? @users/users (:user message))
    (do
      (prn @users/users)
      (users/load-user (java.io.File.
                          (str "resources/users/" (:user message))))))
  (if-not (and
            (= (get message :reply_to nil) 0)
            (re-matches #"is currently in Do Not Disturb mode" (:text message)))
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
                :text (:text message)}))))

(defmethod handler "hello" [message conn error]
  (println "Received hello"))

(defmethod handler "presence_change" [message conn error]
  (println "Received presence change"))

(defmethod handler "reconnect_url" [message conn error]
  (println "Received new reconnect URL")
  (config/update-wsurl (:url message)))

(defmethod handler "desktop_notification" [message conn error]
  (println "Discarding desktop_notification"))

(defmethod handler :default [message conn error]
  (print "Receive unknown message type")
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
