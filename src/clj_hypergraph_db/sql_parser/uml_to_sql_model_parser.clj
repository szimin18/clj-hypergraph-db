(ns clj_hypergraph_db.sql_parser.uml_to_sql_model_parser
  (:require [clj_hypergraph_db.common_parser.common_functions :refer :all]
            [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.sql_parser.sql_common_functions :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]))


(defn flatten-token
  [previous-vector token]
  (if (contains? #{:foreach :associated-with} (:type token))
    (let [new-vector (conj previous-vector (dissoc token :body))]
      (apply concat (for [body-token (:body token)]
                      (flatten-token new-vector body-token))))
    [(conj previous-vector token)]))


(defn flatten-config
  [configuration-list]
  (apply concat (for [configuration-token configuration-list]
                  (flatten-token [] configuration-token))))


#_(defn satisfied-by-inserted
  [[_ & rest-of-tokens] already-inserted-set]
  (let [associated-with-list-paths (map :path (drop-last rest-of-tokens))]
    (every? already-inserted-set (for [i (range (count associated-with-list-paths))]
                                   (apply concat (drop-last i associated-with-list-paths))))))


#_(defn sort-config
  [configuration-list]
  (let [new-configuration-vector (atom [])
        configuration-vector (atom (vec configuration-list))]
    (while (not-empty @configuration-vector)
      (doseq [index (range (dec (count @configuration-vector)) -1 -1)]
        (let [token (nth @configuration-vector index)]
          (when (->> @new-configuration-vector (map first) (into #{}) (satisfied-by-inserted token))
            (let [full-path (apply concat (map :path (rest token)))]
              (swap! new-configuration-vector conj [full-path token])
              (swap! configuration-vector #(vec (concat (subvec % 0 index) (subvec % (inc index))))))))))
    (map second @new-configuration-vector)))


#_(defn create-add-mapping
  [model class-instance-iterator relative-path path attribute-name]
  (let [last-of-path (last path)
        evaluated-path (->> path drop-last (concat relative-path) (eval-path model))
        new-attribute-name (some #(if (-> % val :name (= last-of-path)) (key %)) (get-in model (conj evaluated-path :attributes)))]
    (update-in model evaluated-path merge-concat (if new-attribute-name
                                                   {:add-attribute-mapping [{:class-instance-iterator class-instance-iterator
                                                                             :attribute-name attribute-name
                                                                             :attribute-string-name new-attribute-name}]}
                                                   {:add-text-mapping [{:class-instance-iterator class-instance-iterator
                                                                        :attribute-name attribute-name}]}))))


#_(defn create-associated-with
  [model associated-with-list]
  (let [{association-name :association-name
         path-role :path-role
         target-role :target-role} (last associated-with-list)
        add-token-list (->> associated-with-list (map :path) (apply concat) (eval-path model) (get-in model) :add-token)
        path-instance-iterator (->> (get-target-class-of-role association-name path-role)
                                    get-class-and-all-subclasses-list
                                    (some (fn [class-name] (some #(if (= class-name (:class-name %)) %) add-token-list)))
                                    :iterator)]
    (associated-with-create target-role association-name path-role path-instance-iterator)))


#_(defn create-add-token
  [model token]
  (let [associated-with-list (drop-last (rest token))
        class-name (:name (first token))
        class-instance-iterator (atom (iterator-create :class class-name
                                                       (for [count-to-drop (range (count associated-with-list))]
                                                         (create-associated-with model (drop-last count-to-drop associated-with-list)))))
        path (apply concat (map :path (rest token)))]
    (reduce
      (fn [model [mapping-path mapping-name]]
        (create-add-mapping model class-instance-iterator path mapping-path mapping-name))
      (update-in model (eval-path model path) merge-concat
                 {:add-token [{:class-name class-name
                               :iterator class-instance-iterator}]})
      (for [{mapping-path :path
             mapping-name :name} (find-all-items-by-type (:mappings (last token)) :mapping)]
        [mapping-path mapping-name]))))


#_(defn finalize-model
  [model]
  (let [new-children (->> model :children (map #(vector (key %) (finalize-model (val %)))) (filter second) (into {}))]
    (if (or (not-empty new-children) (some model [:add-token :add-attribute-mapping :add-text-mapping]))
      (assoc model :children new-children))))


(defn create-model
  [config-list output-model]
  {:extent-config config-list :output-model output-model})