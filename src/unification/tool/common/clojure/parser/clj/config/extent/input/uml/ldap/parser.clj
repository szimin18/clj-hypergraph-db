(ns unification.tool.common.clojure.parser.clj.config.extent.input.uml.ldap.parser
  (:require [unification.tool.common.clojure.parser.clj.config.common.parser :refer :all]))


(defn for-each
  [name & other]
  (def-item :for-each
            :name name
            :other other))


(defn add-instance
  [name & mappings]
  (def-item :add-instance
            :name name
            :mappings mappings))


(defn mapping
  [path name]
  (def-item :mapping
            :path path
            :name name))


(defn add-association
  [name & mappings]
  (def-item :add-association
            :name name
            :mappings mappings))


(defn mapping-pk
  [path name]
  (def-item :mapping-pk
            :path path
            :name name))


(defn mapping-fk
  [path name]
  (def-item :mapping-fk
            :path path
            :name name))


(defn evaluate
  [filename]
  (let [namespace (find-ns 'unification.tool.common.clojure.parser.clj.config.extent.input.uml.ldap.parser)]
    (vec (map
           #(binding [*ns* namespace] (eval %))
           (read-string (str "(" (slurp filename) ")"))))))
