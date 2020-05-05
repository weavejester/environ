(defproject environ "1.2.0"
  :description "Library for accessing environment variables"
  :url "https://github.com/weavejester/environ"
  :scm {:dir ".."}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :profiles {:provided {:dependencies [[org.clojure/clojurescript "1.10.439"]]}}
  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-doo "0.1.10"]]
  :aliases
  {"ci" ["do"
         ["clean"]
         ["test"]
         ["doo" "node" "test-nodejs" "once"]
         ["doo" "nashorn" "test-nashorn" "once"]]}
  :cljsbuild
  {:builds
   [{:id "test-nashorn"
     :compiler
     {:main environ.test.runner
      :optimizations :simple
      :output-dir "target/test-nashorn"
      :output-to "target/environ.test.nashorn.js"}
     :source-paths ["src" "test"]}
    {:id "test-nodejs"
     :compiler
     {:main environ.test.runner
      :output-dir "target/test-nodejs"
      :output-to "target/environ.test.nodejs.js"
      :target :nodejs}
     :source-paths ["src" "test"]}]})
