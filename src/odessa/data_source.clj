(ns odessa.data-source
  (:require
    [taoensso.timbre :as timbre]
    [odessa.indexer :refer :all]
    [odessa.loader :refer :all]))

(timbre/refer-timbre)

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
  :niorg
  :scotorg
  ;:wlhb
])

(defn fetcher [source]
  (p source (http-fetcher source)))

(defn build-index [[source data]]
  (p source (create-index extract-indexable-fields source data)))

(defn build-primary-keys [[source data]]
  (p source (create-primary-key extract-primary-key source data)))

(def init
  (memoize
    (fn [sources]
      (let [data  (profile :info :load-data    (load-data fetcher sources))
            pks   (profile :info :primary-keys (apply merge (pmap build-primary-keys data)))
            index (profile :info :build-index  (apply merge-indexes (pmap build-index data)))]
        { :data data :index index :pk pks}))))

(defn get-data []
  (:data (init sources)))

(defn get-index []
  (:index (init sources)))

(defn get-primary-keys []
  (:pk (init sources)))
