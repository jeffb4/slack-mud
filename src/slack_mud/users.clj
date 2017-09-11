(ns slack-mud.users)

(def users (ref {}))

(defn new-user
  "Given a filename/user id, create a user file and return a user hash"
  [file]
  (println "In new-user")
  (prn file)
  (let [user {:name "Unknown Name"
              :desc "Generic description"
              :level 1
              :command_list :player}]
    (spit (.getAbsolutePath file) (pr-str user))
    (dosync
      (alter users conj {(.getName file) user}))))

(defn load-user
  "Load a user file, returns updated user map"
  [file]
  (println "In load-user")
  (try
    (let [user (read-string (slurp (.getAbsolutePath file)))]
      (prn user)
      (dosync
        (alter users
          conj {(.getName file) user})))
    (catch java.io.FileNotFoundException e
      (do
        (println (str "File not found: " (.getMessage e)))
        (new-user file)))))
