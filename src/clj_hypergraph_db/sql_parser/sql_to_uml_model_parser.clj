(ns clj_hypergraph_db.sql_parser.sql_to_uml_model_parser)


(defn create-model
  [extent-config input-model]
  {:extent-config extent-config :input-model input-model})
