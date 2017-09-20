(ns slack-mud.commands)

(require '[clojure.string :as str]
         '[clojure.tools.logging :as log])

(def commands (ref {:aliases []}))

; List of commands allowed for various user types
(def commands_lists (ref {:player #{"repl" "look"}
                          :admin #{"repl" "look"}}))

(defn parse-command
  "Parse and run a command from a user, given a command string and user"
  [message user]
  (prn message)
  (let [split_text (str/split message #" ")
        aliases (get @commands :aliases)
        command (first split_text)
        command_id (get aliases command)
        command_fn (get @commands command_id)
        user_command_list (get user :command_list)
        user_commands (get @commands_lists user_command_list)]
    (println "In parse-command")
    (prn split_text)
    (prn command)
    (prn user)
    (prn user_command_list)
    (prn user_commands)
    (if (get user_commands command)
      (do
        (println (str "running command " command))
        ((eval command_fn)
         (str/join " " (drop 1 split_text))
         user))
      (println (str "NOT running command " command)))))

(defn load-command
  "Load a command file, returns updated command map"
  [commands file]
  (prn file)
  (let [command_file file
        command (read-string (slurp (.getAbsolutePath command_file)))
        command_id (str/replace (.getName file) #"\.clj" "")
        alias_vec (map #({% command_id}) (:aliases (eval command)))]
    (prn alias_vec)
    (conj @commands
          {command_id command
           :aliases (conj (:aliases @commands) alias_vec)})))

(defn load-commands
  "Given a dir, return a map with an entry corresponding to each file
  in it. Files should be maps containing command data."
  [dir]
  (dosync
    (alter commands conj (reduce load-command commands
                          (.listFiles (java.io.File. dir))))))
