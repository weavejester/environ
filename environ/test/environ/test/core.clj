(ns environ.test.core
  (:use clojure.test)
  (:require [environ.core :as e]))

(deftest test-env
  (testing "env variables"
    (is (= (:user e/env) (System/getenv "USER")))
    (is (= (:java-arch e/env) (System/getenv "JAVA_ARCH"))))
  (testing "system properties"
    (is (= (:user-name e/env) (System/getProperty "user.name")))
    (is (= (:user-country e/env) (System/getProperty "user.country"))))
  (testing "env file"
    (spit ".lein-env" (prn-str {:foo "bar"}))
    (let [env (e/refresh!)]
      (is (= (:foo env) "bar"))))
  (testing "env file with irregular keys"
    (spit ".lein-env" (prn-str {:foo.bar "baz"}))
    (let [env (e/refresh!)]
      (is (= (:foo-bar env) "baz")))))
