(ns environ.core)

(def env
  (into {} (System/getenv)))
