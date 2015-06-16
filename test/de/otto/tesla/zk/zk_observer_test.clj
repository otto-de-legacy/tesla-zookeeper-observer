(ns de.otto.tesla.zk.zk-observer-test
  (:require [clojure.test :refer :all]
            [zookeeper :as zk]
            [com.stuartsierra.component :as c]
            [de.otto.tesla.zk.zk-observer :as zko]
            [de.otto.tesla.zk.util.zk-inmem :as inmemzk]
            [de.otto.tesla.zk.util.test-utils :as u])
  (:import (org.apache.zookeeper KeeperException KeeperException$Code)))

(def connect-string "localhost:2184,localhost:2184")

(def test-system
  (dissoc
    (c/system-map :config {:config {:zookeeper-connect connect-string}}
                  :zookeeper (c/using (zko/new-zkobserver) [:config]))
    :server))

(defn overwrite [client path data]
  (let [v (zk/data client path)]
    (zk/set-data client path (.getBytes data) (:version (:stat v)))))

(deftest ^:unit should-return-an-observed-value-and-monitor-it-for-changes
  (u/with-started [started-zoo (inmemzk/map->InMemoryZooKeeper {})]
                  (Thread/sleep 50)                         ;waiting for the ZK to start
                  (u/with-started
                    [started test-system]
                    (with-open
                      [low-level-client (zk/connect connect-string)]
                      (let [zk-client (:zookeeper started)]
                        (zk/create low-level-client "/foo")

                        (overwrite low-level-client "/foo" "kaf")
                        (is (= (zko/observe! zk-client "/foo") "kaf"))

                        (overwrite low-level-client "/foo" "kaz")
                        (Thread/sleep 50)
                        (is (= (zko/observe! zk-client "/foo") "kaz"))

                        (overwrite low-level-client "/foo" "kam")
                        (Thread/sleep 50)
                        (is (= (zko/observe! zk-client "/foo") "kam"))

                        (overwrite low-level-client "/foo" "kan")
                        (Thread/sleep 50)
                        (is (= (zko/observe! zk-client "/foo") "kan")))))))

(deftest ^:unit should-return-nil-on-exception
  (with-redefs [zk/data (fn [_ _ _ _] (throw (KeeperException/create KeeperException$Code/NONODE "no node")))]
    (u/with-started [started-zoo (inmemzk/map->InMemoryZooKeeper {})]
                    (u/with-started [started test-system]
                                    (let [zk-client (:zookeeper started)]
                                      (is (= (zko/observe! zk-client "foo") nil)))))))