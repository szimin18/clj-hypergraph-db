(ns clj_hypergraph_db.hdm_parser.hdm_uml_model_manager
  (:import [org.hypergraphdb HGQuery$hg]
           [org.hypergraphdb.query And]
           [org.hypergraphdb.algorithms HGBreadthFirstTraversal SimpleALGenerator])
  (:require [clj_hypergraph_db.hdm_parser.hdm_uml_model_parser :refer :all]
            [clj_hypergraph_db.persistance.persistance_manager :refer :all]))


(def model (atom nil))


(defn set-model
  [persistance-model]
  (reset! model persistance-model))


(defn add-class-instance
  [class-name]
  (let [class-handle (:handle ((:classes @model) class-name))
        instance-handle (add-node class-name)
        instance-counter (((@model :classes) class-name) :instance-counter)]
    (add-link (keyword (str @instance-counter)) (list class-handle instance-handle))
    (swap! instance-counter inc)
    instance-handle))


(defn get-all-attributes
  [class-name]
  (let [current-class ((:classes @model) class-name)]
    (apply merge (cons (:attributes current-class)
                       (map get-all-attributes (:extends current-class))))))


(defn add-attribute-instance
  [class-instance-handle class-name attribute-name attribute-data]
  (let [attribute-handle (:handle ((get-all-attributes class-name) attribute-name))
        instance-handle (add-node attribute-data)]
    (add-link :attribute-instance (list attribute-handle instance-handle))
    (add-link attribute-name (list class-instance-handle instance-handle))
    instance-handle))


(defn add-association-instance
  [association-name]
  (let [association-handle (:handle ((:associations @model) association-name))
        instance-handle (add-node association-name)
        instance-counter (((@model :associations) association-name) :instance-counter)]
    (add-link (keyword (str @instance-counter)) (list association-handle instance-handle))
    (swap! instance-counter inc)
    instance-handle))


(defn add-role-instance
  [association-instance-handle association-name role-name role-target-handle]
  (let [role-handle (:handle ((:roles ((:associations @model) association-name)) role-name))
        instance-handle (add-link role-name (list association-instance-handle role-target-handle))]
    (add-link :role-instance (list role-handle instance-handle))
    instance-handle))

;TODO change body for adding roles using pk
(defn add-role-instance-pk
  [association-instance-handle association-name role-name role-target-handle]
  (let [role-handle (:handle ((:roles ((:associations @model) association-name)) role-name))
        instance-handle (add-link role-name (list association-instance-handle role-target-handle))]
    (add-link :role-instance (list role-handle instance-handle))
    instance-handle))


(defn get-target-class-of-role
  [association-name role-name]
  (((((@model :associations) association-name) :roles) role-name) :target-class))


(defn get-subclasses-list
  [class-name]
  (((@model :classes) class-name) :extended-by))


(defn get-number-of-class-instances
  [class-name]
  @(((@model :classes) class-name) :instance-counter))


(defn get-class-instance-by-number
  [class-name instance-number]
  (HGQuery$hg/findOne
    @hypergraph
    (And. (HGQuery$hg/eq (keyword (str instance-number))) (HGQuery$hg/incident (((@model :classes) class-name) :handle)))))


(defn get-number-of-association-instances
  [association-name]
  @(((@model :associations) association-name) :instance-counter))
