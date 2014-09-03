(ns clj_hypergraph_db.hdm_parser.hdm_uml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.persistance.persistance_manager :refer :all]))


(defn create-attribute
  [attribute-config representation-mappings]
  (let [representation (if-let [repr (representation-mappings (:variable-type attribute-config))]
                         repr
                         String)
        unique (= :1 (:U attribute-config))
        mandatory (= :1 (:L attribute-config))]
    {:representation representation
     :unique unique
     :mandatory mandatory}))


(defn create-class
  [class-config representation-mappings]
  (let [class-handle (hg-add-node (:name class-config))
        attributes (reduce
                     (fn [attributes attribute-config]
                       (assoc attributes (:name attribute-config) (create-attribute attribute-config representation-mappings)))
                     {}
                     (find-all-items-by-type (:other class-config) :attribute))
        pk-set (apply hash-set (for [pk-attribute (find-all-items-by-type (:other class-config) :attribute)
                                     :when (:pk pk-attribute)]
                                 (:name pk-attribute)))
        extends (map :superclass (find-all-items-by-type (:other class-config) :extends))]
    {:attributes attributes
     :handle class-handle
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
  (let [roles (reduce
                #(assoc %1 (:name %2) (create-role %2))
                {}
                (:roles association-config))]
    {:description (:description association-config)
     :handle (hg-add-node (:name association-config))
     :roles roles
     :roles-order (-> roles keys vec)
     :instance-counter (atom 0)}))


(defn create-model
  [configuration-list]
  (let [representation-mappings (reduce
                                  (fn [mappings-map mapping-config]
                                    (assoc mappings-map (:variable-type mapping-config) (:representation mapping-config)))
                                  {}
                                  (find-all-items-by-type configuration-list :representation))
        classes (reduce
                  (fn [classes-map class-config]
                    (assoc classes-map (:name class-config) (create-class class-config representation-mappings)))
                  {}
                  (find-all-items-by-type configuration-list :class))
        classes (reduce
                  (fn [classes-map [class-name extends]]
                    (update-in classes-map [extends] #(merge-with concat % {:extended-by (list class-name)})))
                  classes
                  (for [class-name (keys classes)
                        extends (:extends (class-name classes))]
                    [class-name extends]))
        associations (reduce
                       #(assoc %1 (:name %2) (create-association %2))
                       {}
                       (find-all-items-by-type configuration-list :association))]
    {:classes classes
     :associations associations
     :nil-handle (hg-add-node :nil)}))
