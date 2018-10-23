(ns environ.core
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(defn- keywordize [s]
  (-> (str/lower-case s)
      (str/replace "_" "-")
      (str/replace "." "-")
      (keyword)))

(defn- sanitize-key [k]
  (let [s (keywordize (name k))]
    (if-not (= k s) (println "Warning: environ key" k "has been corrected to" s))
    s))

(defn- sanitize-val [k v]
  (if (string? v)
    v
    (do (println "Warning: environ value" (pr-str v) "for key" k "has been cast to string")
        (str v))))

(defn- read-system-env []
  (->> (System/getenv)
       (map (fn [[k v]] [(keywordize k) v]))
       (into {})))

(defn- read-system-props []
  (->> (System/getProperties)
       (map (fn [[k v]] [(keywordize k) v]))
       (into {})))

(defn- read-env-reader [r]
  (when-let [reader (io/reader r)]
    (into {}
          (for [[k v] (edn/read-string (slurp r))]
            [(sanitize-key k) (sanitize-val k v)]))))

(defn- read-env-file [f]
  (if-let [env-file (io/file f)]
    (if (.exists env-file)
      (read-env-reader env-file))))

(defn- read-env-resource [r]
  (when-let [resource (io/resource r)]
    (read-env-reader resource)))
                      
(defn- warn-on-overwrite [ms]
  (doseq [[k kvs] (group-by key (apply concat ms))
          :let  [vs (map val kvs)]
          :when (and (next kvs) (not= (first vs) (last vs)))]
    (println "Warning: environ value" (first vs) "for key" k
             "has been overwritten with" (last vs))))

(defn- merge-env [& ms]
  (warn-on-overwrite ms)
  (apply merge ms))

(defonce ^{:doc "A map of environment variables."}
  env
  (merge-env
   (read-env-file ".lein-env")
   (read-env-resource ".boot-env")
   (read-system-env)
   (read-system-props)))
