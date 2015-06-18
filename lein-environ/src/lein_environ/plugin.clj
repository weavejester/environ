(ns lein-environ.plugin
  (:use [robert.hooke :only (add-hook)])
  (:require [clojure.java.io :as io]
            leiningen.core.main))

(defn env-file [path]
  (io/file path ".lein-env"))

(defn- write-env-to-file [func task-name project args]
    (doseq [path (clojure.set/union #{(:root project)} (:also-write-to (:lein-environ project) []))]
      (spit (env-file path) (prn-str (:env project {}))))
  (func task-name project args))

(defn hooks []
  (add-hook #'leiningen.core.main/apply-task #'write-env-to-file))
