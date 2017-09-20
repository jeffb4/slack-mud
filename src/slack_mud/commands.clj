(ns slack-mud.commands)

(require '[clojure.string :as str]
         '[clojure.tools.logging :as log])

(def commands (ref {:aliases {}}))

; List of commands allowed for various user types
(def commands_lists (ref {:player #{"repl" "look"}
                          :admin #{"repl" "look"}}))

(defn parse-command
  "Parse and run a command from a user, given a command string and user"
  [message user]
  (log/debug "In parse-command with" [message user])
  (let [split_text (str/split message #" ")
        aliases (:aliases @commands)
        command (first split_text)
        user_command_list (:command_list user)
        user_commands (get @commands_lists user_command_list)]
    (log/debug "Aliases:" aliases)
    ; if command is valid (in all aliases) and the alias
    ; dereference is allowed for user, proceed
    (if (and (get aliases command)
             (get user_commands (get aliases command)))
      (let [command_id (get aliases command)
            command_fn (:fn (get @commands command_id))]
        (log/info "Running command:"
                  command "with command_id" command_id)
        ((eval command_fn)
         (str/join " " (drop 1 split_text))
         user))
      (do
        (log/warn "Not running command:" command (get aliases command))))))

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
