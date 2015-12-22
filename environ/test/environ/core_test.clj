(ns environ.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]))

(defn refresh-ns []
  (remove-ns 'environ.core)
  (dosync (alter @#'clojure.core/*loaded-libs* disj 'environ.core))
  (require 'environ.core))

(defn refresh-env []
  (refresh-ns)
  (var-get (find-var 'environ.core/env)))

(deftest test-env
  (.delete (io/file ".lein-env"))
  (testing "env variables"
    (let [env (refresh-env)]
      (is (= (:user env) (System/getenv "USER")))
      (is (= (:java-arch env) (System/getenv "JAVA_ARCH")))))
  (testing "system properties"
    (let [env (refresh-env)]
      (is (= (:user-name env) (System/getProperty "user.name")))
      (is (= (:user-country env) (System/getProperty "user.country")))))
  (testing "env file"
    (spit ".lein-env" (prn-str {:foo "bar"}))
    (let [env (refresh-env)]
      (is (= (:foo env) "bar"))))
  (testing "env file with irregular keys"
    (spit ".lein-env" (prn-str {:foo.bar "baz"}))
    (let [env (refresh-env)]
      (is (= (:foo-bar env) "baz"))))
  (testing "env file with irregular keys"
    (spit ".lein-env" "{:foo #=(str \"bar\" \"baz\")}")
    (is (thrown? RuntimeException (refresh-env))))
  (testing "env file with non-string values"
    (spit ".lein-env" (prn-str {:foo 1 :bar :baz}))
    (let [env (refresh-env)]
      (is (= (:foo env) "1"))
      (is (= (:bar env) ":baz")))))
