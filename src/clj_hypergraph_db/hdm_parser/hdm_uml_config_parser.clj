(ns clj_hypergraph_db.hdm_parser.hdm_uml_config_parser
  (:require [clj_hypergraph_db.common_parser.common_config_parser :refer :all]))


(defn representation
  [type-name representation]
  (def-item :representation
            :type type-name
            :representation representation))


(defn class
  [name & other]
  (def-item :class
            :name name
            :other other))


(defn attribute
  [name type L U]
  (def-item :attribute
            :name name
            :representation type
            :L L
            :U U
            :pk false))


(defn key-attribute
  [name type L U]
  (def-item :attribute
            :name name
            :representation type
            :L L
            :U U
            :pk true))


(defn extends
  [class-name]
  (def-item :extends
            :superclass class-name))


(defn association
  [name description & roles]
  (def-item :association
            :name name
            :description description
            :roles roles))


(defn role
  ([name target-class L U]
   (role name "" target-class L U))
  ([name description target-class L U]
   (def-item :role
             :name name
             :description description
             :target-class target-class
             :L L
             :U U)))
