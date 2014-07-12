(ns clj_hypergraph_db.core
  (:gen-class :main true)
  (:require ;persistance
            [clj_hypergraph_db.persistance.persistance_manager :refer :all]

            ;hdm
            [clj_hypergraph_db.hdm_parser.hdm_uml_config_parser :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_parser :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]

            ;xml
            [clj_hypergraph_db.xml_parser.xml_config_parser :refer :all]
            [clj_hypergraph_db.xml_parser.xml_model_parser :refer :all]
            [clj_hypergraph_db.xml_parser.xml_persistance_manager :refer :all]
            [clj_hypergraph_db.xml_parser.xml_to_hdm_config_parser :refer :all]
            [clj_hypergraph_db.xml_parser.xml_to_hdm_model_parser :refer :all]

            ;sql
            [clj_hypergraph_db.sql_parser.sql_config_parser]

            ;prototypers
            [clj_hypergraph_db.xml_parser.xml_model_prototyper :refer :all]
            [clj_hypergraph_db.sql_parser.sql_model_prototyper :refer :all]))


(defn -main
  []
  (do
    (comment (create-database "hgdbtest")
    (create-hdm-uml-persistance-model "configuration/hdm-uml-model.clj")
    (let [xml-config (map #(binding [*ns* (find-ns 'clj_hypergraph_db.xml_parser.xml_config_parser)] (eval %))
                       (read-string (str "(" (slurp "configuration/xml-input-model.clj") ")")))
          xml-model (binding [*ns* (find-ns 'clj_hypergraph_db.xml_parser.xml_model_parser)] (create-xml-model xml-config))
          extent-config (map #(binding [*ns* (find-ns 'clj_hypergraph_db.xml_parser.xml_to_hdm_config_parser)] (eval %))
                          (read-string (str "(" (slurp "configuration/xml-input-extent.clj") ")")))
          extent-model (binding [*ns* (find-ns 'clj_hypergraph_db.xml_parser.xml_to_hdm_model_parser)] (create-extent-model
                                                                                                         extent-config
                                                                                                         xml-model))]
      (load-input-xml-data (:root extent-model) "resources/BES-Example.xml")))

    (let [sql-config (map #(binding [*ns* (find-ns 'clj_hypergraph_db.sql_parser.sql_config_parser)] (eval %))
                       (read-string (str "(" (slurp "configuration/sql-input-model.clj") ")")))]
      (comment (map sql-config println)))
    (comment close-database)))


;(create-prototype-of-sql-configuration "glue_ogf" "user" "password" "configuration/sql-input-model.clj")
;(create-prototype-of-xml-configuration "resources/BES-Example" "configuration/xml-input-model.clj")
