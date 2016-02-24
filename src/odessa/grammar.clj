(ns odessa.grammar
  (:require
    [odessa.parser :refer :all]
    [odessa.indexer :refer :all]))

; searchTerm ::= [NOT] ( singleWord | quotedString | '(' searchExpr ')' )
; searchAnd ::= searchTerm [ AND searchTerm ]...
; searchExpr ::= searchAnd [ OR searchAnd ]...

(def digit (from-re #"[0-9]"))

(def letter (from-re #"[a-z]"))

(def alpha-num (any-of letter digit))

(declare search-expr)

(def single-word
  (do*
    spaces
    (word <- (plus alpha-num))
    spaces
    (return (apply str word))))

(def quoted-string
  (do*
    spaces
    (match "\"")
    (text <- (plus (any-of digit letter (match " "))))
    (match "\"")
    spaces
    (return (apply str text))))

(def bracketed-expr
  (do*
    (match "(")
    spaces
    (expr <- search-expr)
    spaces
    (match ")")
    (return expr)))

(def search-term
  (do*
    (neg <- (optional (do* (string "NOT") (plus space))))
    (term <- (any-of single-word quoted-string bracketed-expr))
    (return (if (empty? neg)
              (build-functor term)
              (negate (build-functor term))))))

(def search-and
  (do*
    (fst <- search-term)
    (rst <- (many (do* (optional (and-then (plus space) (string "AND"))) (plus space) search-term)))
    (return (if (empty? rst) fst (build-and-functor (cons fst rst))))))

(def search-expr
  (do*
    (fst <- search-and)
    (rst <- (many (do* (plus space) (string "OR") (plus space) search-and)))
    (return (if (empty? rst) fst (build-or-functor (cons fst rst))))))
