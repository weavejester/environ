(ns environ.core-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io])
  (:import [javax.naming InitialContext]))

(defn refresh-ns []
  (remove-ns 'environ.core)
  (dosync (alter @#'clojure.core/*loaded-libs* disj 'environ.core))
  (require 'environ.core))

(defn refresh-env []
  (refresh-ns)
  (let [ctx (InitialContext.)]
    (.destroySubcontext ctx "java:comp/env")
    (.unbind ctx "java:comp/env")
    (.unbind ctx "key1"))
  (let [ctx (InitialContext.)
        subctx (.createSubcontext ctx "java:comp/env")]
    (.bind ctx "key1" "value1")
    (.bind subctx "subkey1" "subvalue1"))
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

(deftest test-jndi
  (testing "jndi keyworded value"
    (let [env (refresh-env)]
      (is (= (:key1 env) "value1"))))
  (testing "jndi raw key"
    (let [env (refresh-env)]
      (is (= (get env "key1") "value1"))))
  (testing "jndi subcontext key"
    (let [env (refresh-env)]
      (is (= (:java:comp/env/subkey1 env) "subvalue1"))))
  (testing "jndi subcontext raw key"
    (let [env (refresh-env)]
      (is (= (get env "java:comp/env/subkey1") "subvalue1")))))
