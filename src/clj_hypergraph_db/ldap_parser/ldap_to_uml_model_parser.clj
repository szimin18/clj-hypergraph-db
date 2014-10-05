(ns clj_hypergraph_db.ldap_parser.ldap_to_uml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_functions :refer :all]
            [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]))


(defn get-all-attributes
  [class-token classes]
  (loop [{superclass :superclass attributes :attributes} class-token
         attributes-map {}]
    (if-let [class-token (classes superclass)]
      (recur class-token (merge attributes-map attributes))
      (merge attributes attributes-map))))


(defn get-mappings
  [mappings mapping-type class-attributes]
  (for [{path :path name :name} (find-all-items-by-type mappings mapping-type)]
    [(-> path class-attributes :name keyword) name]))


(defn create-model
  [config-list {classes :classes}]
  (apply
    merge-with
    merge-concat
    (apply
      concat
      (for [{class-name :name other :other} (find-all-items-by-type config-list :foreach)
            :let [{string-class-name :name :as class-token} (classes class-name)
                  class-attributes (get-all-attributes class-token classes)]]
        (concat
          (for [{mappings :mappings uml-name :name} (find-all-items-by-type other :add-instance)]
            {:add-instance {string-class-name [{:name uml-name
                                                :mappings (get-mappings mappings :mapping class-attributes)}]}})
          (for [{mappings :mappings uml-name :name} (find-all-items-by-type other :add-association)]
            {:add-association {string-class-name [{:name uml-name
                                                   :mapping-pk (first (get-mappings mappings :mapping-pk class-attributes))
                                                   :mappings-fk (get-mappings mappings :mapping-fk class-attributes)}]}}))))))
