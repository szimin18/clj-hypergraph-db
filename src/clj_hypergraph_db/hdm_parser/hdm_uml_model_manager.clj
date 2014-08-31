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


(defn get-target-class-of-role
  [association-name role-name]
  (((((@model :associations) association-name) :roles) role-name) :target-class))


(defn get-subclasses-list
  [class-name]
  (((@model :classes) class-name) :extended-by))


(defn get-class-and-all-subclasses-list
  [class-name]
  (let [result (atom (list class-name))
        subclasses (atom (get-subclasses-list class-name))]
    (while (not-empty @subclasses)
      (swap! result concat @subclasses)
      (swap! subclasses #(apply concat (map get-subclasses-list %))))
    @result))


(defn get-superclasses-list
  [class-name]
  (((@model :classes) class-name) :extends))


(defn get-class-and-all-superclasses-list
  [class-name]
  (let [result (atom (list class-name))
        superclasses (atom (get-superclasses-list class-name))]
    (while (not-empty @superclasses)
      (swap! result concat @superclasses)
      (swap! superclasses #(apply concat (map get-superclasses-list %))))
    @result))


(defn get-pk-list
  [class-name]
  (apply concat (map #(:pk ((:classes @model) %)) (get-class-and-all-superclasses-list class-name))))


(defn add-class-instance-return-with-link
  [class-name]
  (let [class-handle (:handle ((:classes @model) class-name))
        instance-handle (add-node class-name)
        instance-counter (((@model :classes) class-name) :instance-counter)
        instance-link (add-link (keyword (str @instance-counter)) (list class-handle instance-handle))]
    (swap! instance-counter inc)
    [instance-handle instance-link]))


(defn add-class-instance
  [class-name]
  (first (add-class-instance-return-with-link class-name)))


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


(declare get-class-instance-by-attributes)


(defn add-role-instance-pk
  [association-instance-handle association-name role-name pk-value]
  (let [role-target-handle (first (filter identity (map
                                                     #(get-class-instance-by-attributes % {(first (get-pk-list %)) pk-value})
                                                     (get-class-and-all-subclasses-list (get-target-class-of-role association-name role-name)))))
        role-target-handle (if role-target-handle
                             role-target-handle
                             (let [shell-class (get-target-class-of-role association-name role-name)
                                   [shell-instance-node-handle shell-instance-link-handle] (add-class-instance-return-with-link shell-class)]
                               (add-attribute-instance shell-instance-node-handle shell-class (first (get-pk-list shell-class)) pk-value)
                               shell-instance-link-handle))
        role-handle (:handle ((:roles ((:associations @model) association-name)) role-name))
        instance-handle (add-link role-name (list association-instance-handle role-target-handle))]
    (add-link :role-instance (list role-handle instance-handle))
    instance-handle))


;
; iterate over model
;


(defn get-instance-extensions-handles
  [instance-handle extension-name]
  (let [hypergraph (get-hypergraph)
        instance-link (.get hypergraph instance-handle)]
    (for [instance-handle-target (range 1 (.getArity instance-link))
          attribute-handle (HGQuery$hg/findAll
                             hypergraph
                             (And.
                               (HGQuery$hg/eq extension-name)
                               (HGQuery$hg/incident (.getTargetAt instance-link instance-handle-target))))]
      (.getTargetAt (.get hypergraph attribute-handle) 1))))


(defn get-instance-extensions
  [instance-handle extension-name]
  (let [hypergraph (get-hypergraph)]
    (map #(.get hypergraph %) (get-instance-extensions-handles instance-handle extension-name))))


(defn get-instance-by-number
  [handle number]
  (HGQuery$hg/findOne (get-hypergraph) (And. (HGQuery$hg/eq (keyword (str number))) (HGQuery$hg/incident handle))))


(declare check-associated-with-satisfied)


(defn iterator-next
  [iterator]
  (let [counter-atom (:counter iterator)
        handle (:handle iterator)
        max-instances @(:max-instances iterator)
        associated-with-list (:associated-with iterator)]
    (if (not= max-instances @counter-atom)
      (swap! counter-atom inc))
    (while (if (not= max-instances @counter-atom)
             (if-let [instance-handle (get-instance-by-number handle @counter-atom)]
               (not-every? #(check-associated-with-satisfied instance-handle %) associated-with-list)
               true)
             false)
      (swap! counter-atom inc))
    (get-instance-by-number handle @counter-atom)))


(defn iterator-reset
  [iterator]
  (reset! (:counter iterator) -1))


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
                        :association ((@model :associations) (first args))
                        :role ((((@model :associations) (first args)) :roles) (second args)))
        iterator {:counter (atom -1)
                  :handle (:handle iterate-token)
                  :max-instances (:instance-counter iterate-token)}
        iterator (if (= :class iteration-type)
                   (assoc iterator :associated-with (second args))
                   iterator)]
    iterator))


(defn check-associated-with-satisfied
  [instance-handle associated-with]
  (let [handle1 instance-handle
        role1 (:target-role associated-with)
        association-name (:association-name associated-with)
        role2 (:path-role associated-with)
        handle2 (iterator-get @(:iterator (:path-instance associated-with)))
        return (atom false)
        association-iterator (iterator-create :association association-name)
        association-instance (atom (iterator-next association-iterator))]
    (while (and (not @return) @association-instance)
      (let [role1-extent (apply hash-set (get-instance-extensions-handles @association-instance role1))
            role2-extent (apply hash-set (get-instance-extensions-handles @association-instance role2))]
        (if (and (contains? role1-extent handle1) (contains? role2-extent handle2))
          (reset! return true)
          (reset! association-instance (iterator-next association-iterator)))))
    @return))


(defn instance-contains-attribute
  [instance-handle attribute-name attribute-value]
  (contains? (apply hash-set (get-instance-extensions instance-handle attribute-name)) attribute-value))


(defn get-class-instance-by-attributes
  [class-name attributes-map]
  (let [class-iterator (iterator-create :class class-name)
        current-instance (atom (iterator-next class-iterator))
        instance-accepted (atom false)]
    (while (and @current-instance (not @instance-accepted))
      (if (every? #(instance-contains-attribute @current-instance (first %) (second %)) attributes-map)
        (reset! instance-accepted true)
        (reset! current-instance (iterator-next class-iterator))))
    @current-instance))
