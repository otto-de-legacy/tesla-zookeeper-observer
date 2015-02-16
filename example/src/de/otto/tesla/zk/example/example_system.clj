(ns de.otto.tesla.zk.example.example-system
  (:require [de.otto.tesla.system :as system]
            [de.otto.tesla.zk.zk-observer :as observer]
            [de.otto.tesla.zk.example.example-page :as example-page]
            [com.stuartsierra.component :as c])
  (:gen-class))

(defn example-system [runtime-config]
  (-> (system/empty-system (assoc runtime-config :name "example-zk-service"))
      (assoc :zk-observer
             (c/using (observer/new-zkobserver) [:config]))
      (assoc :example-page
             (c/using (example-page/new-example-page) [:routes :zk-observer :app-status]))
      (c/system-using {:server [:example-page]})))

(defn -main
  "starts up the production system."
  [& args]
  (system/start-system (example-system {})))