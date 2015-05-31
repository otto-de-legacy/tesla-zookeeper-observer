(defproject de.otto/tesla-zookeeper-observer "0.1.1"
            :description "Addon to https://github.com/otto-de/tesla-microservice to observe values in zookeeper."
            :url "https://github.com/otto-de/tesla-zookeeper-observer"
            :license {:name "Apache License 2.0"
                      :url  "http://www.apache.org/license/LICENSE-2.0.html"}
            :scm {:name "git"
                  :url  "https://github.com/otto-de/tesla-zookeeper-observer"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [de.otto/tesla-microservice "0.1.12"]
                           [zookeeper-clj "0.9.3"]]

            :source-paths ["src" "example/src"]
            :test-paths ["test" "example/test"]
            :main ^:skip-aot de.otto.tesla.zk.example.example-system)
