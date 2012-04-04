(ns environ.core
  (:require [clojure.string :as str]))

(defn keywordize [s]
  (-> (str/lower-case s)
      (str/replace "_" "-")
      (keyword)))

(def env
  (->> (System/getenv)
       (map (fn [[k v]] [(keywordize k) v]))
       (into {})))
