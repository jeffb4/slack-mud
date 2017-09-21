{ :aliases ["hello"]
  :fn (fn [message user]
        (let [room (get @slack-mud.rooms/rooms (:location user))]
          (clojure.tools.logging/info
            "In hello with:" message user room)
          (try
            (slack-mud.message-handler/user_message
              (str "loggin in! " (:name user))
              user)
            (catch RuntimeException e
                   (clojure.tools.logging/error
                    "Runtime exception:" e)))
          true))}
