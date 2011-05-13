(ns brainfuck
    "An implementation of the brainf*ck programming language.
    Delegates most of its work to brainfuck.core."
    (:refer-clojure :exclude [eval spit slurp])
    (:require [brainfuck.core :as bfcore]))

(defn tokenize [source]
      (map identity source))

; The brainf*ck operator characters mapped to the
; keywords used in the operator table in bf-eval.
; A different ops mapping can be used to change
; the language to something like Ook!
(def ops {\+ :+, \- :-, 
          \< :<, \> :>, 
          \. :., \, :in
          \[ :loop-start, \] :loop-end})

(defn parse [source]
      (bfcore/parse source tokenize ops))

(defn spit [x] (-> x char str print))
(defn slurp [] (.read *in*))

(defn eval
      "Main, default brainf*ck interpreter."
      [source]
      (bfcore/eval (parse source) slurp spit))

(defn repl []
      (print "BRAINF*CK=> ")
      (flush)
      (bfcore/eval (parse (read-line)) slurp spit)
      (recur))



