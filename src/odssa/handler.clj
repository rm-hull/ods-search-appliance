(ns odssa.handler
  (:require
    [compojure.handler :as handler]
    [ring.logger.timbre :as logger.timbre]
    [metrics.ring.expose :refer [expose-metrics-as-json]]
    [metrics.ring.instrument :refer [instrument]]
    [odssa.data-source :as ds]
    [odssa.search :as search]))

(ds/get-index)

(def app
    (->
      search/routes
      (logger.timbre/wrap-with-logger)
      (expose-metrics-as-json)
      (instrument)
      (handler/api)))
