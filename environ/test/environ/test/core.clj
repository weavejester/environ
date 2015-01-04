(ns environ.test.core
  (:require [clojure.test :refer :all]
            [environ.core :as e]))

(defn refresh-ns []
  (ns-unalias *ns* 'e)
  (remove-ns 'environ.core)
  (dosync (alter @#'clojure.core/*loaded-libs* disj 'environ.core))
  (require '[environ.core :as e]))

(defn refresh-env []
  (eval `(do (refresh-ns) e/env)))

(deftest test-env
  (testing "env variables"
    (is (= (:user e/env) (System/getenv "USER")))
    (is (= (:java-arch e/env) (System/getenv "JAVA_ARCH"))))
  (testing "system properties"
    (is (= (:user-name e/env) (System/getProperty "user.name")))
    (is (= (:user-country e/env) (System/getProperty "user.country"))))
  (testing "env file"
    (spit ".lein-env" (prn-str {:foo "bar"}))
    (let [env (refresh-env)]
      (is (= (:foo env) "bar"))))
  (testing "env file with irregular keys"
    (spit ".lein-env" (prn-str {:foo.bar "baz"}))
    (let [env (refresh-env)]
      (is (= (:foo-bar env) "baz")))))
