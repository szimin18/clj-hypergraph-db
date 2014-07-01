(ns clj_hypergraph_db.core_test
  (:require [clojure.test :refer :all]
            [clj_hypergraph_db.core :refer :all])
  (:use [clojure.tools.logging :only (info)]))


;wrappers


(defn setup
  []
  (do
    (println "Fix setup")
    (clj_hypergraph_db.persistance.persistance_manager/create-database "hgdbtest")))


(defn teardown
  []
  (do
    (println "Fix teardown")
    (clj_hypergraph_db.persistance.persistance_manager/close-database)))


(defn test-wrapper
  [functions]
  (do
    (setup)
    (functions)
    (teardown)))


(use-fixtures :once test-wrapper)


;tests


(deftest test-parse-hg
  (testing "Testing parse function."
    (is (=
          '(nil {:name :Share, :type :type, :attributes ({:classifier (:Abstract)})} {:name :Service, :type :type} {:name :RelatesTo, :type :unnamed-link, :attributes ({:from (:Service)} {:to (:Service)})})
          (clj_hypergraph_db.core/parse "((def-db :hypergraph) (def-type :Share (with-classifier :Abstract)) (def-type :Service) (def-unnamed-link :RelatesTo (from :Service) (to :Service)))")))))



(deftest test-parse-xml
  (testing "Testing parse function."
    (is (=
          '(nil {:type :token, :name "People", :attributes ({:type :token, :name "Person", :attributes ({:type :class, :name :Person, :attributes ({:type :field, :name :Name, :attributes ({:type :path, :attributes ("Name" :Name-data)} {:type :type, :name :string})} {:type :field, :name :Surname, :attributes ({:type :path, :attributes (:Surname)} {:type :type, :name :string})} {:type :pk, :attributes (:Name :Surname)})} {:type :attribute, :name :Surname} {:type :token, :name "Name", :attributes ({:type :data, :name :Name-data})})})})
          (clj_hypergraph_db.core/parse "((def-db :xml) (def-token \"People\" (def-token \"Person\" (def-class :Person (def-field :Name :string (path \"Name\" :Name-data)) (def-field :Surname :string (path :Surname)) (primary-key :Name :Surname)) (def-attribute :Surname) (def-token \"Name\" (def-data :Name-data)))))")))))


(comment deftest test-parse-and-model-xml
  (testing "Testing parse function."
    (is (=
          '()
          (clj_hypergraph_db.xml_parser.xml_model_parser/create-xml-model
            (clj_hypergraph_db.core/parse "((def-db :xml) (def-token \"People\" (def-token \"Person\" (def-class :Person (def-field :Name :string (path \"Name\" :Name-data)) (def-field :Surname :string (path :Surname)) (primary-key :Name :Surname)) (def-attribute :Surname) (def-token \"Name\" (def-data :Name-data)))))")
            (clj_hypergraph_db.persistance.persistance_manager/add-node :metaclass))))))
