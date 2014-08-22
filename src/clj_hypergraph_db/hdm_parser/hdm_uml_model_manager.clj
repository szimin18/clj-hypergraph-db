(ns clj_hypergraph_db.hdm_parser.hdm_uml_model_manager
  (:import [org.hypergraphdb HGQuery$hg HGValueLink]
           [org.hypergraphdb.query And]
           [org.hypergraphdb.algorithms HGBreadthFirstTraversal SimpleALGenerator])
  (:require [clj_hypergraph_db.hdm_parser.hdm_uml_model_parser :refer :all]
            [clj_hypergraph_db.persistance.persistance_manager :refer :all]))


(def model (atom nil))


(defn set-model
  [persistance-model]
  (reset! model persistance-model))


;
; load model
;


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
        instance-handle (add-node attribute-data)
        instance-counter (((get-all-attributes class-name) attribute-name) :instance-counter)]
    (add-link (keyword (str @instance-counter)) (list attribute-handle instance-handle))
    (add-link attribute-name (list class-instance-handle instance-handle))
    (swap! instance-counter inc)
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
        instance-handle (add-link role-name (list association-instance-handle role-target-handle))
        instance-counter (((((@model :associations) association-name) :roles) role-name) :instance-counter)]
    (add-link (keyword (str @instance-counter)) (list role-handle instance-handle))
    (swap! instance-counter inc)
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


;
; iterate over model
;


(defn get-instance-attributes
  [handle attribute-name]
  (let [hypergraph (get-hypergraph)]
    (map
      #(.get hypergraph (.getTargetAt (.get hypergraph %) 1))
      (HGQuery$hg/findAll hypergraph (And.
                                       (HGQuery$hg/eq attribute-name)
                                       (HGQuery$hg/incident (.getTargetAt (.get hypergraph handle) 1)))))))


(defn get-instance-by-number
  [handle number]
  (HGQuery$hg/findOne (get-hypergraph) (And. (HGQuery$hg/eq (keyword (str number))) (HGQuery$hg/incident handle))))


(defn check-associated-with-satisfied
  ;[handle1 role1-name association-name role2-name handle2]
  ;(:target-role %)
  ;(:association-name %)
  ;(:path-role %)
  ;@(:path-instance %)
  [instance-handle associated-with]
  true)


(defn iterator-next
  [iterator]
  (let [counter (:counter iterator)
        handle (:handle iterator)
        max-instances @(:max-instances iterator)
        associated-with-list (:associated-with iterator)]
    (if (not= max-instances @counter)
      (swap! counter inc))
    (while (if (not= max-instances @counter)
             (if-let [instance-handle (get-instance-by-number handle @counter)]
               (not (zero? (count (filter #(not (check-associated-with-satisfied instance-handle %)) associated-with-list))))
               false)
             false)
      (swap! counter inc))
    (get-instance-by-number handle @counter)))


(defn iterator-get
  [iterator]
  (get-instance-by-number (:handle iterator) @(:counter iterator)))


(defn iterator-create
  "
  :class        class-name        associated-with-list
  :attribute    class-name        attribute-name
  :association  association-name
  :role         association-name  role-name
  "
  [iteration-type & args]
  (let [iterate-token (case iteration-type
                        :class ((@model :classes) (first args))
                        :attribute ((((@model :classes) (first args)) :attributes) (second args))
                        :association ((@model :association) (first args))
                        :role ((((@model :associations) (first args)) :roles) (second args)))
        iterator {:counter (atom -1)
                  :handle (:handle iterate-token)
                  :max-instances (:instance-counter iterate-token)}
        iterator (if (= :class iteration-type)
                   (assoc iterator :associated-with (second args))
                   iterator)]
    iterator))
