(ns odessa.search
  (:require
    [clojure.set :as set]
    [clojure.string :as s]
    [clojure.data.json :as json]
    [compojure.route :as route]
    [compojure.core :refer [defroutes GET POST]]
    [jasentaa.parser :refer [parse-all]]
    [odessa.json :refer :all]
    [odessa.data-source :as ds]
    [odessa.grammar :refer [search-expr]]
    [odessa.loader :refer [to-map]]))

(defn fetch [data]
  (fn [location]
    (if-let [raw-record (and location (get-in data location))]
      (assoc (to-map raw-record) :source location)
      (throw (IllegalArgumentException. "No record found")))))

(defn handle-search [query]
  (let [data (ds/get-data)
        index (ds/get-index)
        search (parse-all search-expr query)]
    (if (nil? search)
      (throw (IllegalArgumentException. "Unable to parse query"))
      (map (fetch data) (search index)))))

(defn limit-result-set [results start size query]
  (let [end (min (count results) (+ start size))]
    {:query query
     :results {
      :total-count (count results)
      :showing {
        :from start
        :to (max 0 (dec end)) }
      :data (->> results (drop start) (take size))}}))

(defn lookup-by-location [location]
  (let [data (ds/get-data)]
    {:query location
     :data ((fetch data) location)}))

(defn lookup-by-org-code [id]
  (let [locn (get (ds/get-primary-keys) id)]
    (assoc (lookup-by-location locn) :query id)))

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
  (GET "/organisation-code/:id" [id]
    (json-exception-handler
      (to-json identity
        (->>
          (lookup-by-org-code id)
          (add-license-attribution)))))

  (GET "/source/:prefix/:index" [prefix index]
    (json-exception-handler
      (to-json identity
        (->>
          (lookup-by-location [(keyword prefix) (Integer/parseInt index)])
          (add-license-attribution)))))

  (GET "/search" [:as req]
    (json-exception-handler
      (to-json identity
        (->
          (get-in req [:params :q])
          (handle-search)
          (limit-result-set
            (Integer/parseInt (get-in req [:params :offset] "0"))
            (Integer/parseInt (get-in req [:params :size] "20"))
            (get-in req [:params :q]))
          (add-license-attribution))))))
