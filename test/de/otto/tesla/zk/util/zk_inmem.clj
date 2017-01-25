(ns de.otto.tesla.zk.util.zk-inmem
  (:require [com.stuartsierra.component :as c]
            [clojure.tools.logging :as log])
  (:import (org.apache.zookeeper.server ZooKeeperServerMain ServerConfig)
           (org.apache.zookeeper.server.quorum QuorumPeerConfig)
           (java.util Properties)))

(def server-properties
  (doto (Properties.)
    (.putAll {"dataDir"    "/tmp"
              "dataLogDir" "/tmp"
              "clientPort" "2184"})))

(defn quorum-config [props]
  (let [quorum-config (QuorumPeerConfig.)]
    (.parseProperties quorum-config props)
    quorum-config))

(defn server-config [props]
  (let [server-config (ServerConfig.)]
    (.readFrom server-config (quorum-config props))
    server-config))

(defrecord InMemoryZooKeeper []
  c/Lifecycle
  (start [self]
    (let [server (proxy [ZooKeeperServerMain] []
                   (shutdown []
                     (proxy-super shutdown)))]
      (.start (Thread. (fn []
                         (log/info "Starting in memory ZooKeeper")
                         (.runFromConfig server (server-config server-properties)))))
      (assoc self :server server)))

  (stop [self]
    (let [server (:server self)]
      (log/info "Stopping in memory ZooKeeper")
      (.shutdown server))))
