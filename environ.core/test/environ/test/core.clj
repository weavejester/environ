(ns environ.test.core
  (:use clojure.test
        environ.core))

(deftest test-env
  (is (= (:user env) (System/getenv "USER")))
  (is (= (:java-arch env) (System/getenv "JAVA_ARCH"))))
