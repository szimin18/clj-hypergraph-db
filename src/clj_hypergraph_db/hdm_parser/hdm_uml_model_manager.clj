(ns clj_hypergraph_db.hdm_parser.hdm_uml_model_manager
  (:import [org.hypergraphdb HGQuery$hg]
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
        instance-handle (add-node class-name)]
    (add-link :instance (list class-handle instance-handle))
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
    (add-link :instance (list attribute-handle instance-handle))
    (add-link attribute-name (list class-instance-handle instance-handle))
    instance-handle))


(defn add-association-instance
  [association-name]
  (let [association-handle (:handle ((:associations @model) association-name))
        instance-handle (add-node association-name)]
    (add-link :instance (list association-handle instance-handle))
    instance-handle))


(defn add-role-instance
  [association-instance-handle association-name role-name role-target-handle]
  (let [role-handle (:handle ((:roles ((:associations @model) association-name)) role-name))
        instance-handle (add-link role-name (list association-instance-handle role-target-handle))]
    (add-link :instance (list role-handle instance-handle))
    instance-handle))

;TODO change body for adding roles using pk
(defn add-role-instance-pk
  [association-instance-handle association-name role-name role-target-handle]
  (let [role-handle (:handle ((:roles ((:associations @model) association-name)) role-name))
        instance-handle (add-link role-name (list association-instance-handle role-target-handle))]
    (add-link :instance (list role-handle instance-handle))
    instance-handle))


(defn get-class-instances
  [class-name]
  (let [hypergraph (get-hypergraph-instance)
        instances-list (atom [])]
    (doseq [handle (.findAll hypergraph (HGQuery$hg/eq class-name))]
      (let [traversal (HGBreadthFirstTraversal. handle (SimpleALGenerator. hypergraph) 1)
            attributes-map (atom {})]
        (while (.hasNext traversal)
          (let [pair (.next traversal)
                link (.get hypergraph (.getFirst pair))
                node (.get hypergraph (.getSecond pair))]
            (try
              (let [value (.getValue link)]
                (if (not (contains? #{:class :instance :role :attribute :Domain :AdminDomain} value))
                  (swap! attributes-map assoc value node)))
              (catch Exception e))))
        (if (not-empty @attributes-map)
          (swap! instances-list conj @attributes-map))))
    @instances-list))
