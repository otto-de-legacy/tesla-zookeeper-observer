(ns de.otto.tesla.zk.util.test-utils
  (:require [com.stuartsierra.component :as comp]))

;; TODO: this is a dupe from tesla-microservice
(defmacro with-started
  "bindings => [name init ...]

  Evaluates body in a try expression with names bound to the values
  of the inits after (.start init) has been called on them. Finally
  a clause calls (.stop name) on each name in reverse order."
  [bindings & body]
  (if (and
        (vector? bindings) "a vector for its binding"
        (even? (count bindings)) "an even number of forms in binding vector")
    (cond
      (= (count bindings) 0) `(do ~@body)
      (symbol? (bindings 0)) `(let [~(bindings 0) (comp/start ~(bindings 1))]
                                (try
                                  (with-started ~(subvec bindings 2) ~@body)
                                  (finally
                                    (comp/stop ~(bindings 0)))))
      :else (throw (IllegalArgumentException.
                     "with-started-system only allows Symbols in bindings")))
    (throw (IllegalArgumentException.
             "not a vector or bindings-count is not even"))))

