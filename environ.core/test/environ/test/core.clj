(ns environ.test.core
  (:use clojure.test
        environ.core))

(deftest test-env
  (is (= (env "USER") (System/getenv "USER"))))