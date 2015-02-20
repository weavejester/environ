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
        ;(finally {})
        ))))

(defn- read-env-file-orig []
  (let [env-file (io/file ".lein-env")]
    (if (.exists env-file)
      (into {} (for [[k v] (read-string (slurp env-file))]
                 [(sanitize k) v])))))

(comment
(defn fnx
  [f envinp]
  (try
    (do
  (spit ".lein-env" envinp)
    (f))
    (finally (io/delete-file ".lein-env")))
  )
  (fnx read-env-file {:foo "bar"})
  (fnx read-env-file-orig {:foo "bar"})

  (fnx read-env-file " ")
  (fnx read-env-file-orig " ")
  (fnx read-env-file "abc ")
  (fnx read-env-file-orig "abc ")
  (fnx read-env-file "")
  (fnx read-env-file-orig "")

(defn read-f
  [inp]
(try
  (let [inpstr (read-string (slurp inp))]
    (instance? clojure.lang.PersistentArrayMap inpstr)  )
  ;(for [[k v] (read-string (slurp "/home/kiran/.lein-env-test"))]
  ;                 [(sanitize k) v])
  (catch java.lang.RuntimeException e
    ;(str "got error " (.getMessage e)))
    (str "got error " ))
  (finally {})))

(read-f "/home/kiran/.lein-env-test")
(read-f "/home/kiran/.lein-env-empty")
(read-env-file)
  )
(defonce ^{:doc "A map of environment variables."}
  env
  (merge
   (read-env-file)
   (read-system-env)
   (read-system-props)))
