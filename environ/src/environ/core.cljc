(ns environ.core
  (:require #?(:clj [clojure.edn :as edn] :cljs [cljs.reader :as edn])
            #?(:clj [clojure.java.io :as io])
            #?(:cljs [goog.object :as obj])
            [clojure.string :as str]))

#?(:cljs (def ^:private nodejs?
           (exists? js/require)))

#?(:cljs (def ^:private fs
           (when nodejs? (js/require "fs"))))

#?(:cljs (def ^:private process
           (when nodejs? (js/require "process"))))

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
  (->> #?(:clj (System/getenv)
          :cljs (zipmap (obj/getKeys (.-env process))
                        (obj/getValues (.-env process))))
       (map (fn [[k v]] [(keywordize k) v]))
       (into {})))

#?(:clj (defn- read-system-props []
          (->> (System/getProperties)
               (map (fn [[k v]] [(keywordize k) v]))
               (into {}))))

(defn- slurp-file [f]
  #?(:clj (when-let [f (io/file f)]
            (when (.exists f)
              (slurp f)))
     :cljs (when ^js (.existsSync fs f)
             (str ^js (.readFileSync fs f)))))

(defn- read-env-file [f]
  (when-let [content (slurp-file f)]
    (into {} (for [[k v] (edn/read-string content)]
               [(sanitize-key k) (sanitize-val k v)]))))

(defn- warn-on-overwrite [ms]
  (doseq [[k kvs] (group-by key (apply concat ms))
          :let  [vs (map val kvs)]
          :when (and (next kvs) (not= (first vs) (last vs)))]
    (println "Warning: environ value" (first vs) "for key" k
             "has been overwritten with" (last vs))))

(defn- merge-env [& ms]
  (warn-on-overwrite ms)
  (apply merge ms))

(defn- read-env []
  #?(:clj (merge-env
           (read-env-file ".lein-env")
           (read-env-file (io/resource ".boot-env"))
           (read-system-env)
           (read-system-props))
     :cljs (if nodejs?
             (merge-env
              (read-env-file ".lein-env")
              (read-system-env))
             {})))

(defonce ^{:doc "A map of environment variables."}
  env (read-env))
