(ns odessa.indexer
  (:require
    [clojure.set :as set]
    [clojure.string :as s]
    [odessa.loader :as l]))

(defn ^:private partitions [text]
  (let [n (count text)]
    (map
      #(subs text % (+ % 3))
      (range 0 (- n 2)))))

(defn trigrams [text]
  (when text
    (->
      text
      s/upper-case
      (s/replace #"(\W|\s)+" " ")
      s/trim
      partitions)))

(defn invert [key index-terms]
  (let [xf (comp
             (map trigrams)
             (mapcat identity)
             (remove nil?))]
  (zipmap
    (into [] xf index-terms)
    (repeat #{ key }))))

(def merge-indexes (partial merge-with set/union))

(defn create-index [extractor prefix data]
  (let [f (fn [index [n record]]
            (merge-indexes
              index
              (invert [prefix n] (extractor record))))]
  (->>
    data
    (map-indexed list)
    (reduce f {}))))

(defn build-functor [term]
  (if (fn? term)
    term
    (fn [index]
      (apply set/intersection (map index (trigrams term))))))

(def all-terms
  (memoize
    (fn [index]
      (apply set/union (vals index)))))

(defn negate [functor]
  (fn [index]
    (set/difference
      (all-terms index)
      (functor index))))

(defn build-composite-functor [combiner functors]
  (fn [index]
    (apply combiner (map #(% index) functors))))

(def build-and-functor (partial build-composite-functor set/intersection))
(def build-or-functor (partial build-composite-functor set/union))
