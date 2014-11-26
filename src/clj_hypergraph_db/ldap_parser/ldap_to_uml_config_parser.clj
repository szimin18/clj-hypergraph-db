(ns clj_hypergraph_db.ldap_parser.ldap_to_uml_config_parser
  (:require [clj_hypergraph_db.common_parser.common_config_parser :refer :all]))


(defn foreach
  [name & other]
  (def-item :foreach
            :name name
            :other other))


(defn add-instance
  [name & mappings]
  (def-item :add-instance
            :name name
            :mappings mappings))


(defn mapping
  [path name]
  (def-item :mapping
            :path path
            :name name))


(defn add-association
  [name & mappings]
  (def-item :add-association
            :name name
            :mappings mappings))


(defn mapping-pk
  [path name]
  (def-item :mapping-pk
            :path path
            :name name))


(defn mapping-fk
  [path name]
  (def-item :mapping-fk
            :path path
            :name name))
