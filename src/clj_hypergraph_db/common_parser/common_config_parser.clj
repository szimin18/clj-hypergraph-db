(ns clj_hypergraph_db.common_parser.common_config_parser)


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
  [type name attribute-list & attributes]
  (let [final-attribute-list (if (empty? attributes)
                               attribute-list
                               (cons attribute-list attributes))
        type-attributes-map (if (empty? final-attribute-list)
                              {:type type}
                              {:type type :attributes final-attribute-list})
        type-name-attributes-map (if (identity name)
                                   (assoc type-attributes-map :name name)
                                   type-attributes-map)]
    type-name-attributes-map))