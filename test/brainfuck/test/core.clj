(ns brainfuck.test.core
  (:require [brainfuck.core :as bfcore] :reload)
  (:require [brainfuck :as bf] :reload)
  (:use [clojure.test]))

(deftest bf-tokenize-spec
   (is (= (bf/tokenize "+-<>[].,")
          [\+ \- \< \> \[ \] \. \,])))

(deftest bf-parse-spec
   (is (= (bf/parse "+,-<>x[ ]_.")
          [:+ :in :- :< :> nil :loop-start nil :loop-end nil :.])))

(defn create-out-mock []
      (let [buffer (atom [])]
        [(fn [] @buffer)
         (fn [x] (swap! buffer conj x))]))

(defn create-in-stream [lst]
      (let [buffer (atom lst)]
        (fn [] (when (seq @buffer)
                 (let [x (first @buffer)]
                   (swap! buffer rest)
                   x)))))

(defn test-interpreter [source input expected-output]
      (let [in-f (create-in-stream input)
            [buffer out-f] (create-out-mock)]
        (bfcore/eval (bf/parse source) in-f out-f)
        (is (= (buffer) expected-output))))

(deftest bf-eval-test
   ;                 program:         input:   expected output:
   (test-interpreter "."               nil      [0])
   (test-interpreter ".."              nil      [0 0])
   (test-interpreter "+.."             nil      [1 1])
   (test-interpreter "+-."             nil      [0])
   (test-interpreter "+>++>+++><.<.<." nil      [3 2 1])
   (test-interpreter ",.-."            [5]      [5 4])
   (test-interpreter ",[.-]"           [5]      [5 4 3 2 1]))

(deftest hello-world-test
      (let [[buffer out-f] (create-out-mock)]
        (bfcore/eval (bf/parse "
 +++++ +++++             initialize counter (cell #0) to 10
 [                       use loop to set the next four cells to 70/100/30/10
     > +++++ ++              add  7 to cell #1
     > +++++ +++++           add 10 to cell #2 
     > +++                   add  3 to cell #3
     > +                     add  1 to cell #4
     <<<< -                  decrement counter (cell #0)
 ]                   
 > ++ .                  print 'H'
 > + .                   print 'e'
 +++++ ++ .              print 'l'
 .                       print 'l'
 +++ .                   print 'o'
 > ++ .                  print ' '
 << +++++ +++++ +++++ .  print 'W'
 > .                     print 'o'
 +++ .                   print 'r'
 ----- - .               print 'l'
 ----- --- .             print 'd'
 > + .                   print '!'
 > .                     print '\n' ") nil out-f)
        (is (= (->> (buffer)
                    (map #(str (char %)))
                    (apply str))
               "Hello World!\n"))))
