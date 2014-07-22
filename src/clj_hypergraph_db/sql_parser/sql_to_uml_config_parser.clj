(ns clj_hypergraph_db.sql_parser.sql_to_uml_config_parser
  (:require [clj_hypergraph_db.common_parser.common_config_parser :refer :all]))

(defn foreach
  [table & body]
  (def-item :foreach
            :table table
            :body body))

(defn add-sql-instance
  [name & mappings]
  (def-item :add-instance
            :name name
            :mappings mappings))


(defn add-sql-association
  [name]
  (def-item :add-association
            :name name))

(defn mapping-sql-pk
  [column name]
  (def-item :mapping-pk
            :column column
            :name name))


(defn mapping-sql
  [column name]
  (def-item :mapping
            :column column
            :name name))
