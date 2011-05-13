# bf-clj

An implementation of the [Brainfuck programming language](http://en.wikipedia.org/wiki/Brainfuck) in Clojure.

## Usage

The simplest way to start using bf-clj is to include bf-clj as a dependency in your [leiningen](https://github.com/technomancy/leiningen) project file. bf-clj is [registered on clojars](http://clojars.org/bf-clj), so find the correct version number there.

Then you can use it like this:

    (require '[brainfuck :as bf])
    
    (bf/eval "++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.
              >+.+++++++..+++.>++.<<+++++++++++++++.>.+++.
              ------.--------.>+.>.")

    ; outputs Hello World! and a line break

Refer to the source and tests for more advanced usage, like how to create your own dialect of brainfuck using brainfuck.core.

## License

Copyright (C) 2011 tormaroe

Distributed under the Eclipse Public License, the same as Clojure.
