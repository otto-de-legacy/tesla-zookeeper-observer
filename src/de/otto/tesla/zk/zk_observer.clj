(ns de.otto.tesla.zk.zk-observer
  (:require [zookeeper :as zk]
            [com.stuartsierra.component :as c]
            [clojure.tools.logging :as log])
  (:import (org.apache.zookeeper KeeperException)))

(defn- watch! [self key event]
  (if (or (= (:event-type event) :NodeDataChanged) (nil? event))
    (try
      (let [client @(:client self)
            data (String. (:data (zk/data client key :watcher (partial watch! self key))) "UTF-8")]
        (log/debug "Got  " data " from zookeeper for " key ".")
        (swap! (:observed self) #(assoc % key data))
        data)
      (catch KeeperException e
        (log/error e "Exception while contacting Zookeeper")))))

(defn- fetch-remote! [self key]
  (log/info "Observing " key)
  (watch! self key nil))

(defn- re-register-watchers! [self]
  (doseq [k (keys @(:observed self))]
    (watch! self k nil)))

(defn- connect! [self]
  (when-let [connect-string (get-in self [:config :config :zookeeper-connect])]
    (log/info "Initializing connection to " connect-string ".")
    (zk/connect connect-string
                :watcher (fn [event]
                           (when (= :Expired (:keeper-state event))
                             (log/warn "Connection expired: " event)
                             (reset! (:client self) (connect! self))
                             (re-register-watchers! self))))))

(defprotocol KeyObserver
  (observe! [self key]))

(defrecord ZKObserver []
  c/Lifecycle
  (start [self]
    (log/info "-> starting Zookeeper-Client.")
    (let [new-self (assoc self :observed (atom {})
                               :client (atom nil))]
      (reset! (:client new-self) (connect! new-self))
      new-self))

  (stop [self]
    (log/info "<- stopping Zookeeper-Client.")
    (when-let [client @(:client self)]
      (log/info "<- closing Zookeeper-Client-connection.")
      (zk/close client))
    self)

  KeyObserver
  (observe! [self key]
    (if-let [local-data (get @(:observed self) key)]
      local-data
      (fetch-remote! self key))))

(defn new-zkobserver [] (map->ZKObserver {}))
