(ns clj_hypergraph_db.xml_parser.uml_to_xml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.xml_parser.xml_common_functions :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]))


(defn flatten-token
  [subvector token]
  (if (contains? #{:foreach :associated-with} (:type token))
    (let [extended-vector (conj subvector (dissoc token :body))]
      (apply concat (map
                      #(flatten-token extended-vector %)
                      (:body token))))
    (conj subvector token)))


(defn flatten-configuration-list
  [configuration-list]
  (map #(flatten-token [] %) configuration-list))


(defn satisfied-by-inserted
  [token inserted-list]
  (let [associated-with-s (find-all-items-by-type token :associated-with)
        a-w-count (count associated-with-s)
        inserted-set (apply hash-set inserted-list)]
    (= a-w-count (count (filter
                          #(contains? inserted-set %)
                          (map #(apply concat (map :path (drop-last % associated-with-s))) (range a-w-count)))))))


(defn sort-configuration-list
  [configuration-list]
  (let [new-configuration-vector (atom [])
        configuration-list (atom configuration-list)]
    (while (not (zero? (count @configuration-list)))
      (doseq [index (range (dec (count @configuration-list)) -1 -1)]
        (let [token (nth @configuration-list index)]
          (if (satisfied-by-inserted token (map first @new-configuration-vector))
            (let [full-path (vec (concat
                                   (apply concat (map :path (find-all-items-by-type token :associated-with)))
                                   (:path (find-first-item-by-type token :add-token))))]
              (swap! new-configuration-vector conj [full-path token])
              (swap! configuration-list #(concat (drop-last (- (count %) index) %) (drop (inc index) %))))))))
    (map second @new-configuration-vector)))


(defn create-add-mapping
  [relative-input-model path class-instance-iterator attribute-name]
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
                                           {:add-attribute-mapping [{:class-instance-iterator class-instance-iterator
                                                                     :attribute-name attribute-name}]})]
                   (swap! model assoc :attributes (assoc attributes name-of-last changed-attribute)))
      :text (swap! model #(merge-with concat % {:add-text-mapping [{:class-instance-iterator class-instance-iterator
                                                                    :attribute-name attribute-name}]})))
    (doseq [path-token (reverse path)]
      (reset! model (assoc (last @stack) :children (assoc (:children (last @stack)) path-token @model)))
      (swap! stack drop-last))
    @model))


(defn create-add-token
  [input-model token]
  (let [model (atom input-model)
        stack (atom [])
        keys-stack (atom '())
        new-associated-with-list (atom '())
        foreach (find-first-item-by-type token :foreach)
        add-token (find-first-item-by-type token :add-token)
        associated-with-list (find-all-items-by-type token :associated-with)
        class-name (:name foreach)
        mappings (:mappings add-token)
        class-instance-iterator (atom nil)]
    (doseq [associated-with associated-with-list]
      (let [path (eval-path (:path associated-with) @model)
            path-role (:path-role associated-with)
            association-name (:association-name associated-with)
            target-role (:target-role associated-with)
            path-corresponding-class-name (get-target-class-of-role association-name path-role)]
        (swap! keys-stack concat path)
        (doseq [path-token path]
          (swap! stack conj (assoc @model :children (dissoc (:children @model) path-token)))
          (swap! model #((:children %) path-token)))
        (swap! new-associated-with-list conj {:target-role target-role
                                              :association-name association-name
                                              :path-role path-role
                                              :path-instance (let [add-token-list (:add-token @model)]
                                                               (first
                                                                 (apply
                                                                   concat
                                                                   (map
                                                                     (fn [class-name]
                                                                       (filter #(= class-name (:class-name %)) add-token-list))
                                                                     (get-class-and-all-subclasses-list path-corresponding-class-name)))))})))
    (let [path (eval-path (:path add-token) @model)]
      (swap! keys-stack concat path)
      (doseq [path-token path]
        (swap! stack conj (assoc @model :children (dissoc (:children @model) path-token)))
        (swap! model #((:children %) path-token))))
    (reset! class-instance-iterator (iterator-create :class class-name @new-associated-with-list))
    (swap! model #(merge-with concat % {:add-token [{:class-name class-name
                                                     :iterator class-instance-iterator}]}))
    (doseq [mapping (find-all-items-by-type mappings :mapping)]
      (swap! model create-add-mapping (:path mapping) class-instance-iterator (:name mapping)))
    (doseq [path-token (reverse @keys-stack)]
      (reset! model (assoc (last @stack) :children (assoc (:children (last @stack)) path-token @model)))
      (swap! stack drop-last))
    @model))


(defn finalize-model
  [model]
  (let [children (:children model)
        new-children (reduce
                       #(assoc %1 (first %2) (first (second %2)))
                       {}
                       (filter (comp second second) (map #(vector % (finalize-model (get children %))) (keys children))))
        token-usable-itself (not (zero? (count (filter (comp identity model) [:add-token
                                                                              :add-attribute-mapping
                                                                              :add-text-mapping]))))
        token-has-usable-children (not (zero? (count new-children)))]
    (vector (assoc model :children new-children) (or token-usable-itself token-has-usable-children))))


(defn create-model
  [configuration-list input-model]
  (let [tokens-list (sort-configuration-list (flatten-configuration-list configuration-list))
        input-model-root (:root input-model)
        added-tokens (reduce create-add-token input-model-root tokens-list)
        [final-root final-root-not-empty] (finalize-model added-tokens)
        final-model (assoc input-model :root (if final-root-not-empty final-root {}))]
    final-model))
