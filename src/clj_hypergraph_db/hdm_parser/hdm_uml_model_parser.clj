(ns clj_hypergraph_db.hdm_parser.hdm_uml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.common_parser.common_functions :refer :all]
            [clj_hypergraph_db.persistance.persistance_manager :refer :all]))


(defn create-attribute
  [attribute-config representation-mappings]
  {:representation (or (representation-mappings (:variable-type attribute-config)) String)
   :unique (= :1 (:U attribute-config))
   :mandatory (= :1 (:L attribute-config))})


(defn create-class
  [class-config representation-mappings]
  (let [other-config (:other class-config)
        attributes-tokens-list (find-all-items-by-type other-config :attribute)
        attributes (into {} (for [attribute-config attributes-tokens-list]
                              [(:name attribute-config) (create-attribute attribute-config representation-mappings)]))
        extends (map :superclass (find-all-items-by-type other-config :extends))
        pk-set (into #{} (for [pk-attribute attributes-tokens-list
                               :when (:pk pk-attribute)]
                           (:name pk-attribute)))]
    {:attributes attributes
     :handle (hg-add-node (:name class-config))
     :extends extends
     :pk-set pk-set
     :instance-counter (atom 0)}))


(defn create-role
  [role-config]
  {:description (:description role-config)
   :target-class (:target-class role-config)
   :unique (= :1 (:U role-config))
   :mandatory (= :1 (:L role-config))})


(defn create-association
  [association-config]
  (let [roles (into {} (for [role-config (:roles association-config)]
                         [(:name role-config) (create-role role-config)]))]
    {:description (:description association-config)
     :handle (hg-add-node (:name association-config))
     :roles roles
     :roles-order (-> roles keys vec)
     :instance-counter (atom 0)}))


(defn create-model
  [configuration-list]
  (let [representation-mappings (into {} (for [mapping-config (find-all-items-by-type configuration-list :representation)]
                                           [(:variable-type mapping-config) (:representation mapping-config)]))
        classes (into {} (for [class-config (find-all-items-by-type configuration-list :class)]
                           [(:name class-config) (create-class class-config representation-mappings)]))
        classes (apply merge-with merge-concat classes (for [[extended-by class-config] classes
                                                             class-name (:extends class-config)]
                                                         {class-name {:extended-by [extended-by]}}))
        associations (into {} (for [association-config (find-all-items-by-type configuration-list :association)]
                                [(:name association-config) (create-association association-config)]))]
    {:classes classes
     :associations associations}))
