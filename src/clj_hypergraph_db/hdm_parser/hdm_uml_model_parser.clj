(ns clj_hypergraph_db.hdm_parser.hdm_uml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.persistance.persistance_manager :refer :all]))


(defn create-attribute
  [attribute-config class-handle representation-mappings]
  (let [representation (if-let [repr (representation-mappings (:variable-type attribute-config))]
                         repr
                         String)
        attribute-handle (add-node (:name attribute-config))
        unique (= :1 (:U attribute-config))
        mandatory (= :1 (:L attribute-config))]
    (add-link :attribute (list class-handle attribute-handle))
    {:handle attribute-handle
     :representation representation
     :unique unique
     :mandatory mandatory
     :instance-counter (atom 0)}))


(defn create-class
  [class-config metaclass-handle representation-mappings]
  (let [class-handle (add-node (:name class-config))
        attributes (reduce
                     #(assoc %1 (:name %2) (create-attribute %2 class-handle representation-mappings))
                     {}
                     (find-all-items-by-type (:other class-config) :attribute))
        pk (apply hash-set (for [pk-attribute (find-all-items-by-type (:other class-config) :attribute)
                                 :when (:pk pk-attribute)]
                             (:name pk-attribute)))
        extends (map :superclass (find-all-items-by-type (:other class-config) :extends))]
    (add-link :class (list metaclass-handle class-handle))
    {:attributes attributes
     :handle class-handle
     :extends extends
     :pk pk
     :instance-counter (atom 0)}))


(defn create-role
  [role-config association-handle classes]
  (let [role-handle (add-node (:name role-config))
        description (:description role-config)
        target-class (:target-class role-config)
        target-class-handle (:handle (target-class classes))
        unique (= :1 (:U role-config))
        mandatory (= :1 (:L role-config))]
    (add-link :role (list association-handle role-handle))
    {:description description
     :handle role-handle
     :target-class target-class
     :target-class-handle target-class-handle
     :unique unique
     :mandatory mandatory
     :instance-counter (atom 0)}))


(defn create-association
  [association-config metaclass-handle classes]
  (let [description (:description association-config)
        association-handle (add-node (:name association-config))
        roles (reduce
                #(assoc %1 (:name %2) (create-role %2 association-handle classes))
                {}
                (:roles association-config))]
    (add-link :association (list metaclass-handle association-handle))
    {:description description
     :handle association-handle
     :roles roles
     :roles-order (-> roles keys vec)
     :instance-counter (atom 0)}))


(defn create-model
  [configuration-list]
  (let [metaclass-handle (add-node :metaclass)
        representation-mappings (reduce
                                  (fn [mappings-map mapping-config]
                                    (assoc mappings-map (:variable-type mapping-config) (:representation mapping-config)))
                                  {}
                                  (find-all-items-by-type configuration-list :representation))
        classes (reduce
                  (fn [classes-map class-config]
                    (assoc classes-map (:name class-config) (create-class class-config metaclass-handle representation-mappings)))
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
                       #(assoc %1 (:name %2) (create-association %2 metaclass-handle classes))
                       {}
                       (find-all-items-by-type configuration-list :association))]
    {:classes classes
     :associations associations
     :nil-handle (add-node :nil)}))
