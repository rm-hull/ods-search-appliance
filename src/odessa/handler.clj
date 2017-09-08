(ns odessa.handler
  (:require
   [compojure.handler :as handler]
   [ring.logger.timbre :as logger.timbre]
   [metrics.ring.expose :refer [expose-metrics-as-json]]
   [metrics.ring.instrument :refer [instrument]]
   [odessa.data-source :as ds]
   [odessa.search :as search]))

(def app
  (->
   search/routes
   (logger.timbre/wrap-with-logger)
   (expose-metrics-as-json)
   (instrument)
   (handler/api)))
