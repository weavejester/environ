(ns environ.core
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(defn- keywordize [s]
  (-> (str/lower-case s)
      (str/replace "_" "-")
      (str/replace "." "-")
      (keyword)))

(defn- sanitize [k]
  (let [s (keywordize (name k))]
    (if-not (= k s) (println "Warning: environ key " k " has been corrected to " s))
    s))

(defn- read-system-env []
  (->> (System/getenv)
       (map (fn [[k v]] [(keywordize k) v]))
       (into {})))

(defn- read-system-props []
  (->> (System/getProperties)
       (map (fn [[k v]] [(keywordize k) v]))
       (into {})))

(defn- read-env-file [f]
  (if-let [env-file (io/file f)]
    (if (.exists env-file)
      (into {} (for [[k v] (edn/read-string (slurp env-file))]
                 [(sanitize k) v])))))

(defonce ^{:doc "A map of environment variables."}
  env
  (merge
   (read-env-file ".lein-env")
   (read-env-file (io/resource ".boot-env"))
   (read-system-env)
   (read-system-props)))
