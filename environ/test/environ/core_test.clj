(ns environ.core-test
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
    ;; These three tests don't really test much unless you set the respective environment variables
    ;; you've got to do that by hand in your shell since Java doesn't have a System fn for that
    (is (= (:user e/env) (System/getenv "USER")))
    (is (= (:java-arch e/env) (System/getenv "JAVA_ARCH")))
    (is (= (get-in e/env [:fake-database :db]) (System/getenv "FAKE_DATABASE__DB"))
        "should support nested association"))
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
      (is (= (:foo-bar env) "baz"))))
  (testing "env file with nested association"
    (spit ".lein-env" (prn-str {:foo {:bar "baz"}}))
    (let [env (refresh-env)]
      (is (= (get-in env [:foo :bar]) "baz")))))
