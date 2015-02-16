(defproject tesla-zookeeper-observer "0.1.0"
            :description "Addon to https://github.com/otto-de/tesla-microservice to observe values in zookeeper."
            :url "https://github.com/otto-de/tesla-zookeeper-observer"
            :license {:name "Apache License 2.0"
                      :url  "http://www.apache.org/license/LICENSE-2.0.html"}
            :scm {:name "git"
                  :url  "https://github.com/otto-de/tesla-zookeeper-observer"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [com.stuartsierra/component "0.2.2"]
                           [de.otto/tesla-microservice "0.1.4"]

                           [zookeeper-clj "0.9.3"]

                           ;; logging
                           [org.clojure/tools.logging "0.3.0"]
                           [org.slf4j/slf4j-api "1.7.7"]
                           [ch.qos.logback/logback-core "1.1.2"]
                           [ch.qos.logback/logback-classic "1.1.2"]
                           [net.logstash.logback/logstash-logback-encoder "3.4"]]

            :source-paths ["src" "example/src"]
            :test-paths ["test" "example/test"]
            :main ^:skip-aot de.otto.tesla.zk.example.example-system

            )
