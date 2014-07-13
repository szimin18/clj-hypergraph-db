(ns clj_hypergraph_db.sql_parser.sql_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]))



(defn create-sql-model
  [configuration-list]
  (let [metadata (:metadata (find-first-item-by-type configuration-list :database))
        default-configuration (find-first-item-by-type metadata :credentials)
        tables (find-all-items-by-type configuration-list :table)
        relations (find-all-items-by-type configuration-list :relation)]
    (println default-configuration)
    {:default-configuration default-configuration
     :tables tables
     :relations relations}))
