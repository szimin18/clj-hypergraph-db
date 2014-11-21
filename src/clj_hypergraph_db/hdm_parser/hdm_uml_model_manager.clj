(ns clj_hypergraph_db.hdm_parser.hdm_uml_model_manager
  (:require [clj_hypergraph_db.persistance.persistance_manager :refer :all]))


(def model (atom nil))


(defn set-model
  [persistance-model]
  (reset! model persistance-model))


;
; load model
;


(defn get-target-class-of-role
  [association-name role-name]
  (-> @model :associations association-name :roles role-name :target-class))

(defn get-roles-index
  [association-name role-name]
  (->> role-name (.indexOf (-> @model :associations association-name :roles-order)) inc))


(defn get-class-and-all-subclasses-list
  [class-name]
  (loop [result []
         subclasses [class-name]]
    (if (not-empty subclasses)
      (recur (concat result subclasses) (apply concat (map #(-> @model :classes % :extended-by) subclasses)))
      result)))


(defn get-class-and-all-superclasses-list
  [class-name]
  (loop [result []
         superclasses [class-name]]
    (if (not-empty superclasses)
      (recur (concat result superclasses) (apply concat (map #(-> @model :classes % :extends) superclasses)))
      result)))


(defn get-pk-list
  [class-name]
  (for [class-name (get-class-and-all-superclasses-list class-name)
        pk-name (-> @model :classes class-name :pk-set)]
    pk-name))


(defn add-class-instance-return-with-link
  [class-name]
  (let [{class-handle :handle
         instance-counter :instance-counter} (-> @model :classes class-name)
        instance-handle (hg-add-node class-name)
        instance-link (-> @instance-counter str keyword (hg-add-link [class-handle instance-handle]))]
    (swap! instance-counter inc)
    [instance-handle instance-link]))


(defn add-class-instance
  [class-name]
  (first (add-class-instance-return-with-link class-name)))


(defn add-attribute-instance
  [class-instance-handle attribute-name attribute-data]
  (hg-add-link attribute-name [class-instance-handle (hg-add-node attribute-data)]))


(defn add-association-instance
  [association-name]
  (let [{instance-counter :instance-counter
         roles-order :roles-order
         association-handle :handle} (-> @model :associations association-name)
        instance-value (-> @instance-counter str keyword)]
    (swap! instance-counter inc)
    (hg-add-link instance-value (repeat (inc (count roles-order)) association-handle))))


(defn replace-role-target
  [association-instance-handle from-handle to-handle]
  (let [association-instance (hg-get association-instance-handle)
        association-instance-value (hg-link-value association-instance)
        new-targets-list (for [target-index (range (hg-link-arity association-instance))]
                           (let [value (hg-link-target-at association-instance target-index)]
                             (if (= value from-handle) to-handle value)))]
    (hg-remove association-instance-handle)
    (hg-add-link association-instance-value new-targets-list)))


(defn add-role-instance
  [association-instance-handle association-name role-name role-target-handle]
  (let [roles-order-vector (-> @model :associations association-name :roles-order)
        association-instance (hg-get association-instance-handle)
        association-instance-value (hg-link-value association-instance)
        index-to-replace (inc (.indexOf roles-order-vector role-name))
        new-targets-list (for [target-index (range (hg-link-arity association-instance))]
                           (if (= target-index index-to-replace)
                             role-target-handle
                             (hg-link-target-at association-instance target-index)))]
    (hg-remove association-instance-handle)
    (hg-add-link association-instance-value new-targets-list)))


(declare get-class-instance-by-attributes)


(defn add-role-instance-pk
  [association-instance-handle association-name role-name pk-value]
  (let [role-target-handle (or
                             (some (fn [class-name]
                                     (get-class-instance-by-attributes class-name {(-> class-name get-pk-list first) pk-value}))
                                   (get-class-and-all-subclasses-list (get-target-class-of-role association-name role-name)))
                             (let [shell-class-name (get-target-class-of-role association-name role-name)
                                   [shell-instance-node-handle shell-instance-link-handle] (add-class-instance-return-with-link shell-class-name)]
                               (add-attribute-instance shell-instance-node-handle (first (get-pk-list shell-class-name)) pk-value)
                               shell-instance-link-handle))]
    (add-role-instance association-instance-handle association-name role-name role-target-handle)))


;
; iterate over model
;


(defn get-instance-attributes
  [instance-handle attribute-name]
  (let [instance-link (hg-get instance-handle)]
    (for [instance-handle-target (range 1 (hg-link-arity instance-link))
          attribute-handle (hg-find-all (hg-eq attribute-name) (hg-incident (hg-link-target-at instance-link instance-handle-target)))]
      (-> attribute-handle hg-get hg-link-first-target hg-get))))


(defn get-instance-by-number
  [handle number]
  (hg-find-one (-> number str keyword hg-eq) (hg-incident handle)))


(defn iterator-get
  [{handle :handle
    counter :counter}]
  (get-instance-by-number handle @counter))


(defn check-associated-with-satisfied
  [instance-handle {iterator :path-instance-iterator
                    association-name :association-name
                    target-role-index :target-role-index
                    path-role-index :path-role-index}]
  (when-let [path-handle (iterator-get @iterator)]
      (hg-find-one (-> @model :associations association-name :handle (hg-incident-at 0))
                   (hg-incident-at instance-handle target-role-index)
                   (hg-incident-at path-handle path-role-index))))


(defn iterator-next
  [{counter-atom :counter
    handle :handle
    max-instances :max-instances
    associated-with-list :associated-with}]
  (let [max-instances @max-instances]
    (when-not (= max-instances @counter-atom)
      (swap! counter-atom inc))
    (while (when-not (= max-instances @counter-atom)
             (if-let [instance-handle (get-instance-by-number handle @counter-atom)]
               (not-every? #(check-associated-with-satisfied instance-handle %) associated-with-list)
               true))
      (swap! counter-atom inc))
    (get-instance-by-number handle @counter-atom)))


(defn iterator-lazy-seq [iterator]
  (take-while identity (repeatedly #(iterator-next iterator))))


(defn iterator-reset
  [{counter :counter}]
  (reset! counter -1))


(defn iterator-create-class
  [class-name & associated-with]
  (let [{handle :handle
         max-instances :instance-counter} (-> @model :classes class-name)]
    {:counter (atom -1)
     :handle handle
     :max-instances max-instances
     :associated-with associated-with}))


(defn iterator-create-association
  [association-name]
  (let [{handle :handle
         max-instances :instance-counter} (-> @model :associations association-name)]
    {:counter (atom -1)
     :handle handle
     :max-instances max-instances}))


(defn iterator-create
  "
  :class        class-name        associated-with-list
  :association  association-name
  "
  [iteration-type & args]
  (let [arg1 (first args)
        arg2 (second args)
        iterate-token (case iteration-type
                        :class (-> @model :classes arg1)
                        :association (-> @model :associations arg1))
        iterator {:counter (atom -1)
                  :handle (:handle iterate-token)
                  :max-instances (:instance-counter iterate-token)}
        iterator (if (#{:class} iteration-type)
                   (assoc iterator :associated-with arg2)
                   iterator)]
    iterator))


(defn associated-with-create
  [target-role-name association-name path-role-name path-instance-iterator]
  (let [roles-order-vector (-> @model :associations association-name :roles-order)]
    {:target-role-index (->> target-role-name (.indexOf roles-order-vector) inc)
     :association-name association-name
     :path-role-index (->> path-role-name (.indexOf roles-order-vector) inc)
     :path-instance-iterator path-instance-iterator}))


(defn instance-contains-attribute
  [instance-handle attribute-name attribute-value]
  (contains? (set (get-instance-attributes instance-handle attribute-name)) attribute-value))


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
