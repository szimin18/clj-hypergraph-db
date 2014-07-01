(ns clj_hypergraph_db.common_parser.common_config_parser)


(defn def-item
  ([type]
   {:type type})
  ([type & keyvals]
   (apply assoc (cons {:type type} keyvals))))
