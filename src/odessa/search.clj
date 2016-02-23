(ns odessa.search
  (:require
    [clojure.set :as set]
    [clojure.string :as s]
    [clojure.data.json :as json]
    [compojure.route :as route]
    [compojure.core :refer [defroutes GET POST]]
    [odessa.json :refer :all]
    [odessa.data-source :as ds]
    [odessa.indexer :as i]
    [odessa.loader :as l]))

(defn all [word]
  (let [index (ds/get-index)]
    (apply set/intersection (map index (i/trigrams word)))))

(defn fetch [data location]
  (let [raw-record (get-in data location)]
    (assoc (l/to-map raw-record) :source location)))

(defn handle-search [query]
  (let [words (s/split query #"\W")
        data (ds/get-data)]
    (->>
      words
      (map all)
      (reduce set/intersection)
      (map (partial fetch data)))))

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

(defroutes routes
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
