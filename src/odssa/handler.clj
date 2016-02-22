(ns odssa.handler
  (:require
    [clojure.data.json :as json]
    [compojure.route :as route]
    [compojure.handler :as handler]
    [compojure.core :refer [defroutes GET POST]]
    [ring.logger.timbre :as logger.timbre]
    [metrics.ring.expose :refer [expose-metrics-as-json]]
    [metrics.ring.instrument :refer [instrument]]
    [odssa.json :refer :all]
    [odssa.indexer :refer :all]
    [odssa.loader :refer :all]
    [clojure.set :as set]
    [clojure.string :as s]))

(def sources [
  :eauth
  :ecare
  :eccg
  :eccgsite
  :ecsu
  :enonnhs
  :eother
  :epraccur
  :etr
  :etrust
  ;:niorg
  :scotorg
  ;:wlhb

  ])

(def init
  (memoize
    (fn [sources]
      (let [data (load-data http-fetcher sources)
            index (apply
                    merge-indexes
                    (pmap #(apply create-index extract-indexable-fields %) data))]
        { :data data :index index }))))

(defn all [word]
  (let [index (:index (init sources))]
    (apply set/intersection (map index (trigrams word)))))

(defn handle-search [query]
  (let [words (s/split query #"\W")
        data (:data (init sources))]
    (->>
      words
      (map all)
      (reduce set/intersection)
      (map #(get-in data %))
      (map to-map))))

(defn limit-result-set [results start size]
  (let [end (min (count results) (+ start size))]
    {:results {
      :total-count (count results)
      :showing {
        :from start
        :to (max 0 (dec end)) }
      :data (->> results (drop start) (take size))}}))

(defn add-license-attribution [results]
  (assoc results
    :attribution [
    {
      :license "MIT"
      :title "ODS Search Appliance (c) Richard Hull 2016"
      :description "In-memory trigram inverted-indexes on HSCIC ODS data."
      :url "https://github.com/rm-hull/ods-search-application" }
    {
      :license "Open Government License"
      :title "Organisation Data Service, Health and Social Care Information Centre, licenced under the Open Government Licence v2.0"
      :description "ODS data is published under the Open Government Licence (OGL) and is openly available to everyone to use."
      :url "http://www.nationalarchives.gov.uk/doc/open-government-licence/version/2/"}]))

(defroutes app-routes
  (GET "/search" [:as req]
    (json-exception-handler
      (to-json identity
        (->
          (get-in req [:params :q])
          (handle-search)
          (limit-result-set
            (Integer/parseInt (get-in req [:params :offset] "0"))
            (Integer/parseInt (get-in req [:params :size] "20")))
          (add-license-attribution))))))

(def app
    (->
      app-routes
      (logger.timbre/wrap-with-logger)
      (expose-metrics-as-json)
      (instrument)
      (handler/api)))
