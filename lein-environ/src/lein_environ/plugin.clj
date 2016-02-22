(ns lein-environ.plugin
  (:use [robert.hooke :only (add-hook)])
  (:require [clojure.java.io :as io]
            leiningen.core.main))

(defn- as-edn [& args]
  (binding [*print-dup*    false
            *print-meta*   false
            *print-length* false
            *print-level*  false]
    (apply prn-str args)))

(defn env-file [project]
  (io/file (:root project) ".lein-env"))

(defn- write-env-to-file [func task-name project args]
  (spit (env-file project) (as-edn (:env project {})))
  (func task-name project args))

(defn hooks []
  (add-hook #'leiningen.core.main/apply-task #'write-env-to-file))
