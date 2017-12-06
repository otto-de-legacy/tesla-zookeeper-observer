(defproject de.otto/tesla-zookeeper-observer "0.2.2-SNAPSHOT"
  :description "Addon to https://github.com/otto-de/tesla-microservice to observe values in zookeeper."
  :url "https://github.com/otto-de/tesla-zookeeper-observer"
  :license {:name "Apache License 2.0"
            :url  "http://www.apache.org/license/LICENSE-2.0.html"}
  :scm {:name "git"
        :url  "https://github.com/otto-de/tesla-zookeeper-observer"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [zookeeper-clj "0.9.4"]
		 [org.apache.zookeeper/zookeeper "3.4.11"]
		 [io.netty/netty "3.10.6.Final"]
                 [org.clojure/tools.logging "0.4.0"]
                 [com.stuartsierra/component "0.3.2"]]
  :lein-release {:deploy-via :clojars}
  :profiles {:dev      {:dependencies [[org.slf4j/slf4j-api "1.7.25"]
                                       [ch.qos.logback/logback-core "1.2.3"]
                                       [ch.qos.logback/logback-classic "1.2.3"]]
                        :plugins      [[lein-release "1.0.9"]]}})

