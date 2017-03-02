(ns environ.core
  #?(:clj
     (:require [clojure.edn :as edn]
               [clojure.java.io :as io]))
  (:require [clojure.string :as str])
  #?(:cljs
     (:require-macros [cljs.core :refer [exists?]])))

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
  #?(:clj  (->> (System/getenv)
                (map (fn [[k v]] [(keywordize k) v]))
                (into {}))
     :cljs (when (exists? js/process)
             (->> js/process.env
                  js/Object.keys
                  js->clj
                  (map (fn [k] [(keywordize k) (aget js/process.env k)]))
                  (into {})))))

(defn- read-system-props []
  #?(:clj (->> (System/getProperties)
               (map (fn [[k v]] [(keywordize k) v]))
               (into {}))))

(defn- read-env-file [f]
  #?(:clj (if-let [env-file (io/file f)]
            (if (.exists env-file)
              (into {} (for [[k v] (edn/read-string (slurp env-file))]
                         [(sanitize-key k) (sanitize-val k v)]))))))

(def ^{:doc "A map of environment variables."}
  env
  (merge
    (read-env-file ".lein-env")
    (read-env-file #?(:clj  (io/resource ".boot-env")
                      :cljs nil))
    (read-system-env)
    (read-system-props)))