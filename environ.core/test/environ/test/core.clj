(ns environ.test.core
  (:use clojure.test
        environ.core))

(deftest test-env
  (testing "env variables"
    (is (= (:user env) (System/getenv "USER")))
    (is (= (:java-arch env) (System/getenv "JAVA_ARCH"))))
  (testing "env file"
    (spit ".lein-env" (prn-str {:foo "bar"}))
    (use 'environ.core :reload)
    (is (= (:foo env) "bar"))))
