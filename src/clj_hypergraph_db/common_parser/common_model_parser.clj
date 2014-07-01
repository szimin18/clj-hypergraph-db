(ns clj_hypergraph_db.common_parser.common_model_parser)


(defn find-all-items-by-type
  [coll type]
  (if (seq? coll)
    (filter #(= type (:type %)) coll)
    '()))


(defn find-first-item-by-type
  [coll type]
  (if (seq? coll)
    (first (filter #(= type (:type %)) coll))
    nil))


(defn find-first-item-by-type-and-name
  [coll type name]
  (if (seq? coll)
    (first (filter #(and (= name (:name %)) (= type (:type %))) coll))
    nil))