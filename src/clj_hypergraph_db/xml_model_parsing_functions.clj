(ns clj_hypergraph_db.xml_model_parsing_functions
  (:require [clj_hypergraph_db.adaptive_model_parsing_functions :refer :all]))


(defn def-token
  [name & attributes]
  (def-item :token name attributes))


(defn with-attribute
  [name]
  {:attribute (list name)})


(defn with-def-token
  [name & attributes]
  {:attribute (list (apply def-token (cons name attributes)))})


(defn def-data
  [name]
  {:data (def-item :data name nil)})
