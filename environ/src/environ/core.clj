(ns environ.core
  (:require [clojure.string :as str]
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

(defn- read-env-file []
  (let [env-file (io/file ".lein-env")]
    (if (.exists env-file)
      (try
        (let [inpstr (read-string (slurp env-file))]
          (if (instance? clojure.lang.PersistentArrayMap inpstr)
            (into {} (for [[k v] inpstr]
                   [(sanitize k) v]))
            (throw (java.lang.RuntimeException. "input is not a Clojure map"))))
          (catch java.lang.RuntimeException e
            (throw (java.lang.IllegalArgumentException. (clojure.core/str "Invalid format for .lein-env : " (.getMessage e)))))
        ))))

(defonce ^{:doc "A map of environment variables."}
  env
  (merge
   (read-env-file)
   (read-system-env)
   (read-system-props)))
