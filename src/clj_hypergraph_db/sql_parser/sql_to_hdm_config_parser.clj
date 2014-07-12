(ns clj_hypergraph_db.xml_parser.xml_to_hdm_config_parser
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
