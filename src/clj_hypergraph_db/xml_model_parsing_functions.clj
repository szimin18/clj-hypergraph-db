(ns clj_hypergraph_db.xml_model_parsing_functions
  (:require [clj_hypergraph_db.adaptive_model_parsing_functions :refer :all]))


(defn root
  [name & attributes]
  (def-item :token name attributes))


(defn with-token
  [name & attributes]
  {:attribute (list (apply def-token (cons name attributes)))})


(defn with-attribute
  [name]
  {:attribute (list name)})


(defn with-data
  [name]
  {:data (def-item :data name nil)})




(defn def-type
  []
  ())


(defn primary-key
  []
  ())

(comment root "People"
      (with-token "Person"
                  (def-type :Person
                            (primary-key
                              (path :Surname)
                              (path "Name" :Name-data)))
                  (with-attribute :Surname)
                  (with-token "Name"
                              (with-data :Name-data))))