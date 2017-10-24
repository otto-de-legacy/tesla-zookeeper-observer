(ns de.otto.tesla.zk.zk-observer
  (:require [zookeeper :as zk]
            [com.stuartsierra.component :as c]
            [clojure.tools.logging :as log]))

(defn- log-tag [{:keys [zk-name]}]
  (if zk-name
    (str "[" zk-name "]")
    ""))

(defn- watch! [self key event]
  (if (or (= (:event-type event) :NodeDataChanged) (nil? event))
    (try
      (let [client @(:client self)
            data (:data (zk/data client key :watcher (partial watch! self key)))]
        (log/debug (log-tag self) "Got" data "from zookeeper for" key ".")
        (swap! (:observed self) #(assoc % key data))
        data)
      (catch Exception e
        (log/error e (log-tag self) "Exception while contacting Zookeeper")))))

(defn- fetch-remote! [self key]
  (log/info (log-tag self) "Observing" key)
  (watch! self key nil))

(defn- re-register-watchers! [self]
  (doseq [k (keys @(:observed self))]
    (watch! self k nil)))

(defn zookeeper-connect-str [{:keys [config zk-name]}]
  (get-in config [:config (if zk-name
                            (keyword (str zk-name "-zookeeper-connect"))
                            :zookeeper-connect)]))

(defn- connect! [self]
  (when-let [connect-string (zookeeper-connect-str self)]
    (log/info (log-tag self) "Initializing connection to" connect-string ".")
    (zk/connect connect-string
                :watcher (fn [event]
                           (when (= :Expired (:keeper-state event))
                             (log/warn (log-tag self) "Connection expired:" event)
                             (Thread/sleep 2000)
                             (reset! (:client self) (connect! self))
                             (re-register-watchers! self))))))

(defprotocol KeyObserver
  (observe! [self key] [self key transform-fn]))

(defprotocol KeySetter
  (set! [self key val]))

(defrecord ZKObserver [config zk-name]
  c/Lifecycle
  (start [self]
    (log/info (log-tag self) "-> starting Zookeeper-Client.")
    (let [new-self (assoc self :observed (atom {})
                               :client (atom nil))]
      (reset! (:client new-self) (connect! new-self))
      new-self))

  (stop [self]
    (log/info (log-tag self) "<- stopping Zookeeper-Client.")
    (when-let [client @(:client self)]
      (log/info (log-tag self) "<- closing Zookeeper-Client-connection.")
      (zk/close client))
    self)

  KeyObserver
  (observe! [self key]
    (observe! self key (fn [val] (String. ^bytes val "UTF8"))))

  (observe! [self key transform-fn]
    (try
      (transform-fn (if-let [local-data (get @(:observed self) key)]
                      local-data
                      (fetch-remote! self key)))
      (catch Exception e
        (log/error e (log-tag self) "Value determined using zookeeper could not be transformed using given transformation-function, key:" key))))

  KeySetter
  (set! [self key val]
    (let [client @(:client self)
          old-version (-> (zk/data client key) :stat :version)]
      (zk/set-data client key (.getBytes val) old-version))))

(defn new-zkobserver
  ([] (map->ZKObserver {:zk-name nil}))
  ([name] (map->ZKObserver {:zk-name name})))
