{ :aliases ["repl"]
  :fn (fn [message user]
        (do
          (println (str "in repl with message" (pr-str message)))
          (eval (read-string message))))}
