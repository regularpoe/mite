(ns mite.core
  (:require [cheshire.core :as json]
            [clj-time.core :as time]
            [clj-time.format :as fmt]
            [hawk.core :as hawk])
  (:gen-class))

(defn read-config [file]
  (let [content (slurp file)]
    (json/parse-string content true)))

(defn current-timestamp []
  (fmt/unparse (fmt/formatters :date-time) (time/now)))

(defn handle-event [ctx event]
  (let [kind (event :kind)
        path (event :file)]
    (println (str "Event: " kind " - " path)))
  ctx)

(defn monitor-paths [paths]
  (hawk/watch! [{:paths paths
                 :handler handle-event}]))

(defn -main [& args]
  (let [config (read-config "config.json")
        paths (:paths config)
        poll-interval (:poll_interval config)]
    (println "Monitoring paths:" paths "with poll interval:" poll-interval)
    (monitor-paths paths)
    (loop []
      (Thread/sleep (* poll-interval 1000))
      (recur))))

