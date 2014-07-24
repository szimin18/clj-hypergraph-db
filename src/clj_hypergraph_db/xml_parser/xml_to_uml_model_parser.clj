(ns clj_hypergraph_db.xml_parser.xml_to_uml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]))


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


(defn eval-attribute-path
  ([path input-model]
   (eval-attribute-path path [] input-model))
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
       (eval-attribute-path
         (rest path)
         (conj new-path new-child-key)
         (children-of-input-model new-child-key))))))


(defn create-mapping
  [relative-input-model path class-name attribute-name class-instance-handle]
  (let [stack (atom [])
        model (atom relative-input-model)
        [path name-of-last type-of-last] (eval-attribute-path (if (map? path) (:path path) path) relative-input-model)]
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
        path (eval-path (if (map? path) (:path path) path) input-model)]
    (doseq [path-token path]
      (swap! stack conj (assoc @model :children (dissoc (:children @model) path-token)))
      (swap! model #((:children %) path-token)))
    (swap! model #(merge-with concat % {:add-instance [{:class-name class-name
                                                        :instance-handle instance-handle}]}))
    (reset! model (reduce
                    #(create-mapping %1 (:path %2) class-name (:name %2) instance-handle)
                    @model
                    mappings))
    (doseq [path-token (reverse path)]
      (reset! model (assoc (last @stack) :children (assoc (:children (last @stack)) path-token @model)))
      (swap! stack drop-last))
    @model))


(defn create-add-association
  [input-model path1 path2 association-name mappings]
  (let [instance-handle (atom nil)
        stack (atom [])
        model (atom input-model)
        outer-instance-atom (atom nil)
        inner-instance-atom (atom nil)
        inner-name (:alias path1)
        outer-name (:alias path2)
        path1 (eval-path (:path path1) input-model)
        path2 (eval-path (:path path2) input-model)
        path1-2 (drop (count path2) path1)]
    (doseq [path-token path2]
      (swap! stack conj (assoc @model :children (dissoc (:children @model) path-token)))
      (swap! model #((:children %) path-token)))
    (reset! outer-instance-atom (:instance-handle (first (:add-instance @model))))
    (doseq [path-token path1-2]
      (swap! stack conj (assoc @model :children (dissoc (:children @model) path-token)))
      (swap! model #((:children %) path-token)))
    (reset! inner-instance-atom (if-let [instance-handle (:instance-handle (first (:add-instance @model)))]
                                  instance-handle
                                  (if-let [instance-handle (:instance-handle (first (:add-attribute @model)))]
                                    instance-handle
                                    (:instance-handle (first (:add-attribute-from-text @model))))))
    (let [mappings-map {inner-name @inner-instance-atom
                        outer-name @outer-instance-atom}
          pk-mappings (map #(vector (mappings-map (:path %)) (:name %)) (find-all-items-by-type mappings :mapping-pk))
          normal-mappings (map #(vector (mappings-map (:path %)) (:name %)) (find-all-items-by-type mappings :mapping))]
      (swap! model #(merge-with concat % {:add-association [{:association-name association-name
                                                             :instance-handle instance-handle}]}))
      (doseq [normal-mapping normal-mappings]
        (swap! model #(merge-with concat % {:add-role [{:association-name association-name
                                                        :role-name (second normal-mapping)
                                                        :instance-handle instance-handle
                                                        :target-instance-handle (first normal-mapping)}]})))
      (doseq [pk-mapping pk-mappings]
        (swap! model #(merge-with concat % {:add-role-pk [{:association-name association-name
                                                           :role-name (second pk-mapping)
                                                           :instance-handle instance-handle
                                                           :target-instance-handle (first pk-mapping)}]}))))
    (doseq [path-token (reverse path1)]
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
                             (for [[path1 in-token] (for [foreach-token foreach-tokens
                                                          in-token (:body foreach-token)
                                                          :when (= :in (:type in-token))]
                                                      [(:path foreach-token) in-token])
                                   add-association-token (:body in-token)
                                   :when (= :add-association (:type add-association-token))]
                               [path1 (:path in-token) (:name add-association-token) (:mappings add-association-token)]))]
    (assoc input-model :root added-associations)))
