(ns environ.core
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(defn- keywordize [s]
  (-> (str/lower-case s)
      (str/replace "_" "-")
      (str/replace "." "-")
      (keyword)))

(defn- read-system-env []
  (->> (System/getenv)
       (map (fn [[k v]] [(keywordize k) v]))
       (into {})))

(defn- read-system-props []
  (->> (System/getProperties)
       (map (fn [[k v]] [(keywordize k) v]))
       (into {})))

(defn- read-env-file []
  (let [env-file (io/file ".lein-env")]
    (if (.exists env-file)
      (read-string (slurp env-file)))))

(def ^{:doc "A map of environment variables."}
  env
  (merge
   (read-env-file)
   (read-system-props)
   (read-system-env)))
