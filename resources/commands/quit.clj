{ :aliases ["quit"]
  :fn (fn [message user]
        (let [room (get @slack-mud.rooms/rooms (:location user))]
          (clojure.tools.logging/info
            "In quit with:" message user room)
          (try
            (slack-mud.message-handler/user_message
              (str "loggin off! " (:name user))
              user)
            (catch RuntimeException e
                   (clojure.tools.logging/error
                    "Runtime exception:" e)))))}