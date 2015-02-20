(ns clj_hypergraph_db.common_parser.common_model_parser)


(defn find-all-items-by-type
  [coll type]
  (if (coll? coll)
    (filter #(= type (:type %)) coll)
    ()))


(defn find-first-item-by-type
  [coll type]
  (if (coll? coll)
    (some #(if (= type (:type %)) %) coll)))


(defn find-first-item-by-type-and-name
  [coll type name]
  (if (coll? coll)
    (some #(if (and (= name (:name %)) (= type (:type %))) %) coll)))