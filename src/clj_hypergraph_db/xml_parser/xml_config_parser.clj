(ns clj_hypergraph_db.xml_parser.xml_config_parser
  (:require [clj_hypergraph_db.common_parser.common_config_parser :refer :all]))



(defn default-path
  [path]
  (def-item :default-path
            :path path))


(defn token
  [token-name name & other]
  (def-item :token
            :token-name token-name
            :name name
            :other other))


(defn attribute
  [attribute-name name]
  (def-item :attribute
            :attribute-name attribute-name
            :name name))


(defn text
  [name]
  (def-item :text
            :name name))
