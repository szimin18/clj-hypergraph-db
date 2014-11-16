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
  [from to]
  (def-item :mapping
            :name from
            :path to))


(defn mapping-each
  [from to]
  (def-item :mapping-each
            :from from
            :to to))


(defn bind
  [from to]
  (def-item :bind
            :from from
            :to to))


(defn call
  [fn-name & args]
  (def-item :call
            :fn-name fn-name
            :args args))


(defn def-function
  [name body]
  (def-item :function
            :name name
            :body body))
