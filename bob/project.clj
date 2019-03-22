(defproject io.wakamau/bob "0.1.0-SNAPSHOT"
  :description "Bob is the server "
  :url "TODO"
  :license {:name "TODO: Choose a license"
            :url "http://choosealicense.com/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [com.stuartsierra/component "0.4.0"]
                 [mount "0.1.16"]
                 [org.clojure/core.async "0.4.490"]
                 [cljfx "1.2.1"]
                 [com.bhauman/rebel-readline "0.1.4"]
                 [org.clojure/data.json "0.2.6"]
                 [cheshire "5.8.1"]
                 [commons-validator/commons-validator "1.6"]
                 ]
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [com.stuartsierra/component.repl "0.2.0"]]
                   :source-paths ["dev"]}
             :uberjar {:aot [io.wakamau.bob]}}
  :aliases {"rebl" ["trampoline" "run" "-m" "rebel-readline.main"]}
  :main ^{:skip-aot true} io.wakamau.bob
  :uberjar-name "bob.jar")
