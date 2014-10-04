(ns clj_hypergraph_db.xml_parser.xml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]))


(defn parse-token
  [{name :name other :other}]
  (let [attributes (into {} (for [{attribute-name :attribute-name
                                   name :name} (find-all-items-by-type other :attribute)]
                              [attribute-name {:name name}]))
        text (:name (find-first-item-by-type other :text))
        children (into {} (for [child (find-all-items-by-type other :token)]
                            [(:token-name child) (parse-token child)]))]
    {:name name
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
