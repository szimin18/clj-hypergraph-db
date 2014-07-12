(ns clj_hypergraph_db.sql_parser.sql_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]))



(defn create-sql-model
  [configuration-list]
  (let [metadata (:metadata (find-first-item-by-type configuration-list :database))
        default-configuration (:configuration (find-first-item-by-type metadata :default-configuration))
        tables (find-all-items-by-type configuration-list :table)]
    {:default-configuration default-configuration
     :tables tables}))