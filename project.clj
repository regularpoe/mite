(defproject mite "0.1.0"
  :description "Watch for file change(s) on multiple paths"
  :url ""
  :license {:name "GNU GENERAL PUBLIC LICENSE Version 3"
            :url ""}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/tools.namespace "1.5.0"]
                 [cheshire "5.13.0"]
                 [clj-time "0.15.2"]
                 [hawk "0.2.11"]]
  :main ^:skip-aot mite.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
