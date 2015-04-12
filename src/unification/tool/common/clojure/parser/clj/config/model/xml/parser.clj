(ns unification.tool.common.clojure.parser.clj.config.model.xml.parser
  (:require [unification.tool.common.clojure.parser.clj.config.common.parser :refer :all]))


(defn default-path
  [path]
  (def-item :default-path
            :path path))


(defn token
  [token-name name & other]
  (def-item :token
            :token-name token-name
            :name name
            :other other))


(defn attribute
  [attribute-name name]
  (def-item :attribute
            :attribute-name attribute-name
            :name name))


(defn text
  [name]
  (def-item :text
            :name name))


(defn evaluate
  [filename]
  (let [namespace (find-ns 'unification.tool.common.clojure.parser.clj.config.model.xml.parser)]
    (vec (map
           #(binding [*ns* namespace] (eval %))
           (read-string (str "(" (slurp filename) ")"))))))
