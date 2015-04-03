(ns clj_hypergraph_db.hdm_parser.hdm_uml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.common_parser.common_functions :refer :all]
            [clj_hypergraph_db.persistance.persistance_manager :refer :all])
  (:use [korma.db]
        [korma.core]))


;
; attribute
;


(defn create-attribute-table
  [database-specification class-table-symbol table-symbol]
  (let [id-column-name (str (name class-table-symbol) "_id")]
    (create-table
      database-specification
      table-symbol
      [:id "INTEGER" "NOT NULL" "PRIMARY KEY" "AUTOINCREMENT"]
      [id-column-name "INTEGER" "NOT NULL"]
      [:value "VARCHAR(255)" "NOT NULL"])
    (eval `(defentity ~table-symbol
                      (belongs-to ~class-table-symbol)))))


(defn inherit-attributes
  [old-classes-map
   [class-name]]
  (update-in
    old-classes-map
    [class-name :attributes]
    (fn [old-attributes]
      (merge
        (apply
          merge
          (reverse (map
                     #(-> old-classes-map % :attributes)
                     (loop [result (list)
                            superclasses [class-name]]
                       (if (not-empty superclasses)
                         (recur (concat result superclasses) (apply concat (map #(-> old-classes-map % :extends) superclasses)))
                         result)))))
        old-attributes))))


(defn inject-korma-to-attributes
  [attributes database-specification class-table-symbol class-name]
  (reduce
    (fn [attributes attribute-name]
      (let [korma-symbol (gensym (str (name attribute-name) (name class-name)))]
        (create-attribute-table database-specification class-table-symbol korma-symbol)
        (update-in attributes [attribute-name] assoc :korma-symbol korma-symbol)))
    attributes
    (keys attributes)))


(defn create-attribute
  [{variable-type :variable-type
    attribute-name :name
    U :U
    L :L}
   representation-mappings]
  [attribute-name
   {:representation (or (representation-mappings variable-type) String)
    :unique (= :1 U)
    :mandatory (= :1 L)}])


;
; class
;


(defn create-class-table
  [database-specification table-symbol]
  (create-table
    database-specification
    table-symbol
    [:id "INTEGER" "NOT NULL" "PRIMARY KEY"])
  (eval `(defentity ~table-symbol)))


(defn create-class
  [{class-name :name
    other :other}
   representation-mappings
   database-specificatin]
  (let [attributes-tokens-list (find-all-items-by-type other :attribute)
        extends (map :superclass (find-all-items-by-type other :extends))
        pk-set (set (map :name (filter :pk attributes-tokens-list)))
        korma-symbol (gensym (name class-name))]
    (create-class-table database-specificatin korma-symbol)
    (let [attributes (into {} (for [attribute-token attributes-tokens-list]
                                (create-attribute attribute-token representation-mappings)))]
      [class-name {:attributes attributes
                   :korma-symbol korma-symbol
                   :extends extends
                   :pk-set pk-set
                   :instance-counter (atom 0)}])))


;
; association
;


(defn create-association-table
  [database-specificatin table-symbol roles-data]
  (let []
    (apply
      create-table
      database-specificatin
      table-symbol
      [:id "INTEGER" "NOT NULL" "PRIMARY KEY" "AUTOINCREMENT"]
      (for [[role-name] roles-data]
        [role-name "INTEGER"])))
  (eval `(defentity ~table-symbol
                    ~@(map #(list `belongs-to (second %) {:fk (first %)}) roles-data))))


(defn create-association
  [{association-name :name
    roles :roles
    description :description}
   database-specificatin
   classes]
  (let [roles (into {} (for [{description :description
                              target-class :target-class
                              role-name :name
                              U :U
                              L :L} roles]
                         [role-name {:description description
                                     :target-class target-class
                                     :unique (= :1 U)
                                     :mandatory (= :1 L)}]))
        korma-symbol (gensym (name association-name))]
    (create-association-table
      database-specificatin
      korma-symbol
      (for [[role-name {target-class :target-class}] roles]
        [role-name (-> classes target-class :korma-symbol)]))
    [association-name {:description description
                       :korma-symbol korma-symbol
                       :roles roles
                       :roles-order (-> roles keys vec)
                       :instance-counter (atom 0)}]))


;
; model
;


(defn create-model
  [configuration-list database-specification]
  (let [representation-mappings (into {} (for [{variable-type :variable-type
                                                representation :representation}
                                               (find-all-items-by-type configuration-list :representation)]
                                           [variable-type representation]))
        classes (into {} (map #(create-class % representation-mappings database-specification)
                              (find-all-items-by-type configuration-list :class)))
        classes (apply merge-with merge-concat classes (for [[extended-by class-config] classes
                                                             class-name (:extends class-config)]
                                                         {class-name {:extended-by (list extended-by)}}))
        classes (reduce inherit-attributes classes classes)
        classes (reduce
                  (fn [old-classes class-name]
                    (update-in old-classes [class-name :attributes]
                               inject-korma-to-attributes
                               database-specification
                               (-> classes class-name :korma-symbol)
                               class-name))
                  classes
                  (keys classes))
        associations (into {} (map #(create-association % database-specification classes)
                                   (find-all-items-by-type configuration-list :association)))]
    {:classes classes
     :associations associations}))
