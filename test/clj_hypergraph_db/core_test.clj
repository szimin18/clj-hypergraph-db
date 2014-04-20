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


(deftest basic-test
  (testing "FIXME, I fail."
    (is (= {} (clj_hypergraph_db.model_parsing_functions/merge-attributes {:a (list :b)} {:a (list :b)} {:a (list :c)} {:b (list :c :b :a)})))))


(deftest test1
  (testing "FIXME, I fail."
    (is (= {} (clj_hypergraph_db.core/parse (str "(" (slurp "configuration.clj") ")"))))))

;(reduce (fn [attribute-one attribute-two] (reduce (fn [previous-map key-to-merge-in] (assoc previous-map key-to-merge-in (merge (previous-map key-to-merge-in) (attribute-one key-to-merge-in)))) (merge attribute-one attribute-two) (filter #(contains? (keys attribute-one) %) (keys attribute-two)))) {} (list {:a (list :b)} {:a (list :b)} {:a (list :c)} {:b (list :c :b :a)}))