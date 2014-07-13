(ns clj_hypergraph_db.sql_parser.sql_to_hdm_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]))



(defn create-sql-extent-model
  [configuration-list input-model]
  (let [foreach-tables (find-all-items-by-type configuration-list :foreach)]
    ;foreach-tables
    (map :body foreach-tables))
  )

