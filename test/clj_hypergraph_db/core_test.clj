(ns clj_hypergraph_db.core_test
  (:require [clojure.test :refer :all]
            [clj_hypergraph_db.core :refer :all]
            [clj_hypergraph_db.common_parser.common_config_parser :refer :all]
            [clj_hypergraph_db.hg_parser.hypergraph_config_parser :refer :all])
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
    (is (= {:from '(:Service) :to '(:Service)} (merge-attributes (from :Service) (to :Service))))))


(deftest test-parse-hg
  (testing "Testing parse function."
    (is (=
          '(nil {:type :type :name :Share :attributes {:classifier (:Abstract)}} {:type :type :name :Service} {:type :unnamed-link :name :RelatesTo :attributes {:to (:Service) :from (:Service)}})
          (clj_hypergraph_db.core/parse "((def-db :hypergraph) (def-type :Share (with-classifier :Abstract)) (def-type :Service) (def-unnamed-link :RelatesTo (from :Service) (to :Service)))")))))



(deftest test-parse-xml
  (testing "Testing parse function."
    (is (=
          '(nil {:type :token, :name "People", :attributes ({:type :token, :name "Person", :attributes ({:type :class, :name :Person, :attributes ({:type :field, :name :Name, :attributes ({:type :path, :name nil, :attributes ("Name" :Name-data)} {:type :type, :name :string})} {:type :field, :name :Surname, :attributes ({:type :path, :name nil, :attributes (:Surname)} {:type :type, :name :string})} {:type :pk, :name nil, :attributes (:Name :Surname)})} {:type :attribute, :name :Surname} {:type :token, :name "Name", :attributes ({:type :data, :name :Name-data})})})})
          (clj_hypergraph_db.core/parse "((def-db :xml) (def-token \"People\" (def-token \"Person\" (def-class :Person (def-field :Name :string (path \"Name\" :Name-data)) (def-field :Surname :string (path :Surname)) (primary-key :Name :Surname)) (def-attribute :Surname) (def-token \"Name\" (def-data :Name-data)))))")))))
