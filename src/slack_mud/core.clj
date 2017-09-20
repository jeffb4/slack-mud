(ns slack-mud.core
  (:gen-class))

(require '[clj-slack.rtm]
         '[aleph.http :as http]
         '[manifold.stream :as s]
         '[cheshire.core :refer :all]
         '[clojure.tools.logging :as log]
         '[slack-mud.rooms :as rooms]
         '[slack-mud.config :as config]
         '[slack-mud.message-handler :as message-handler]
         '[slack-mud.commands :as commands])

(defn -main
  "Simple Slack bot"
  [& args]
  (config/read-config)
  (rooms/load-rooms "resources/rooms")
  (log/debug "rooms:" @rooms/rooms)
  (commands/load-commands commands/commands "resources/commands")
  (let [rtm-cxn (clj-slack.rtm/connect
                 {:api-url "https://slack.com/api"
                  :token (:token @config/slack)})]
    (log/debug "rtm-cxn:" rtm-cxn)
    (config/update-wsurl (:url rtm-cxn))
    (while true
      (do
        (let [conn @(http/websocket-client (:wsurl @config/slack))
              error (promise)]
          (s/consume
            (fn [message]
              (let [parsed_message (parse-string message true)]
                (log/debug "parsed_message:" parsed_message)
                (message-handler/handler parsed_message conn error)))
            conn)
          @error)))
    (log/error "True evaluated to false")))
