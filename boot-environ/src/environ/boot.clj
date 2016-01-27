(ns environ.boot
  {:boot/export-tasks true}
  (:require [boot.core :as core]
            [environ.core :as environ]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(def ^:const env-file ".boot-env")

(defn- read-boot-env [fileset]
  (if-let [t (core/tmp-get fileset env-file)]
    (-> t core/tmp-file slurp edn/read-string)
    {}))

(defn- write-boot-env [env tmp]
  (spit (io/file tmp env-file) (prn-str env)))

(defn- update-boot-env
  [fileset tmp-dir env]
  (doto fileset
    (-> (read-boot-env) (merge env) (write-boot-env tmp-dir))
    (-> (core/add-source tmp-dir) (core/commit!))))

(core/deftask environ
  "Adds key-value pairs to the environment picked up by environ."
  [e env       FOO=BAR {kw str} "The environment map"]
  (let [tmp (core/tmp-dir!)]
    (fn environ-middleware [next-task]
      (fn environ-handler [fileset]
        (core/empty-dir! tmp)
        (with-redefs [environ/env (merge environ/env env)]
          (next-task (update-boot-env fileset tmp env)))))))
