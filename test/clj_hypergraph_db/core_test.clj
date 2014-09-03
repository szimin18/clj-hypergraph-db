(ns clj_hypergraph_db.core_test
  (:require [clojure.test :refer :all]
            [clj_hypergraph_db.core :refer :all])
  (:use [clojure.tools.logging :only (info)]))


;wrappers


(defn setup
  []
  (do
    (println "Fix setup")
    (clj_hypergraph_db.persistance.persistance_manager/hg-create "hgdbtest")))


(defn teardown
  []
  (do
    (println "Fix teardown")
    (clj_hypergraph_db.persistance.persistance_manager/hg-close)))


(defn test-wrapper
  [functions]
  (do
    (setup)
    (functions)
    (teardown)))


(use-fixtures :once test-wrapper)

