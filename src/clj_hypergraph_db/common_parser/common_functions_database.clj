(ns clj_hypergraph_db.common_parser.common_functions_database)

(def default-functions-map
  {:sum
   (fn [x & xs]
     (reduce + x xs))
   :sum-coll
   (fn [coll]
     (if (coll? coll)
       (reduce + 0 coll)))
   :seubstract
   (fn [x y]
     (- x y))
   :multiply
   (fn [x & xs]
     (reduce * x xs))
   :multiply-coll
   (fn [coll]
     (if (coll? coll)
       (reduce * 0 coll)))
   :divide
   (fn [x y]
     (if (not (zero? y))
       (/ x y)))
   :increment
   (fn [x]
     (inc x))
   :decrement
   (fn [x]
     (dec x))})
