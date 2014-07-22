(ns clj_hypergraph_db.xml_parser.xml_to_uml_config_parser
  (:require [clj_hypergraph_db.common_parser.common_config_parser :refer :all]))


(defn foreach
  [path & body]
  (def-item :foreach
            :path path
            :body body))


(defn in
  [path & body]
  (def-item :in
            :path path
            :body body))


(defn as
  [alias path]
  (def-item :alias
            :alias alias
            :path path))


(defn add-instance
  [name & mappings]
  (def-item :add-instance
            :name name
            :mappings mappings))


(defn add-association
  [name & mappings]
  (def-item :add-association
            :name name
            :mappings mappings))


(defn mapping
  [path name]
  (def-item :mapping
            :path path
            :name name))


(defn mapping-pk
  [path name]
  (def-item :mapping-pk
            :path path
            :name name))
