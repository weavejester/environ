(ns environ.leiningen.hooks
  (:use [robert.hooke :only (add-hook)])
  (:require [clojure.java.io :as io]
            leiningen.core.classpath))

(defn env-file [project]
  (io/file (:root project) ".env.clj"))

(defn- write-env-to-file [func dep-key project & args]
  (spit (env-file project)
        (prn-str (:env project {})))
  (apply func dep-key project args))

(defn activate []
  (add-hook #'leiningen.core.classpath/resolve-dependencies
            write-env-to-file))
