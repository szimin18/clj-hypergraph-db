(ns clj_hypergraph_db.core
  (:gen-class :main true)
  (:require [clj_hypergraph_db.persistance.persistance_manager :refer :all] ;peek database
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all] ;get-class-instances
            [clj_hypergraph_db.common_parser.common_model_parser :refer :all]))

(def run-namespaces
  {:hdm {:uml {:config 'clj_hypergraph_db.hdm_parser.hdm_uml_config_parser
               :model 'clj_hypergraph_db.hdm_parser.hdm_uml_model_parser
               :manager 'clj_hypergraph_db.hdm_parser.hdm_uml_model_manager
               :extents {:xml {}
                         :sql {}}}}
   :models {:xml {:config 'clj_hypergraph_db.xml_parser.xml_config_parser
                  :model 'clj_hypergraph_db.xml_parser.xml_model_parser
                  :extents {:uml {:config 'clj_hypergraph_db.xml_parser.xml_to_uml_config_parser
                                  :model 'clj_hypergraph_db.xml_parser.xml_to_uml_model_parser
                                  :persistance 'clj_hypergraph_db.xml_parser.xml_to_uml_persistance_manager}}}
            :sql {:config 'clj_hypergraph_db.sql_parser.sql_config_parser
                  :model 'clj_hypergraph_db.sql_parser.sql_model_parser
                  :extents {:uml {:config 'clj_hypergraph_db.sql_parser.sql_to_uml_config_parser
                                  :model 'clj_hypergraph_db.sql_parser.sql_to_uml_model_parser
                                  :persistance 'clj_hypergraph_db.sql_parser.sql_to_uml_persistance_manager}}}}
   :prototypers {:xml 'clj_hypergraph_db.xml_parser.xml_model_prototyper
                 :sql 'clj_hypergraph_db.sql_parser.sql_model_prototyper}})


(defn run
  [run-filename]
  (do
    (create-database "hgdbtest")
    (let [run-config (do
                       (require 'clj_hypergraph_db.run_config_parser)
                       (map
                         #(binding [*ns* (find-ns 'clj_hypergraph_db.run_config_parser)] (eval %))
                         (read-string (str "(" (slurp run-filename) ")"))))
          hdm-config-file (read-string (str "(" (slurp (:filename (find-first-item-by-type run-config :hdm))) ")"))
          hdm-model-type (second (first hdm-config-file))
          hdm-namespaces ((run-namespaces :hdm) hdm-model-type)
          hdm-config-namespace (:config hdm-namespaces)
          hdm-model-namespace (:model hdm-namespaces)
          hdm-manager-namespace (:manager hdm-namespaces)
          hdm-config (do
                       (require hdm-config-namespace)
                       (map
                         #(binding [*ns* (find-ns hdm-config-namespace)] (eval %))
                         hdm-config-file))
          hdm-model (do
                      (require hdm-model-namespace)
                      (binding [*ns* (find-ns hdm-model-namespace)]
                        (eval ('create-model hdm-config))))]
      (do
        (require hdm-manager-namespace)
        (binding [*ns* (find-ns hdm-manager-namespace)]
          (set-model hdm-model)))
      (doseq [input-token (find-all-items-by-type run-config :input)]
        (let [input-config-file (read-string (str "(" (slurp (:filename input-token)) ")"))
              input-type (second (first input-config-file))
              input-namespaces ((run-namespaces :models) input-type)
              input-config-namespace (:config input-namespaces)
              input-model-namespace (:model input-namespaces)
              input-manager-namespace (:manager input-namespaces)
              input-config (do
                             (require input-config-namespace)
                             (map
                               #(binding [*ns* (find-ns input-config-namespace)] (eval %))
                               input-config-file))
              input-model (do
                            (require input-model-namespace)
                            (binding [*ns* (find-ns input-model-namespace)]
                              (eval (read-string (str "(create-model " input-config ")")))))]
          (println input-model))))
    ;(create-persistance-model "configuration/hdm-uml-model.clj")
    ;(do (require 'clj_hypergraph_db.sql_parser.sql_to_hdm_config_parser)
    ;(let [sql-config (map #(binding [*ns* (find-ns 'clj_hypergraph_db.sql_parser.sql_config_parser)] (eval %))
    ;                      (read-string (str "(" (slurp "configuration/sql-input-model.clj") ")")))
    ;      sql-model (binding [*ns* (find-ns 'clj_hypergraph_db.sql_parser.sql_model_parser)] (create-sql-model sql-config))
    ;      sql-extent-config (map #(binding [*ns* (find-ns 'clj_hypergraph_db.sql_parser.sql_to_hdm_config_parser)] (eval %))
    ;                             (read-string (str "(" (slurp "configuration/sql-input-extent.clj") ")")))]
    ;  (binding [*ns* (find-ns 'clj_hypergraph_db.sql_parser.sql_to_hdm_model_parser)] (import-sql-into-hdm
    ;                                                                                    sql-extent-config
    ;                                                                                    sql-model))))
    ;(do
    ;  (require 'clj_hypergraph_db.xml_parser.xml_to_hdm_config_parser)
    ;(let [xml-config (map #(binding [*ns* (find-ns 'clj_hypergraph_db.xml_parser.xml_config_parser)] (eval %))
    ;                      (read-string (str "(" (slurp "configuration/xml-input-model.clj") ")")))
    ;      xml-model (binding [*ns* (find-ns 'clj_hypergraph_db.xml_parser.xml_model_parser)] (create-xml-model xml-config))
    ;      extent-config (map #(binding [*ns* (find-ns 'clj_hypergraph_db.xml_parser.xml_to_hdm_config_parser)] (eval %))
    ;                         (read-string (str "(" (slurp "configuration/xml-input-extent.clj") ")")))
    ;      extent-model (binding [*ns* (find-ns 'clj_hypergraph_db.xml_parser.xml_to_hdm_model_parser)] (create-extent-model
    ;                                                                                                     extent-config
    ;                                                                                                     xml-model))]
    ;  (load-input-xml-data (:root extent-model) "resources/BES-Example.xml")))
    ;(println (get-class-instances :UserDomain))
    ;(println (get-class-instances :AdminDomain))
    (close-database)))


(defn -main
  []
  (do
    (run "configuration/run.clj")))


;(create-prototype-of-sql-configuration "glue_ogf" "user" "password" "configuration/sql-input-model.clj")
;(create-prototype-of-xml-configuration "resources/BES-Example" "configuration/xml-input-model.clj")
