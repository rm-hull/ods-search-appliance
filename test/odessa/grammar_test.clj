(ns odessa.grammar-test
  (:require
    [jasentaa.parser :refer [parse-all]]
    [odessa.grammar :refer :all]
    [odessa.indexer :refer :all]
    [odessa.loader :refer :all]))

(parse-all search-expr "")
(parse-all search-expr "NOT hello ")
(parse-all search-expr "hello")
(parse-all search-expr " \"hello world\"")
(parse-all search-expr "hello world  eat ")
(parse-all search-expr "hello NOT world")
(parse-all search-expr " goodbye AND  world ")
(parse-all search-expr "hello OR goodbye AND world")
(parse-all search-expr "hello AND goodbye OR world")
(parse-all search-expr "( hello OR goodbye ) AND NOT ( world OR planet)")
(parse-all search-expr "hello you ")

(def test-data (load-data http-fetcher [:etr]))

(def index
  (apply merge-indexes
    (pmap (fn [[ k v]] (create-index extract-indexable-fields k v)) test-data)))

((parse-all search-expr "harrogate") index)
((parse-all search-expr "NOT (leeds OR york)") index)
(map
  #(to-map (get-in test-data %))
  ((parse-all search-expr "(NOT (leeds OR harrogate)) hospital") index) )

