(ns clj_hypergraph_db.ldap_parser.ldap_config_parser
  (:require [clj_hypergraph_db.common_parser.common_config_parser :refer :all]))


(defn default-host
  [name]
  (def-item :default-host
            :name name))


(defn default-port
  [name]
  (def-item :default-port
            :name name))


(defn default-dn
  [name]
  (def-item :default-dn
            :name name))


(defn default-password
  [name]
  (def-item :default-password
            :name name))


(defn object-class
  [name string-name & other]
  (def-item :object-class
            :name name
            :string-name string-name
            :other other))


(defn superclass
  [name]
  (def-item :superclass
            :name name))


(defn structural
  []
  (def-item :structural))


(defn must
  [name string-name]
  (def-item :must
            :name name
            :string-name string-name))


(defn may
  [name string-name]
  (def-item :may
            :name name
            :string-name string-name))
