(defproject slack-mud "0.1.0-SNAPSHOT"
  :description "A MUD that uses Slack for communication"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.4.0"]
                 [org.julienxx/clj-slack "0.5.5"]
                 [aleph "0.4.3"]
                 [cheshire "5.8.0"]
                 [io.forward/yaml "1.0.6"]
                 [manifold "0.1.6"]]
  :source-paths ["src"]
  :main ^:skip-aot slack-mud.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
