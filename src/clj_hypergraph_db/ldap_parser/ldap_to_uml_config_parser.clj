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
