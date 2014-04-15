(ns clj_hypergraph_db.model_parsing_functions)


(defn def-attribute
  ""
  [name]
  {:type :attribute :name name})


(defn merge-attributes
  ""
  [& attributes]
  (reduce
    (fn
      [attribute-one attribute-two]
      (reduce (fn
                [previous-map key-to-merge-in]
                (assoc previous-map key-to-merge-in (merge (previous-map key-to-merge-in) (attribute-one key-to-merge-in))))
              (merge attribute-one attribute-two) (filter #(contains? (keys attribute-one) %) (keys attribute-two))))
    {} attributes))


(defn def-type
  ""
  [name & attributes]
  (apply merge (cons {:type :type :name name} (merge-attributes attributes))))


(defn def-link
  ""
  [name & attributes]
  (apply merge (cons {:type :link :name name} (merge-attributes attributes))))


(defn with-attribute
  ""
  [attribute]
  {:attribute (list attribute)})


(defn from
  ""
  [attribute]
  {:from (list attribute)})


(defn to
  ""
  [attribute]
  {:to (list attribute)})


(defn from-to
  ""
  [attribute]
  {:from (list attribute) :to (list attribute)})


(defn parse
  ""
  [file]
  (apply merge (map #(do %) (list file))))
