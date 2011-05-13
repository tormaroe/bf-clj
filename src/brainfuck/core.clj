(ns brainfuck.core
    "Core brainf*ck functionality. The public vals are: 
    
    *memory-size* => Set the amount of memory to use, defaults to 1000 cells.
    eval          => Runs a brainf*ck program, encoded as a list of keywords.
    parse         => Produces a program from brainf*ck source code.
    
    The core functionality is agnostic to the actual representation of brainf*ck
    source code. Eval uses a set of keywords as program tokens, and parse takes
    a tokenizer function and a hash-map mapping source tokens to program keywords
    to produce the program. This means that brainfuck.core can be used to parse
    and evaluate similar languages to brainf*ck, like Ook!"
    (:refer-clojure :exclude [eval]))

(letfn [(apply-cell [f m p]
          (assoc m p (f (nth m p))))]

  ; Functions to inc and dec memory m at pointer p
  (defn- bf+ [pp m p] [(inc pp) (apply-cell inc m p) p])
  (defn- bf- [pp m p] [(inc pp) (apply-cell dec m p) p]))

; Functions to move memory pointer p up or down
(defn- bf< [pp m p] [(inc pp) m (dec p)])
(defn- bf> [pp m p] [(inc pp) m (inc p)])

; Functions to write to or read form memory m at address p
(defn- bf-out [pp m p f] (f (nth m p)) [(inc pp) m p])
(defn- bf-in  [pp m p f] [(inc pp) (assoc m p (f)) p])

; Loop functions
(defn- bf-loop-start [c pp m p]
      [(if (zero? (nth m p))
         ((fn [pp]
              (if (= (nth c pp) :loop-end)
                pp
                (recur (inc pp)))) (inc pp))
         (inc pp)) 
       m p])

(defn- bf-loop-end [c pp m p]
      [(if (zero? (nth m p))
         (inc pp)
         ((fn [pp]
              (if (= (nth c pp) :loop-start)
                pp
                (recur (dec pp)))) pp)) 
       m p])

(defn- create-memory [size]
      (vec (take size (iterate identity 0))))

(def *memory-size* 1000)

(defn eval 
      "Program needs to be a sequence of brainf*ck program symbols. 
      Unrecognized symbols will just be skipped.
      
      in-f is a function taking no parameters, returning a single 
      input integer value when needed (probably prompting the user).
      
      out-f is a function taking one parameter, a memory value to 
      output (to the user, probably as a character)."
      [program in-f out-f] 
      (loop [p-pointer 0, memory (create-memory *memory-size*), pointer 0]
        (if (< p-pointer (count program))
          (let [[p-pointer memory pointer] (case (nth program p-pointer)
                                             ; Operator table
                                             :+ (bf+ p-pointer memory pointer)
                                             :- (bf- p-pointer memory pointer)
                                             :< (bf< p-pointer memory pointer)
                                             :> (bf> p-pointer memory pointer)
                                             :. (bf-out p-pointer memory pointer out-f)
                                             :in (bf-in p-pointer memory pointer in-f)
                                             :loop-start (bf-loop-start program p-pointer memory pointer)
                                             :loop-end (bf-loop-end program p-pointer memory pointer)
                                             [(inc p-pointer) memory pointer])]
            (recur p-pointer memory pointer))
          (println))))

(defn parse 
      "source will be sent to tokenizer, which will produce a
      sequence of source code tokens. ops is a hash-map with
      source code tokens as keys and brainf*ck program tokens
      as the corresponding values.
      
      The recogized brainf*ck program tokens are:
        :+
        :-
        :<
        :>
        :.
        :in
        :loop-start
        :loop-end"
      [source tokenizer ops]
      (map (partial get ops) 
           (tokenizer source)))

