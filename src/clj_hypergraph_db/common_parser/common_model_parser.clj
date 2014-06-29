(ns clj_hypergraph_db.common_parser.common_model_parser
  (:require [clj_hypergraph_db.persistance.persistance_manager :refer :all])
  (:use [clojure.tools.logging :only (info)]))


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
