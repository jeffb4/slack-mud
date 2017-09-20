(ns slack-mud.rooms)

(require
  '[clojure.string :as str])

(def rooms (ref {}))

(defn load-room
  "Load a room file, returns updated room map"
  [rooms file]
  (let [room (read-string (slurp (.getAbsolutePath file)))]
    (conj rooms
          {(str/replace (.getName file) #"\.clj" "")
           {:name (keyword (.getName file))
            :desc (:desc room)
            :shortdesc (:shortdesc room)
            :exits (ref (or (:exits room) #{}))
            :items (ref (or (:items room) #{}))
            :inhabitants (ref #{})}})))

(defn load-rooms
  "Given a dir, return a map with an entry corresponding to each file
  in it. Files should be maps containing room data."
  [dir]
  (dosync
    (alter rooms conj (reduce load-room @rooms
                       (.listFiles (java.io.File. dir))))))
