(ns clj_hypergraph_db.hypergraph_model_parsing_functions
  (:require [clj_hypergraph_db.adaptive_model_parsing_functions :refer :all]))


(defn def-type
  [name & attributes]
  (def-item :type name attributes))


(defn def-named-link
  [name & attributes]
  (def-item :named-link name attributes))


(defn def-unnamed-link
  [name & attributes]
  (def-item :unnamed-link name attributes))


(defn with-classifier
  [name]
  {:classifier (list name)})


(defn from
  [attribute]
  {:from (list attribute)})


(defn to
  [attribute]
  {:to (list attribute)})
