(ns clj_hypergraph_db.xml_parser.xml_to_uml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_functions :refer :all]
            [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]
            [clj_hypergraph_db.xml_parser.xml_common_functions :refer :all]))


(defn create-attribute-mapping
  [model previous-path path class-name attribute-name class-instance-map]
  (let [last-of-path (last path)
        evaluated-path (->> path drop-last (concat previous-path) (eval-path model))]
    (if-let [new-attribute-name (some #(if (-> % second :name (= last-of-path)) (first %)) (get-in model (conj evaluated-path :attributes)))]
      (update-in model (conj evaluated-path :attributes new-attribute-name) merge-concat
                 {:add-attribute [{:class-name class-name
                                   :attribute-name attribute-name
                                   :instance-map class-instance-map}]})
      (update-in model evaluated-path merge-concat
                 {:add-attribute-from-text [{:class-name class-name
                                             :attribute-name attribute-name
                                             :instance-map class-instance-map}]}))))


(defn create-add-instance
  [model path class-name mappings]
  (let [class-instance-map (atom {})]
    (reduce
      #(apply create-attribute-mapping %1 %2)
      (update-in model (eval-path model path) merge-concat
                 {:add-instance [{:class-name class-name
                                  :instance-map class-instance-map
                                  :related-roles (atom (list))}]})
      (for [{name :name
             mapping-path :path} (find-all-items-by-type mappings :mapping)]
        [path mapping-path class-name name class-instance-map]))))


(defn create-role-mapping
  [model previous-path path association-name role-name association-instance-number]
  (let [path-only-backward (every? #{:..} path)
        evaluated-path (->> path (concat previous-path) (eval-path model))
        add-instance-list (get-in model (conj evaluated-path :add-instance))
        target-instance-number (:instance-number
                                (some (fn [class-name] (some #(if (= class-name (:class-name %)) %) add-instance-list))
                                      (get-class-and-all-subclasses-list (get-target-class-of-role association-name role-name))))]
    (update-in model (if path-only-backward (eval-path model previous-path) evaluated-path) merge-concat
               {:add-role [{:association-name association-name
                            :role-name role-name
                            :instance-number association-instance-number
                            :target-instance-number target-instance-number}]})))


(defn create-role-mapping-pk
  [model previous-path path association-name role-name association-instance-number]
  (let [last-of-path (last path)
        evaluated-path (->> path drop-last (concat previous-path) (eval-path model))]
    (if-let [new-attribute-name (some #(if (-> % second :name (= last-of-path)) (first %)) (get-in model (conj evaluated-path :attributes)))]
      (update-in model (conj evaluated-path :attributes new-attribute-name) merge-concat
                 {:add-role-pk [{:association-name association-name
                                 :role-name role-name
                                 :instance-number association-instance-number}]})
      (update-in model evaluated-path merge-concat
                 {:add-role-from-text-pk [{:association-name association-name
                                           :role-name role-name
                                           :instance-number association-instance-number}]}))))


(defn create-add-association
  [model path association-name mappings]
  (let [instance-number (atom nil)]
    (reduce
      #(apply (first %2) %1 (rest %2))
      (update-in model (eval-path model path) merge-concat
                 {:add-association [{:association-name association-name
                                     :instance-number instance-number}]})
      (concat
        (for [{name :name
               mapping-path :path} (find-all-items-by-type mappings :mapping)]
          [create-role-mapping path mapping-path association-name name instance-number])
        (for [{name :name
               mapping-path :path} (find-all-items-by-type mappings :mapping-pk)]
          [create-role-mapping-pk path mapping-path association-name name instance-number])))))


(defn create-model
  [configuration-list input-model]
  (let [foreach-tokens (find-all-items-by-type configuration-list :foreach)]
    (assoc
      input-model
      :root
      (reduce
        #(apply (first %2) %1 (rest %2))
        (:root input-model)
        (concat
          (for [{body :body
                 path :path} foreach-tokens
                {type :type
                 name :name
                 mappings :mappings} body
                :when (= type :add-instance)]
            [create-add-instance path name mappings])
          #_(for [{body :body
                 path :path} foreach-tokens
                {type :type
                 name :name
                 mappings :mappings} body
                :when (= type :add-association)]
            [create-add-association path name mappings]))))))
