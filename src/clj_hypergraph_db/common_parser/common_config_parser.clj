(ns clj_hypergraph_db.common_parser.common_config_parser)



(defn def-item
  ([type]
   {:type type})
  ([type & keyvals]
   (apply assoc (def-item type) keyvals)))


(defn database
  [type & metadata]
  (def-item :database
            :metadata metadata))
