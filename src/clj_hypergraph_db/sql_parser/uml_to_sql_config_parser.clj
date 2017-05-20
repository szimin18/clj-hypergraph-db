(ns clj_hypergraph_db.sql_parser.uml_to_sql_config_parser
  (:require [clj_hypergraph_db.common_parser.common_config_parser :refer :all]))


(defn- def-function
  [name body]
  (def-item :function
            :name name

(function
  :trim-all
  [v]
  (map
    (fn
      [s]
      (let [s (atom s)]
        (while (#{\space} (first s))
          (swap! s rest))
        (while (#{\space} (last s))
          (swap! s drop-last))
        @s))
    v))

(function
  :regex-split
  [s delim]
  (clojure.string/split s delim))

(function
  :join
  [coll sep]
  (clojure.string/join sep coll))

(function
  :concat
  [& seqs]
  (apply concat seqs))


(function
  :sum
  [& args]
  (apply + args))

(defn foreach
  [name & body]
  (def-item :foreach
            :name name
            :body body))


(defn add-entity
  [table & mappings]
  (def-item :add-entity
            :table table
            :mappings mappings))

(defn mapping-relation
  [relation-path column & relation]
  (def-item :mapping-relation
            :relation-path relation-path
            :column column
            :relation relation))

(defn for-each-association
  [association-name & body]
  (def-item :association
            :name association-name
            :body body))


(defn mapping
  [attribute column]
  (def-item :mapping
            :name attribute
            :column column))

(defn bind
  [from to]
  (def-item :bind
            :from from
            :to to))


(defn call
  [fn-name & args]
  (def-item :call
            :fn-name fn-name
            :args args))


(defn aggregate
  [arg]
  (def-item :aggregate
            :arg arg))


(defn- def-function
  [name body]
  (def-item :function
            :name name
            :body body))

