{ :aliases ["look" "l"]
  :fn (fn [message user]
        (do
          (println (str "in look with message" (pr-str message)))
          (eval (read-string message))))}