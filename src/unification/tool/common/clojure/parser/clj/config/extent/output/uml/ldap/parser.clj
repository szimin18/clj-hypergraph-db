(ns unification.tool.common.clojure.parser.clj.config.extent.output.uml.ldap.parser
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
  [name path]
  (def-item :mapping
            :name name
            :path path))


(defn mapping-fk
  [primary-role association-name foreign-role foreign-class-name foreign-pk-name attribute-name]
  (def-item :mapping-fk
            :primary-role primary-role
            :association-name association-name
            :foreign-role foreign-role
            :foreign-class-name foreign-class-name
            :foreign-pk-name foreign-pk-name
            :attribute-name attribute-name))


(defn evaluate
  [filename]
  (let [namespace (find-ns 'unification.tool.common.clojure.parser.clj.config.extent.output.uml.ldap.parser)]
    (vec (map
           #(binding [*ns* namespace] (eval %))
           (read-string (str "(" (slurp filename) ")"))))))
