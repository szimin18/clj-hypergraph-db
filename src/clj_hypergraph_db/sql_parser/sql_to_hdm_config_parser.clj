(ns clj_hypergraph_db.sql_parser.sql_to_hdm_config_parser
  (:require [clj_hypergraph_db.common_parser.common_config_parser :refer :all]))

(defn foreach
  [table & body]
  (def-item :foreach
            :table table
            :body body))

(defn add-instance
  [name & mappings]
  (def-item :add-instance
            :name name
            :mappings mappings))


(defn add-association
  [name]
  (def-item :add-association
            :name name))

(defn mapping-pk
  [column name]
  (def-item :mapping-pk
            :column column
            :name name))


(defn mapping
  [column name]
  (def-item :mapping
            :column column
            :name name))
