(ns slack-mud.config)

(require '[yaml.core :as yaml]
         '[clojure.walk])

(def slack (ref {}))

(defn update-wsurl
  "Update web-socket URL for Slack connection"
  [wsurl]
  (dosync
    (alter slack assoc :wsurl wsurl))
  (prn @slack))

(defn read-config
  "Read slack-mud configuration from config.yml"
  []
  (let [file-config (clojure.walk/keywordize-keys (yaml/from-file "config.yml"))]
    (prn file-config)
    (dosync
      (alter slack conj (:slack file-config)))))
