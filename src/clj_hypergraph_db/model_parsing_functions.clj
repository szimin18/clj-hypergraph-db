(ns clj_hypergraph_db.model_parsing_functions
  (:use [clojure.tools.logging :only (info)]))


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
      (reduce
        (fn [previous-map key-to-merge-in]
          (assoc previous-map key-to-merge-in (merge (previous-map key-to-merge-in) (attribute-one key-to-merge-in))))
        (merge attribute-one attribute-two)
        (filter #(contains? (keys attribute-one) %) (keys attribute-two))))
    {} attributes))


(defn def-type
  ""
  [name & attributes]
  (if (nil? attributes)
    {:type :type :name name}
    {:type :type :name name :attributes attributes}))


(defn def-named-link
  ""
  [name & attributes]
  {:type :named-link :name name :attributes (apply merge attributes)})


(defn def-unnamed-link
  ""
  [name & attributes]
  {:type :unnamed-link :name name :attributes (apply merge attributes)})


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
