(ns de.otto.tesla.zk.zk-observer-test
  (:require [clojure.test :refer :all]
            [zookeeper :as zk]
            [com.stuartsierra.component :as c]
            [clojure.string :as cs]
            [de.otto.tesla.zk.zk-observer :as zko]
            [de.otto.tesla.zk.util.zk-inmem :as inmemzk]
            [de.otto.tesla.zk.util.test-utils :as u])
  (:import (org.apache.zookeeper KeeperException KeeperException$Code)))

(def connect-string "localhost:2184,localhost:2184")

(defn test-system [runtime-conf]
  (dissoc
    (c/system-map :config {:config runtime-conf}
                  :zookeeper (c/using (zko/new-zkobserver) [:config])
                  :zookeeper-with-name (c/using (zko/new-zkobserver "zk-with-name") [:config]))
    :server))

(defn overwrite [client path data]
  (let [v (zk/data client path)]
    (zk/set-data client path (.getBytes data) (:version (:stat v)))))

(deftest ^:unit should-return-an-observed-value-and-monitor-it-for-changes
  (u/with-started [_ (inmemzk/map->InMemoryZooKeeper {})]
    (Thread/sleep 50)                                       ;waiting for the ZK to start
    (u/with-started
      [started (test-system {:zookeeper-connect connect-string})]
      (with-open
        [low-level-client (zk/connect connect-string)]
        (let [zk-client (:zookeeper started)]
          (zk/create low-level-client "/foo")

          (overwrite low-level-client "/foo" "kaf")
          (is (= "kaf"
                 (zko/observe! zk-client "/foo")))

          (overwrite low-level-client "/foo" "kaz")
          (Thread/sleep 50)
          (is (= "kaz"
                 (zko/observe! zk-client "/foo")))

          (overwrite low-level-client "/foo" "kam")
          (Thread/sleep 50)
          (is (= "kam"
                 (zko/observe! zk-client "/foo")))

          (overwrite low-level-client "/foo" "kan")
          (Thread/sleep 50)
          (is (= "kan"
                 (zko/observe! zk-client "/foo"))))))))

(defn as-str-with-leading-zero [^bytes val]
  (str "0" (String. val "UTF8")))

(defn as-reverse-str [^bytes val]
  (cs/reverse (String. val "UTF8")))

(deftest ^:unit should-return-an-observed-value-and-monitor-it-for-changes-using-a-transform-function
  (u/with-started [_ (inmemzk/map->InMemoryZooKeeper {})]
    (Thread/sleep 50)                                       ;waiting for the ZK to start
    (u/with-started [started (test-system {:zookeeper-connect connect-string})]
      (with-open
        [low-level-client (zk/connect connect-string)]
        (let [zk-client (:zookeeper started)]
          (zk/create low-level-client "/foo")

          (testing "should return an uncached transformed value"
            (overwrite low-level-client "/foo" "12345")
            (Thread/sleep 50)
            (is (= "54321"
                   (zko/observe! zk-client "/foo" as-reverse-str))))
          (testing "should return a cached value with different transformation"
            (is (= "012345"
                   (zko/observe! zk-client "/foo" as-str-with-leading-zero))))

          (testing "should return a refreshed transformed value"
            (overwrite low-level-client "/foo" "54321")
            (Thread/sleep 50)
            (is (= "054321"
                   (zko/observe! zk-client "/foo" as-str-with-leading-zero)))))))))

(deftest ^:unit should-return-nil-on-exception
  (with-redefs [zk/data (fn [_ _ _ _] (throw (KeeperException/create KeeperException$Code/NONODE "no node")))]
    (u/with-started [_ (inmemzk/map->InMemoryZooKeeper {})]
      (u/with-started [started (test-system {:zookeeper-connect connect-string})]
        (let [zk-client (:zookeeper started)]
          (is (= nil
                 (zko/observe! zk-client "foo"))))))))

(deftest ^:unit should-determine-zk-connect-string
  (with-redefs [zko/connect! (fn [_])]
    (u/with-started [started (test-system {:zookeeper-connect              "foo"
                                           :zk-with-name-zookeeper-connect "bar"})]
      (is (= "foo"
             (zko/zookeeper-connect-str (:zookeeper started))))
      (is (= "bar"
             (zko/zookeeper-connect-str (:zookeeper-with-name started)))))))

(deftest ^:unit set-value-test
  (u/with-started [_ (inmemzk/map->InMemoryZooKeeper {})]
    (Thread/sleep 50)                                       ;waiting for the ZK to start
    (u/with-started [started (test-system {:zookeeper-connect connect-string})]
      (with-open [low-level-client (zk/connect connect-string)]
        (let [zk-client (:zookeeper started)]
          (zk/create low-level-client "/foo")

          (testing "should set value that can be observed later"
            (overwrite low-level-client "/foo" "bar")
            (Thread/sleep 50)
            (is (= "bar" (zko/observe! zk-client "/foo")))
            (Thread/sleep 50)
            (zko/set! zk-client "/foo" "baz")
            (Thread/sleep 50)
            (is (= "baz" (zko/observe! zk-client "/foo")))
            (Thread/sleep 50)
            (zko/set! zk-client "/foo" "bam")
            (Thread/sleep 50)
            (is (= "bam" (zko/observe! zk-client "/foo")))))))))
