(defproject environ "1.0.2"
  :description "Library for accessing environment variables"
  :url "https://github.com/weavejester/environ"
  :scm {:dir ".."}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev {:dependencies [[org.eclipse.jetty/jetty-jndi "9.2.15.v20160210"]]}
             :test {:dependencies [[org.eclipse.jetty/jetty-jndi "9.2.15.v20160210"]]}})
