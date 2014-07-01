(ns clj_hypergraph_db.core
  (:require [clj_hypergraph_db.persistance.persistance_manager :refer :all]

            [clj_hypergraph_db.hdm_parser.hdm_uml_config_parser :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_parser :refer :all]

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
        modeled-configuration (binding [*ns* (find-ns (db-type model-paser-namespaces))] (create-hdm-uml-model parsed-configuration metaclass-handle))]
    modeled-configuration))


(defn -main
  []
  (do
    (create-database "hgdbtest")
    (println (binding [*ns* (find-ns 'clj_hypergraph_db.hdm_parser.hdm_uml_model_parser)]
               (create-hdm-uml-model (map #(binding [*ns* (find-ns 'clj_hypergraph_db.hdm_parser.hdm_uml_config_parser)] (eval %))
                                  (read-string (str "(" (slurp "configuration/hdm-uml-model.clj") ")"))))))
    (println (binding [*ns* (find-ns 'clj_hypergraph_db.xml_parser.xml_model_parser)]
               (create-xml-model (map #(binding [*ns* (find-ns 'clj_hypergraph_db.xml_parser.xml_config_parser)] (eval %))
                                  (read-string (str "(" (slurp "configuration/xml-input-model.clj") ")"))))))
    (close-database)))



;(create-prototype-of-sql-configuration "glue_ogf" "user" "password" "configuration/sql-input-model.clj")

;(create-prototype-of-xml-configuration "resources/BES-Example" "configuration/xml-input-model.clj")
