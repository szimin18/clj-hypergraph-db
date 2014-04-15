(ns clj_hypergraph_db.core_test
  (:require [clojure.test :refer :all]
            [clj_hypergraph_db.core :refer :all]
            [clj_hypergraph_db.model_parsing_functions :refer :all])
  (:use [clojure.tools.logging :only (info)]))


(defn setup
  []
  (println "Fix setup"))


(defn teardown
  []
  (println "Fix teardown"))


(defn test-wrapper
  [functions]
  (do
    (setup)
    (functions)
    (teardown)))


(use-fixtures :once test-wrapper)


(deftest a-test
  (testing "FIXME, I fail."
    (is (= {} (clj_hypergraph_db.model_parsing_functions/parse (load-file "configuration.clj"))))))
