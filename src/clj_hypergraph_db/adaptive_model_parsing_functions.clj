(ns clj_hypergraph_db.adaptive_model_parsing_functions)


(defn merge-attributes
  ""
  [& attributes]
  (reduce
    (fn
      [attribute-one attribute-two]
      (reduce
        (fn [previous-map key-to-merge-in]
          (assoc previous-map key-to-merge-in (concat (previous-map key-to-merge-in) (attribute-one key-to-merge-in))))
        (merge attribute-one attribute-two)
        (filter (set (keys attribute-one)) (keys attribute-two))))
    {} attributes))


(defn def-item
  ""
  [type name attribute-list]
  (if (nil? attribute-list)
    {:type type :name name}
    {:type type :name name :attributes (apply merge-attributes attribute-list)}))