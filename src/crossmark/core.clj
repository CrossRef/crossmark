(ns crossmark.core
  (:require [config.core :refer [env]]
            [crossmark.handlers :as handlers]
            [org.httpkit.server :as server]
            [clojure.tools.logging :as log])
(:gen-class))

(defonce s (atom nil))

(defn -main
  [& args]
  (log/info "Start server on" (:port env))
  (server/run-server handlers/app {:port (Integer/parseInt (:port env)) :thread 10}))
