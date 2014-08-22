(ns clj_hypergraph_db.xml_parser.uml_to_xml_config_parser
  (:require [clj_hypergraph_db.common_parser.common_config_parser :refer :all]))


(defn foreach
  [name & body]
  (def-item :foreach
            :name name
            :body body))


(defn add-token
  [path & mappings]
  (def-item :add-token
            :path path
            :mappings mappings))


(defn associated-with
  [path path-role association-name target-role & body]
  (def-item :associated-with
            :path path
            :path-role path-role
            :association-name association-name
            :target-role target-role
            :body body))


(defn mapping
  [name path]
  (def-item :mapping
            :name name
            :path path))
