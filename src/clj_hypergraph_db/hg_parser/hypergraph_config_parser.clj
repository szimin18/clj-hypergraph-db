(ns clj_hypergraph_db.hg_parser.hypergraph_config_parser
  (:require [clj_hypergraph_db.common_parser.common_config_parser :refer :all]))


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
