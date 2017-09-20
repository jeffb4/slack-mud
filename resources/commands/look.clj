{ :aliases ["look" "l"]
  :fn (fn [message user]
        (let [room (get @slack-mud.rooms/rooms (:location user))]
          (clojure.tools.logging/info
            "In look with:" message user room)
          (try
            (doall
              (map
                #(slack-mud.message-handler/send_message
                    % user)
                [(str "Short: " (:shortdesc room))
                 (str "Desc: " (:desc room))]))
            (catch RuntimeException e
                   (clojure.tools.logging/error
                    "Runtime exception:" e)))))}
