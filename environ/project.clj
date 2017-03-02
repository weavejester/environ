(defproject environ "1.1.1"
  :description "Library for accessing environment variables"
  :url "https://github.com/weavejester/environ"
  :scm {:dir ".."}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :cljsbuild {:builds
              [{:id           "nodejs"
                :source-paths ["src"]
                :compiler     {:main          environ.core
                               :target        :nodejs
                               :output-dir    "target/nodejs"
                               :output-to     "target/nodejs/main.js"
                               :source-map    true
                               :optimizations :none}}]}
  :profiles {:provided {:plugins      [[lein-cljsbuild "1.1.5" :exclusions [[org.clojure/clojure]]]]
                        :dependencies [[org.clojure/clojurescript "1.9.229"]]}})
