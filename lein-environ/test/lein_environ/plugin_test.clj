(ns lein-environ.plugin-test
  (:require [clojure.test :refer :all]
            [lein-environ.plugin :as l]))

(deftest replace-project-keyword-test
  (are [expected project] (= expected (l/read-env project))
    {:app-version "1.0.0", :test-version :version}
    {:version "1.0.0", :env {:app-version :project/version :test-version :version}}

    {:foo "bar"}
    {:env {:foo "bar"}}))
