(ns clj_hypergraph_db.hdm_parser.hdm_uml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.common_parser.common_functions :refer :all]
            [clj_hypergraph_db.persistance.persistance_manager :refer :all]))


(defn create-class
  [{name :name
    other :other} representation-mappings]
  (let [attributes-tokens-list (find-all-items-by-type other :attribute)
        attributes (into {} (for [{variable-type :variable-type
                                   name :name
                                   U :U
                                   L :L} attributes-tokens-list]
                              [name {:representation (or (representation-mappings variable-type) String)
                                     :unique (= :1 U)
                                     :mandatory (= :1 L)}]))
        extends (map :superclass (find-all-items-by-type other :extends))
        pk-set (into #{} (for [{is-pk :pk
                                pk-name :name} attributes-tokens-list
                               :when is-pk]
                           pk-name))]
    [name {:attributes attributes
           :handle (hg-add-node name)
           :extends extends
           :pk-set pk-set
           :instance-counter (atom 0)}]))


(defn create-association
  [{name :name
    roles :roles
    description :description}]
  (let [roles (into {} (for [{description :description
                              target-class :target-class
                              name :name
                              U :U
                              L :L} roles]
                         [name {:description description
                                :target-class target-class
                                :unique (= :1 U)
                                :mandatory (= :1 L)}]))]
    [name {:description description
           :handle (hg-add-node name)
           :roles roles
           :roles-order (-> roles keys vec)
           :instance-counter (atom 0)}]))


(defn create-model
  [configuration-list]
  (let [representation-mappings (into {} (for [{variable-type :variable-type
                                                representation :representation}
                                               (find-all-items-by-type configuration-list :representation)]
                                           [variable-type representation]))
        classes (into {} (map #(create-class % representation-mappings)
                              (find-all-items-by-type configuration-list :class)))
        classes (apply merge-with merge-concat classes (for [[extended-by class-config] classes
                                                             class-name (:extends class-config)]
                                                         {class-name {:extended-by [extended-by]}}))
        associations (into {} (map create-association (find-all-items-by-type configuration-list :association)))]
    {:classes classes
     :associations associations}))
