(ns unification.tool.common.clojure.parser.clj.config.extent.output.uml.sql.parser
  (:require [unification.tool.common.clojure.parser.clj.config.common.parser :refer :all]))

(defn foreach
  [name & body]
  (def-item :foreach
            :name name
            :body body))

(defn foreach-attribute
  [attribute & body]
  (def-item :foreach-attribute
    :attribute attribute
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

(defn evaluate
  [filename]
  (let [namespace (find-ns 'unification.tool.common.clojure.parser.clj.config.extent.output.uml.sql.parser)]
    (vec (map
           #(binding [*ns* namespace] (eval %))
           (read-string (str "(" (slurp filename) ")"))))))
