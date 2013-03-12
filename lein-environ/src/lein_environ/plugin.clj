(ns lein-environ.plugin
  (:use [robert.hooke :only (add-hook)])
  (:require [clojure.java.io :as io]
            leiningen.core.main))

(defn env-file [project]
  (io/file (:root project) ".lein-env"))

(defn- write-env-to-file [func task-name project args]
  (spit (env-file project) (prn-str (:env project {})))
  (func task-name project args))

(defn hooks []
  (add-hook #'leiningen.core.main/apply-task #'write-env-to-file))