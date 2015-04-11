(ns unification.tool.common.clojure.parser.clj.config.intermediate.uml.parser
  (:require [unification.tool.common.clojure.parser.clj.config.common.parser :refer :all]))

(defn model-type
  [model-type]
  (def-item :model-type))


(defn representation
  [type-name representation]
  (def-item :representation
            :variable-type type-name
            :representation representation))


(defn uml-class
  [name & other]
  (def-item :class
            :name name
            :other other))


(defn uml-attribute
  [name type L U]
  (def-item :attribute
            :name name
            :variable-type type
            :L L
            :U U
            :pk false))


(defn key-uml-attribute
  [name type L U]
  (def-item :attribute
            :name name
            :representation type
            :L L
            :U U
            :pk true))


(defn extends
  [class-name]
  (def-item :extends
            :superclass class-name))


(defn association
  [name description & roles]
  (def-item :association
            :name name
            :description description
            :roles roles))


(defn role
  ([name target-class L U]
   (role name "" target-class L U))
  ([name description target-class L U]
   (def-item :role
             :name name
             :description description
             :target-class target-class
             :L L
             :U U)))


(defn evaluate
  [filename]
  (let [namespace (find-ns 'unification.tool.common.clojure.parser.clj.config.intermediate.uml.model.parser)]
    (vec (map
           #(binding [*ns* namespace] (eval %))
           (read-string (str "(" (slurp filename) ")"))))))
