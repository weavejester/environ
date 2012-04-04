# Environ

Environ is a Clojure library for managing environment settings from a
number of different sources.

In version 0.1.0, only one source has been implemented; environment
variables. These are taken and converted into idiomatic Clojure
keywords, lowercasing the names and converting "_" into "-".

## Installation

Include the following dependency in your `project.clj` file:

    [environ "0.1.0"]

## Usage

```clojure
(use 'environ.core)
(prn (:pwd env))
```

## License

Copyright © 2012 James Reeves

Distributed under the Eclipse Public License, the same as Clojure.
