(ns odessa.grammar
  (:require
    [jasentaa.monad :as m]
    [jasentaa.position :refer [strip-location]]
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
    (x <- (token (plus alpha-num)))
    (m/return (strip-location x))))

(def quoted-string
  (m/do*
    (symb "\"")
    (x <- (plus (any-of digit letter (match " "))))
    (symb "\"")
    (m/return (strip-location x))))

(def bracketed-expr
  (m/do*
    (symb "(")
    (expr <- search-expr)
    (symb ")")
    (m/return expr)))

(def search-term
  (m/do*
    (neg <- (optional (m/do* (symb "NOT") space)))
    (term <- (any-of single-word quoted-string bracketed-expr))
    (m/return (if (empty? neg)
                (build-functor term)
                (negate (build-functor term))))))

(def search-and
  (m/do*
    (fst <- search-term)
    (rst <- (many (m/do* (optional (symb "AND")) (plus space) search-term)))
    (m/return (if (empty? rst) fst (build-and-functor (cons fst rst))))))

(def search-expr
  (m/do*
    (fst <- search-and)
    (rst <- (many (m/do* (symb "OR") space search-and)))
    (m/return (if (empty? rst) fst (build-or-functor (cons fst rst))))))
