(ns odssa.indexer-test
  (:require
    [clojure.set :as set]
    [clojure.string :as s]
    [odssa.loader :as l]
    [odssa.indexer :as i]))

;(def test-data (l/load-data l/http-fetcher [:etrust :eccg :ecsu :etr :ecare :epraccur :scotorg]))
(def test-data (l/load-data l/http-fetcher [:etr]))

(def index
  (apply i/merge-indexes
    (pmap (fn [[ k v]] (i/create-index l/extract-indexable-fields k v)) test-data)))

(map
  #(l/to-json (get-in test-data %))
  (set/intersection
    (apply set/intersection (map index (i/trigrams "bannatyne")))
    (apply set/intersection (map index (i/trigrams "harrogate")))
    (apply set/intersection (map index (i/trigrams "york")))))
