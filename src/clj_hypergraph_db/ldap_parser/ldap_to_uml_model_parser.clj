(ns clj_hypergraph_db.ldap_parser.ldap_to_uml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_functions :refer :all]
            [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]))


(defn create-model
  [config-list {classes :classes}]
  {:add-instance
    (reduce merge-concat {}
            (for [{class-name :name other :other} (find-all-items-by-type config-list :foreach)
                  {mappings :mappings uml-name :name} (find-all-items-by-type other :add-instance)]
              (let [{name :name :as class-token} (classes class-name)
                    class-attributes (loop [{superclass :superclass attributes :attributes} class-token
                                            attributes-map {}]
                                       (if-let [class-token (classes superclass)]
                                         (recur class-token (merge attributes attributes-map))
                                         (merge attributes attributes-map)))]
                {name [{:name uml-name
                        :mappings (for [{path :path name :name} (find-all-items-by-type mappings :mapping)]
                                    [(-> path class-attributes :name keyword) name])}]})))})
