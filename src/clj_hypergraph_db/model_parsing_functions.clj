(ns clj_hypergraph_db.model_parsing_functions
  (:use [clojure.tools.logging :only (info)]))


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


(defn -def-item
  ""
  [type name attribute-list]
  (if (nil? attribute-list)
    {:type type :name name}
    {:type type :name name :attributes (apply merge-attributes attribute-list)}))


(defn def-type
  [name & attributes]
  (-def-item :type name attributes))


(defn def-named-link
  [name & attributes]
  (-def-item :named-link name attributes))


(defn def-unnamed-link
  [name & attributes]
  (-def-item :unnamed-link name attributes))


(defn with-classifier
  [name]
  {:classifier (list name)})


(defn from
  [attribute]
  {:from (list attribute)})


(defn to
  [attribute]
  {:to (list attribute)})
