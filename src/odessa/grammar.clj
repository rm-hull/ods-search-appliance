(ns odessa.grammar
  (:require
    [jasentaa.monad :as m]
    [jasentaa.parser.basic :refer :all]
    [jasentaa.parser.combinators :refer :all]
    [odessa.indexer :refer :all]))

; searchTerm ::= [NOT] ( singleWord | quotedString | '(' searchExpr ')' )
; searchAnd ::= searchTerm [ AND searchTerm ]...
; searchExpr ::= searchAnd [ OR searchAnd ]...

(def digit (from-re #"[0-9]"))

(def letter (from-re #"[a-z]"))

(def alpha-num (any-of letter digit))

(declare search-expr)

(def single-word
  (m/do*
    spaces
    (word <- (plus alpha-num))
    spaces
    (m/return (apply str word))))

(def quoted-string
  (m/do*
    spaces
    (match "\"")
    (text <- (plus (any-of digit letter (match " "))))
    (match "\"")
    spaces
    (m/return (apply str text))))

(def bracketed-expr
  (m/do*
    (match "(")
    spaces
    (expr <- search-expr)
    spaces
    (match ")")
    (m/return expr)))

(def search-term
  (m/do*
    (neg <- (optional (m/do* (string "NOT") (plus space))))
    (term <- (any-of single-word quoted-string bracketed-expr))
    (m/return (if (empty? neg)
                (build-functor term)
                (negate (build-functor term))))))

(def search-and
  (m/do*
    (fst <- search-term)
    (rst <- (many (m/do* (optional (and-then (plus space) (string "AND"))) (plus space) search-term)))
    (m/return (if (empty? rst) fst (build-and-functor (cons fst rst))))))

(def search-expr
  (m/do*
    (fst <- search-and)
    (rst <- (many (m/do* (plus space) (string "OR") (plus space) search-and)))
    (m/return (if (empty? rst) fst (build-or-functor (cons fst rst))))))
