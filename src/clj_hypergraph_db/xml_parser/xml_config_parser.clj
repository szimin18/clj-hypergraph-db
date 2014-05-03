(ns clj_hypergraph_db.xml_parser.xml_config_parser
  (:require [clj_hypergraph_db.common_parser.common_config_parser :refer :all]))


;
; data definition
;


(defn def-token
  [name & attributes]
  (def-item :token name attributes))


(defn def-attribute
  [name]
  (def-item :attribute name nil))


(defn def-data
  [name]
  (def-item :data name nil))


;
; classes definition
;


(defn def-class
  [name & attributes]
  (def-item :class name attributes))


(defn def-field
  [name type path-map]
  (def-item :field name path-map (def-item :type type nil)))


(defn primary-key
  [& fields]
  (def-item :pk nil fields))


(defn path
  [& path-tokens]
  (def-item :path nil path-tokens))


;
; output definition
;




(comment root "People"
         (with-token "Person"
                     (def-class :Person
                                (def-field :Name :string
                                           (path "Name" :Name-data))
                                (def-field :Surname :string
                                           (path :Surname))
                                (primary-key :Name :Surname))
                     (with-attribute :Surname)
                     (with-token "Name"
                                 (with-data :Name-data))))
