(ns odessa.indexer-test
  (:require
    [clojure.set :as set]
    [odessa.loader :as l]
    [odessa.indexer :as i]))

;(def test-data (l/load-data l/http-fetcher [:etrust :eccg :ecsu :etr :ecare :epraccur :scotorg]))
(def test-data (l/load-data l/http-fetcher [:etr]))

(def index
  (apply i/merge-indexes
    (pmap (fn [[ k v]] (i/create-index l/extract-indexable-fields k v)) test-data)))

(map
  #(l/to-map (get-in test-data %))
  (set/intersection
    (apply set/intersection (map index (i/trigrams "harrogate")))
    (apply set/intersection (map index (i/trigrams "york")))))


(zipmap [1 2 3] [:a :b :c])

