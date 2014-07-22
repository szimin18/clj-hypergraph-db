(ns clj_hypergraph_db.hdm_parser.hdm_uml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.persistance.persistance_manager :refer :all]))


;
; create model from scratch
;


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
     :mandatory mandatory}))


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
     :pk pk}))


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
     :mandatory mandatory}))


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
     :roles roles}))


(defn create-model
  [configuration-list]
  (let [metaclass-handle (add-node :metaclass)
        representation-mappings (reduce
                                  #(assoc %1 (:variable-type %2) (:representation %2))
                                  {}
                                  (find-all-items-by-type configuration-list :representation))
        classes (reduce
                  #(assoc %1 (:name %2) (create-class %2 metaclass-handle representation-mappings))
                  {}
                  (find-all-items-by-type configuration-list :class))
        associations (reduce
                       #(assoc %1 (:name %2) (create-association %2 metaclass-handle classes))
                       {}
                       (find-all-items-by-type configuration-list :association))]
    {:classes classes
     :associations associations}))
