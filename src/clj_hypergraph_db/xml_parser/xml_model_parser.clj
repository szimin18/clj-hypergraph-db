(ns clj_hypergraph_db.xml_parser.xml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]))


(defn parse-token
  [token-configuration]
  (let [attributes (reduce
                     #(assoc %1 (:attribute-name %2) {:name (:name %2)})
                     {}
                     (find-all-items-by-type (:other token-configuration) :attribute))
        text (:name (find-first-item-by-type (:other token-configuration) :text))
        children (reduce
                   #(assoc %1 (:token-name %2) (parse-token %2))
                   {}
                   (find-all-items-by-type (:other token-configuration) :token))]
    {:name (:name token-configuration)
     :attributes attributes
     :children children
     :text text}))


(defn create-model
  [configuration-list]
  (let [metadata (:metadata (find-first-item-by-type configuration-list :database))
        default-path (:path (find-first-item-by-type metadata :default-path))
        first-token (find-first-item-by-type configuration-list :token)
        root {:children {(:token-name first-token) (parse-token first-token)}}]
    {:default-access [default-path]
     :root root}))
