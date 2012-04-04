(ns environ.core
  (:require [clojure.string :as str]))

(defn- keywordize [s]
  (-> (str/lower-case s)
      (str/replace "_" "-")
      (keyword)))

(defn- read-system-env []
  (->> (System/getenv)
       (map (fn [[k v]] [(keywordize k) v]))
       (into {})))

(def ^{:doc "A map of environment variables."}
  env
  (read-system-env))
