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
  (-> model deref :associations association-name :roles role-name :target-class))


(defn get-subclasses-list
  [class-name]
  (-> model deref :classes class-name :extended-by))


(defn get-class-and-all-subclasses-list
  [class-name]
  (let [result (atom [class-name])
        subclasses (atom (get-subclasses-list class-name))]
    (while (not-empty @subclasses)
      (swap! result concat @subclasses)
      (swap! subclasses #(->> % (map get-subclasses-list) (apply concat))))
    @result))


(defn get-superclasses-list
  [class-name]
  (-> model deref :classes class-name :extends))


(defn get-class-and-all-superclasses-list
  [class-name]
  (let [result (atom [class-name])
        superclasses (atom (get-superclasses-list class-name))]
    (while (not-empty @superclasses)
      (swap! result concat @superclasses)
      (swap! superclasses #(->> % (map get-superclasses-list) (apply concat))))
    @result))


(defn get-pk-list
  [class-name]
  (for [class-name (get-class-and-all-superclasses-list class-name)
        pk (-> model deref :classes class-name :pk)]
    pk))


(defn add-class-instance-return-with-link
  [class-name]
  (let [current-class (-> model deref :classes class-name)
        class-handle (:handle current-class)
        instance-counter (:instance-counter current-class)
        instance-handle (add-node class-name)
        instance-link (-> instance-counter deref str keyword (add-link [class-handle instance-handle]))]
    (swap! instance-counter inc)
    [instance-handle instance-link]))


(defn add-class-instance
  [class-name]
  (-> class-name add-class-instance-return-with-link first))


(defn add-attribute-instance
  [class-instance-handle class-name attribute-name attribute-data]
  (let [current-attribute (some #(get (-> model deref :classes % :attributes) attribute-name)
                                (get-class-and-all-superclasses-list class-name))
        attribute-handle (:handle current-attribute)
        instance-counter (:instance-counter current-attribute)
        instance-handle (add-node attribute-data)]
    (-> instance-counter deref str keyword (add-link [attribute-handle instance-handle]))
    (-> attribute-name (add-link [class-instance-handle instance-handle]))
    (swap! instance-counter inc)
    instance-handle))


(defn add-association-instance
  [association-name]
  (let [current-association (-> model deref :associations association-name)
        association-handle (:handle current-association)
        instance-counter (:instance-counter current-association)
        instance-handle (add-node association-name)]
    (-> instance-counter deref str keyword (add-link [association-handle instance-handle]))
    (swap! instance-counter inc)
    instance-handle))


(defn add-role-instance
  [association-instance-handle association-name role-name role-target-handle]
  (let [current-role (-> model deref :associations association-name :roles role-name)
        role-handle (:handle current-role)
        instance-counter (:instance-counter current-role)
        instance-handle (add-link role-name [association-instance-handle role-target-handle])]
    (-> instance-counter deref str keyword (add-link [role-handle instance-handle]))
    (swap! instance-counter inc)
    instance-handle))


(declare get-class-instance-by-attributes)


(defn add-role-instance-pk
  [association-instance-handle association-name role-name pk-value]
  (let [role-target-handle (if-let [role-target-handle (some #(get-class-instance-by-attributes % {(-> % get-pk-list first) pk-value})
                                                             (get-class-and-all-subclasses-list (get-target-class-of-role association-name role-name)))]
                             role-target-handle
                             (let [shell-class-name (get-target-class-of-role association-name role-name)
                                   [shell-instance-node-handle shell-instance-link-handle] (add-class-instance-return-with-link shell-class-name)]
                               (add-attribute-instance shell-instance-node-handle shell-class-name (first (get-pk-list shell-class-name)) pk-value)
                               shell-instance-link-handle))
        role-handle (-> model deref :associations association-name :roles role-name :handle)
        instance-handle (add-link role-name [association-instance-handle role-target-handle])]
    (add-link :role-instance [role-handle instance-handle])
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
    (for [extension-handle (get-instance-extensions-handles instance-handle extension-name)]
      (.get hypergraph extension-handle))))


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
    (if-not (= max-instances @counter-atom)
      (swap! counter-atom inc))
    (while (if-not (= max-instances @counter-atom)
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
  (let [arg1 (first args)
        arg2 (second args)
        iterate-token (case iteration-type
                        :class (-> model deref :classes arg1)
                        :attribute (-> model deref :classes arg1 :attributes arg2)
                        :association (-> model deref :associations arg1)
                        :role (-> model deref :associations arg1 :roles arg2))
        iterator {:counter (atom -1)
                  :handle (:handle iterate-token)
                  :max-instances (:instance-counter iterate-token)}
        iterator (if (#{:class} iteration-type)
                   (assoc iterator :associated-with arg2)
                   iterator)]
    iterator))


(defn check-associated-with-satisfied
  [instance-handle associated-with]
  (let [handle1 instance-handle
        role1 (:target-role associated-with)
        association-name (:association-name associated-with)
        role2 (:path-role associated-with)
        handle2 (-> associated-with :path-instance :iterator deref iterator-get)
        return (atom false)
        association-iterator (iterator-create :association association-name)
        association-instance (atom (iterator-next association-iterator))]
    (while (and (not @return) @association-instance)
      (if (and (contains? (apply hash-set (get-instance-extensions-handles @association-instance role1)) handle1)
               (contains? (apply hash-set (get-instance-extensions-handles @association-instance role2)) handle2))
        (reset! return true)
        (reset! association-instance (iterator-next association-iterator))))
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
