(ns lein-environ.plugin
  (:use [robert.hooke :only (add-hook)])
  (:require leiningen.core.main
            leiningen.core.eval
            [clojure.string :as str]))

(def last-env (atom nil))

(defn- reverse-keywordize [s]
  (-> (name s)
      (str/upper-case)
      (str/replace "-" "_")))

(defn- generate-variable-setup-code [[key value]]
  `(System/setProperty ~key ~value))

(defn- generate-env-setup-code [env]
  (map generate-variable-setup-code env))

(defn- safe-env [env]
  (into {} (map (fn [[key value]] [(reverse-keywordize key) value]) env)))

(defn- setup-env! [func project form]
  ; we cannot use project here to retrieve :env because it has been already filtered out prior to passing to shell-command
  (let [env (safe-env @last-env)
        form-with-env-setup `(do
                               ~@(generate-env-setup-code env)
                               ~form)]
    (func project form-with-env-setup)))

(defn- capture-env! [func task-name project args]
  ; apply-task is a suitable place where we can capture full project information (with merged profiles)
  ; we capture collected :env map here for later use when spawning a java compiltion via shell-command
  (reset! last-env (:env project))
  (func task-name project args))

(defn hooks []
  (add-hook #'leiningen.core.main/apply-task #'capture-env!)
  (add-hook #'leiningen.core.eval/shell-command #'setup-env!))