(ns unification.tool.common.clojure.parser.clj.config.extent.input.uml.xml.parser
  (:require [unification.tool.common.clojure.parser.clj.config.common.parser :refer :all]))


(defn foreach
  [path & body]
  (def-item :foreach
            :path path
            :body body))


(defn add-instance
  [name & mappings]
  (def-item :add-instance
            :name name
            :mappings mappings))


(defn add-association
  [name & mappings]
  (def-item :add-association
            :name name
            :mappings mappings))


(defn mapping
  [path name]
  (def-item :mapping
            :path path
            :name name
            :pk-mapping false))


(defn mapping-pk
  [path name]
  (def-item :mapping
            :path path
            :name name
            :pk-mapping true))


(defn evaluate
  [filename]
  (let [namespace (find-ns 'unification.tool.common.clojure.parser.clj.config.extent.input.uml.xml.parser)]
    (vec (map
           #(binding [*ns* namespace] (eval %))
           (read-string (str "(" (slurp filename) ")"))))))
