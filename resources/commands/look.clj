{ :aliases ["look" "l"]
  :fn (fn [message user]
        (do
          (clojure.tools.logging/info "In look with message:" [message user])
          (try
            ((juxt
              #(slack-mud.message-handler/send_message
                  (str "Short: "
                    (:shortdesc %1))
                  user)
              #(slack-mud.message-handler/send_message
                  (str "Desc: "
                    (:desc %1))
                  user))

             (get @slack-mud.rooms/rooms (:location user)))
            (catch RuntimeException e (prn "Runtime exception:")))))}
