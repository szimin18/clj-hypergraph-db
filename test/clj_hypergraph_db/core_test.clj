(ns clj_hypergraph_db.core_test
  (:require [clojure.test :refer :all]
            [clj_hypergraph_db.core :refer :all]
            [clj_hypergraph_db.model :refer :all]
            [clj_hypergraph_db.parser :refer :all])
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


;;
;; test def-attribute function
;;
(deftest test-def-attribute
  (testing "Does (def-attribute :Abstract) contain :type key?"
      (is (contains? (def-attribute :Abstract) :type))))


;;
;; test merge-attributes function
;;
(deftest test-merge-attributes
  (testing "Does (merge-attributes '(with-attribute :Abstract)) returns a map with one mapping?"
      (is (= 1 (count (keys (merge-attributes '(with-attribute :Abstract))))))))


;;
;; test def-type function
;;
(deftest test-def-type
  (testing "Does (def-type :Domain (with-attribute :Abstract)) contains :type key?"
      (is (contains? (def-type :Domain (with-attribute :Abstract)) :type))))


;;
;; test parse function
;;
(deftest test-parse
  (testing "Check if first element of the parse result is a map?"
    (is (instance? clojure.lang.PersistentArrayMap
                   (first
                    (parse
                      (read-string (str "[" (slurp "configuration.clj") "]"))))))))


