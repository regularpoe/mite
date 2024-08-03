(ns mite.core
  (:require [cheshire.core :as json])
  (:gen-class))

(defn read-config [file]
  (let [content (slurp file)]
    (json/parse-string content true)))

(defn -main [& args]
  (let [config (read-config "config.json")]
    (println "Config:" config)))
