(ns clj_hypergraph_db.xml_parser.xml_to_uml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]))


(defn eval-path
  ([path input-model]
   (eval-path path [] input-model))
  ([path new-path model]
   (if (zero? (count path))
     new-path
     (let [children-of-input-model (:children model)
           first-of-path (first path)
           new-child-key (first (filter
                                  #(= first-of-path (:name (children-of-input-model %)))
                                  (keys children-of-input-model)))]
       (eval-path
         (rest path)
         (conj new-path new-child-key)
         (children-of-input-model new-child-key))))))


(defn eval-leaf-path
  ([path input-model]
   (eval-leaf-path path [] input-model))
  ([path new-path model]
   (if (<= (count path) 1)
     (let [token-attributes (:attributes model)
           attribute-name (first (filter
                                   #(= (last path) (:name (token-attributes %)))
                                   (keys token-attributes)))]
       [new-path (if attribute-name attribute-name (last path)) (if attribute-name :attribute :text)])
     (let [children-of-input-model (:children model)
           first-of-path (first path)
           new-child-key (first (filter
                                  #(= first-of-path (:name (children-of-input-model %)))
                                  (keys children-of-input-model)))]
       (eval-leaf-path
         (rest path)
         (conj new-path new-child-key)
         (children-of-input-model new-child-key))))))


(defn create-attribute-mapping
  [relative-input-model path class-name attribute-name class-instance-handle]
  (let [stack (atom [])
        model (atom relative-input-model)
        [path name-of-last type-of-last] (eval-leaf-path path relative-input-model)]
    (doseq [path-token path]
      (swap! stack conj (assoc @model :children (dissoc (:children @model) path-token)))
      (swap! model #((:children %) path-token)))
    (case type-of-last
      :attribute (let [attributes (:attributes @model)
                       changed-attribute (merge-with
                                           concat
                                           (attributes name-of-last)
                                           {:add-attribute [{:class-name class-name
                                                             :attribute-name attribute-name
                                                             :instance-handle class-instance-handle}]})]
                   (swap! model assoc :attributes (assoc attributes name-of-last changed-attribute)))
      :text (swap! model #(merge-with concat % {:add-attribute-from-text [{:class-name class-name
                                                                           :attribute-name attribute-name
                                                                           :instance-handle class-instance-handle}]})))
    (doseq [path-token (reverse path)]
      (reset! model (assoc (last @stack) :children (assoc (:children (last @stack)) path-token @model)))
      (swap! stack drop-last))
    @model))


(defn create-add-instance
  [input-model path class-name mappings]
  (let [instance-handle (atom nil)
        stack (atom [])
        model (atom input-model)
        path (eval-path path input-model)]
    (doseq [path-token path]
      (swap! stack conj (assoc @model :children (dissoc (:children @model) path-token)))
      (swap! model #((:children %) path-token)))
    (swap! model #(merge-with concat % {:add-instance [{:class-name class-name
                                                        :instance-handle instance-handle}]}))
    (doseq [mapping (find-all-items-by-type mappings :mapping)]
      (swap! model create-attribute-mapping (:path mapping) class-name (:name mapping) instance-handle))
    (doseq [path-token (reverse path)]
      (reset! model (assoc (last @stack) :children (assoc (:children (last @stack)) path-token @model)))
      (swap! stack drop-last))
    @model))


(defn create-role-mapping
  [relative-input-model path association-name role-name association-instance-handle]
  (let [stack (atom [])
        model (atom relative-input-model)
        path (eval-path path relative-input-model)]
    (doseq [path-token path]
      (swap! stack conj (assoc @model :children (dissoc (:children @model) path-token)))
      (swap! model #((:children %) path-token)))
    (let [target-classes-of-role (atom (list (get-target-class-of-role association-name role-name)))
          target-instance-handle (atom (:instance-handle (first (filter
                                                                  #(=
                                                                    (first @target-classes-of-role)
                                                                    (:class-name %))
                                                                  (:add-instance @model)))))]
      (while (nil? @target-instance-handle)
        (reset! target-classes-of-role (apply concat (map get-subclasses-list @target-classes-of-role)))
        (reset!
          target-instance-handle
          (:instance-handle (first (filter
                                     #(contains? (apply hash-set @target-classes-of-role) (:class-name %))
                                     (:add-instance @model))))))
      (reset! model (merge-with
                      concat
                      @model
                      {:add-role [{:association-name association-name
                                   :role-name role-name
                                   :instance-handle association-instance-handle
                                   :target-instance-handle @target-instance-handle}]})))
    (doseq [path-token (reverse path)]
      (reset! model (assoc (last @stack) :children (assoc (:children (last @stack)) path-token @model)))
      (swap! stack drop-last))
    @model))


(defn create-role-mapping-pk
  [relative-input-model path association-name role-name association-instance-handle]
  (let [stack (atom [])
        model (atom relative-input-model)
        [path name-of-last type-of-last] (eval-leaf-path path relative-input-model)]
    (doseq [path-token path]
      (swap! stack conj (assoc @model :children (dissoc (:children @model) path-token)))
      (swap! model #((:children %) path-token)))
    (case type-of-last
      :attribute (let [attributes (:attributes @model)
                       changed-attribute (merge-with
                                           concat
                                           (attributes name-of-last)
                                           {:add-role-pk [{:association-name association-name
                                                           :role-name role-name
                                                           :instance-handle association-instance-handle}]})]
                   (swap! model assoc :attributes (assoc attributes name-of-last changed-attribute)))
      :text (swap! model #(merge-with concat % {:add-role-from-text-pk [{:association-name association-name
                                                                         :role-name role-name
                                                                         :instance-handle association-instance-handle}]})))
    (doseq [path-token (reverse path)]
      (reset! model (assoc (last @stack) :children (assoc (:children (last @stack)) path-token @model)))
      (swap! stack drop-last))
    @model))


(defn create-add-association
  [input-model path association-name mappings]
  (let [instance-handle (atom nil)
        stack (atom [])
        model (atom input-model)
        path (eval-path path input-model)]
    (doseq [path-token path]
      (swap! stack conj (assoc @model :children (dissoc (:children @model) path-token)))
      (swap! model #((:children %) path-token)))
    (swap! model #(merge-with concat % {:add-association [{:association-name association-name
                                                           :instance-handle instance-handle}]}))
    (doseq [mapping (find-all-items-by-type mappings :mapping)]
      (swap! model create-role-mapping (:path mapping) association-name (:name mapping) instance-handle))
    (doseq [mapping-pk (find-all-items-by-type mappings :mapping-pk)]
      (swap! model create-role-mapping-pk (:path mapping-pk) association-name (:name mapping-pk) instance-handle))
    (doseq [path-token (reverse path)]
      (reset! model (assoc (last @stack) :children (assoc (:children (last @stack)) path-token @model)))
      (swap! stack drop-last))
    @model))


(defn create-model
  [configuration-list input-model]
  (let [input-model-root (:root input-model)
        foreach-tokens (find-all-items-by-type configuration-list :foreach)
        added-instances (reduce
                          #(apply create-add-instance (cons %1 %2))
                          input-model-root
                          (for [foreach-token foreach-tokens
                                add-instacne-token (:body foreach-token)
                                :when (= :add-instance (:type add-instacne-token))]
                            [(:path foreach-token) (:name add-instacne-token) (:mappings add-instacne-token)]))
        added-associations (reduce
                             #(apply create-add-association (cons %1 %2))
                             added-instances
                             (for [foreach-token foreach-tokens
                                   add-association-token (:body foreach-token)
                                   :when (= :add-association (:type add-association-token))]
                               [(:path foreach-token) (:name add-association-token) (:mappings add-association-token)]))]
    (assoc input-model :root added-associations)))
