(ns mite.server
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.cors :refer [wrap-cors]]
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
  (let [output-stream (java.io.PipedOutputStream.)
        input-stream (java.io.PipedInputStream. output-stream)
        response (-> (response/response input-stream)
                     (response/content-type "text/event-stream")
                     (response/charset "UTF-8")
                     (response/header "Cache-Control" "no-cache")
                     (response/header "Connection" "keep-alive"))]
    (go-loop []
      (when-let [event (<! events-chan)]
        (binding [*out* (java.io.OutputStreamWriter. output-stream)]
          (println (str "data: " event "\n"))
          (flush))))
    response))

(defn start-server []
  (run-jetty (wrap-cors sse-handler :access-control-allow-origin [#"http://localhost:8000"]
                        :access-control-allow-methods [:get]
                        :access-control-allow-headers ["Content-Type"])
             {:host "0.0.0.0" :port 3000 :join? false}))

(defn send-event [event-data]
  (go (>! events-chan (json/generate-string event-data))))

