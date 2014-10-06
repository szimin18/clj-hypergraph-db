(ns clj_hypergraph_db.sql_parser.uml_to_sql_config_parser
  (:require [clj_hypergraph_db.common_parser.common_config_parser :refer :all]))


(defn foreach
  [name & body]
  (def-item :foreach
            :name name
            :body body))


(defn add-entity
  [table & mappings]
  (def-item :add-entity
            :table table
            :mappings mappings))

(defn mapping-single-relation
  [relation-path column]
  (def-item :mapping-single-relation
            :relation-path relation-path
            :column column))

#_(defn associated-with
  [path path-role association-name target-role & body]
  (def-item :associated-with
            :path path
            :path-role path-role
            :association-name association-name
            :target-role target-role
            :body body))


(defn mapping
  [attribute column]
  (def-item :mapping
            :name attribute
            :column column))
