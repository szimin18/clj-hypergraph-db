(ns clj_hypergraph_db.xml_parser.xml_to_uml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]
            [clj_hypergraph_db.xml_parser.xml_common_functions :refer :all]))


(defn create-attribute-mapping
  [model previous-path path class-name attribute-name class-instance-handle]
  (let [last-of-path (last path)
        evaluated-path (->> path drop-last (concat previous-path) (eval-path model))
        new-attribute-name (some #(if (-> % second :name (= last-of-path)) (first %)) (get-in model (conj evaluated-path :attributes)))
        model (if new-attribute-name
                (update-in
                  model
                  (conj evaluated-path :attributes new-attribute-name)
                  #(merge-with concat % {:add-attribute [{:class-name class-name
                                                          :attribute-name attribute-name
                                                          :instance-handle class-instance-handle}]}))
                (update-in
                  model
                  evaluated-path
                  #(merge-with concat % {:add-attribute-from-text [{:class-name class-name
                                                                    :attribute-name attribute-name
                                                                    :instance-handle class-instance-handle}]})))]
    model))


(defn create-add-instance
  [model path class-name mappings]
  (let [instance-node-handle (atom nil)
        instance-handle (atom nil)
        evaluated-path (eval-path model path)
        model (update-in model evaluated-path #(merge-with concat % {:add-instance [{:class-name class-name
                                                                                     :instance-link-handle instance-handle
                                                                                     :instance-node-handle instance-node-handle}]}))
        model (reduce
                #(apply create-attribute-mapping (cons %1 %2))
                model
                (for [mapping (find-all-items-by-type mappings :mapping)]
                  [path (:path mapping) class-name (:name mapping) instance-node-handle]))]
    model))


(defn create-role-mapping
  [model previous-path path association-name role-name association-instance-handle]
  (let [path-only-backward (every? #{:..} path)
        evaluated-path (->> path (concat previous-path) (eval-path model))
        add-instance-list (get-in model (conj evaluated-path :add-instance))
        target-instance-handle (:instance-link-handle
                                (some (fn [class-name] (some #(if (= class-name (:class-name %)) %) add-instance-list))
                                      (get-class-and-all-subclasses-list (get-target-class-of-role association-name role-name))))
        model (update-in
                model
                (if path-only-backward (eval-path model previous-path) evaluated-path)
                #(merge-with concat % {:add-role [{:association-name association-name
                                                   :role-name role-name
                                                   :instance-handle association-instance-handle
                                                   :target-instance-handle target-instance-handle}]}))]
    model))


(defn create-role-mapping-pk
  [model previous-path path association-name role-name association-instance-handle]
  (let [last-of-path (last path)
        evaluated-path (eval-path model (concat previous-path (drop-last path)))
        new-attribute-name (some #(if (-> % second :name (= last-of-path)) (first %)) (get-in model (conj evaluated-path :attributes)))
        model (if (nil? new-attribute-name)
                (update-in
                  model
                  evaluated-path
                  #(merge-with concat % {:add-role-from-text-pk [{:association-name association-name
                                                                  :role-name role-name
                                                                  :instance-handle association-instance-handle}]}))
                (update-in
                  model
                  (conj evaluated-path :attributes new-attribute-name)
                  #(merge-with concat % {:add-role-pk [{:association-name association-name
                                                        :role-name role-name
                                                        :instance-handle association-instance-handle}]})))]
    model))


(defn create-add-association
  [model path association-name mappings]
  (let [instance-handle (atom nil)
        evaluated-path (eval-path model path)
        model (update-in model evaluated-path #(merge-with concat % {:add-association [{:association-name association-name
                                                                                        :instance-handle instance-handle}]}))
        model (reduce
                #(apply create-role-mapping (cons %1 %2))
                model
                (for [mapping (find-all-items-by-type mappings :mapping)]
                  [path (:path mapping) association-name (:name mapping) instance-handle]))
        model (reduce
                #(apply create-role-mapping-pk (cons %1 %2))
                model
                (for [mapping-pk (find-all-items-by-type mappings :mapping-pk)]
                  [path (:path mapping-pk) association-name (:name mapping-pk) instance-handle]))]
    model))


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
