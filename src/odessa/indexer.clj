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
     (repeat [key]))))

(defn cheap-concat [a b]
  (if (empty? b)
    a
    (recur
     (cons (first b) a)
     (next b))))

(def merge-indexes (partial merge-with cheap-concat))

(defn ^:private indexer [f data]
  (->>
   data
   (map-indexed list)
   (reduce f {})))

(defn create-index [extractor prefix data]
  (let [f (fn [index [n record]]
            (merge-indexes
             index
             (invert [prefix n] (extractor record))))]
    (indexer f data)))

(defn create-primary-key [extractor prefix data]
  (let [f (fn [index [n record]]
            (let [pk (extractor record)]
              (if (contains? index pk)
                (throw (IllegalStateException. (str "Duplicate primary key: " pk)))
                (assoc index (extractor record) [prefix n]))))]
    (indexer f data)))

(defn build-functor [term]
  (if (fn? term)
    term
    (fn [index]
      (->>
       (trigrams term)
       (map index)
       (map set)
       (apply set/intersection)))))

(def all-terms
  (memoize
   (fn [index]
     (->>
      (vals index)
      (map set)
      (reduce set/union)))))

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
