(ns clj_hypergraph_db.core_test
  (:require [clojure.test :refer :all]
            [clj_hypergraph_db.core :refer :all]
            [clj_hypergraph_db.hypergraph_model_parsing_functions :refer :all])
  (:use [clojure.tools.logging :only (info)]))


;wrappers


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


;tests


(deftest merge-attributes-test
  (testing "Testing merge attributes function."
    (is (= {:from '(:Service) :to '(:Service)} (clj_hypergraph_db.adaptive_model_parsing_functions/merge-attributes (from :Service) (to :Service))))))


(deftest test-parse
  (testing "Testing parse function."
    (is (=
          '({:type :type :name :Share :attributes {:classifier (:Abstract)}} {:type :type, :name :Service} {:type :unnamed-link :name :RelatesTo :attributes {:to (:Service) :from (:Service)}})
          (clj_hypergraph_db.core/parse "((def-type :Share (with-classifier :Abstract)) (def-type :Service) (def-unnamed-link :RelatesTo (from :Service) (to :Service)))")))))
