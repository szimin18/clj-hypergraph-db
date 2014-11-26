(ns clj_hypergraph_db.sql_parser.uml_to_sql_model_parser
  (:require [clj_hypergraph_db.common_parser.common_functions :refer :all]
            [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.sql_parser.sql_common_functions :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]))



(defn create-model
  [config-list output-model]
  (do
    #_(println config-list)
    {:extent-config config-list :output-model output-model}))
