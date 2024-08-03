(ns mite.server
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.util.response :as response]
            [cheshire.core :as json]
            [clojure.core.async :refer [go >! <! chan]]))

(defmacro go-loop [& body]
  `(go
     (loop []
       ~@body
       (recur))))

(def events-chan (chan))

(defn sse-handler [request]
  (let [response (response/response "")
        output-stream (:body response)]
    (-> response
        (response/content-type "text/event-stream")
        (response/charset "UTF-8")
        (response/header "Cache-Control" "no-cache")
        (response/header "Connection" "keep-alive")
        (assoc :body output-stream))
    (go-loop []
      (when-let [event (<! events-chan)]
        (binding [*out* output-stream]
          (println (str "data: " event "\n"))
          (flush))))
    response))

(defn start-server []
  (run-jetty sse-handler {:port 3000 :join? false}))

(defn send-event [event-data]
  (go (>! events-chan (json/generate-string event-data))))

