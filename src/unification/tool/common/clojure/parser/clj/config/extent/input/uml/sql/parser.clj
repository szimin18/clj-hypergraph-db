(ns unification.tool.common.clojure.parser.clj.config.extent.input.uml.sql.parser
  (:require [unification.tool.common.clojure.parser.clj.config.common.parser :refer :all]))

(defn foreach
  [table & body]
  (def-item :foreach
            :table table
            :body body))

(defn add-instance
  [name & mappings]
  (def-item :add-instance
            :name name
            :mappings mappings))


(defn add-association
  ([name & roles]
   (def-item :add-association
             :name name
             :roles roles)))

(defn mapping-pk
  [column name]
  (def-item :mapping-pk
            :column column
            :name name))


(defn mapping
  [column name]
  (def-item :mapping
            :column column
            :name name))

(defn role
  [name column]
  (def-item :role
            :name name
            :column column))


(defn evaluate
  [filename]
  (let [namespace (find-ns 'unification.tool.common.clojure.parser.clj.config.extent.input.uml.sql.parser)]
    (vec (map
           #(binding [*ns* namespace] (eval %))
           (read-string (str "(" (slurp filename) ")"))))))
