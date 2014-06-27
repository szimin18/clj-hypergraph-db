(ns clj_hypergraph_db.core
  (:require [clj_hypergraph_db.persistance.persistance_manager :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_config_parser :refer :all]
            [clj_hypergraph_db.xml_parser.xml_config_parser :refer :all]
            [clj_hypergraph_db.xml_parser.xml_model_parser :refer :all]
            [clj_hypergraph_db.xml_parser.xml_persistance_manager :refer :all]
            [clj_hypergraph_db.xml_parser.xml_model_prototyper :refer :all]
            [clj_hypergraph_db.sql_parser.sql_model_prototyper :refer :all])
  (:gen-class :main true)
  (:use [clojure.tools.logging :only (info)]))


(def config-parser-namespaces
  {:hypergraph    'clj_hypergraph_db.hdm_parser.hdm_uml_config_parser
   :xml           'clj_hypergraph_db.xml_parser.xml_config_parser})


(def model-paser-namespaces
  {:xml           'clj_hypergraph_db.xml_parser.xml_model_parser})


(defn parse
  [file metaclass-handle]
  (let [tokens (read-string file)
        db-type (second (first tokens))
        parsed-configuration (map #(binding [*ns* (find-ns (db-type config-parser-namespaces))] (eval %)) tokens)
        modeled-configuration (binding [*ns* (find-ns (db-type model-paser-namespaces))] (create-model parsed-configuration metaclass-handle))]
    modeled-configuration))


(defn -main
  []
  (create-prototype-of-sql-configuration "glue_ogf" "user" "password" "configuration/sql-input-model.clj"))


;(defn -main
;  []
;  (do
;    (create-database "hgdbtest")
;    (let [metaclass-handle (add-node :metaclass)]
;      (load-input-data (parse (str "(" (slurp "xml-input-model.clj") ")") metaclass-handle) "sample-xml.xml")
;      (write-output-data (parse (str "(" (slurp "xml-output-model.clj") ")") metaclass-handle) "sample-output-xml.xml")
;      ;(peek-database)
;      )
;    (close-database)))
