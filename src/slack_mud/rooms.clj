(ns slack-mud.rooms)

(def rooms (ref {}))

(defn load-room 
  "Load a room file, returns updated room map"
  [rooms file]
  (let [room (read-string (slurp (.getAbsolutePath file)))]
    (conj rooms
          {(keyword (.getName file))
           {:name (keyword (.getName file))
            :desc (:desc room)
            :exits (ref (:exits room))
            :items (ref (or (:items room) #{}))
            :inhabitants (ref #{})}})))

(defn load-rooms
  "Given a dir, return a map with an entry corresponding to each file
  in it. Files should be maps containing room data."
  [rooms dir]
  (dosync
    (reduce load-room rooms
           (.listFiles (java.io.File. dir)))))
