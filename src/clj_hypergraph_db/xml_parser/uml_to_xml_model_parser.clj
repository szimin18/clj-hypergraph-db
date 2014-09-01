(ns clj_hypergraph_db.xml_parser.uml_to_xml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.xml_parser.xml_common_functions :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]))


(defn flatten-token
  [previous-vector token]
  (if (contains? #{:foreach :associated-with} (:type token))
    (let [new-vector (conj previous-vector (dissoc token :body))]
      (apply concat (for [body-token (:body token)]
                      (flatten-token new-vector body-token))))
    (list (conj previous-vector token))))


(defn flatten-configuration-list
  [configuration-list]
  (apply concat (for [configuration-token configuration-list]
                  (flatten-token [] configuration-token))))


(defn satisfied-by-inserted
  [token inserted-list]
  (let [associated-with-list-paths (map :path (drop-last (rest token)))
        already-inserted (apply hash-set inserted-list)]
    (every? already-inserted (for [i (range (count associated-with-list-paths))]
                               (apply concat (drop-last i associated-with-list-paths))))))


(defn sort-configuration-list
  [configuration-list]
  (let [new-configuration-vector (atom [])
        configuration-vector (atom (vec configuration-list))]
    (while (not-empty @configuration-vector)
      (doseq [index (range (dec (count @configuration-vector)) -1 -1)]
        (let [token (nth @configuration-vector index)]
          (if (satisfied-by-inserted token (map first @new-configuration-vector))
            (let [full-path (apply concat (map :path (rest token)))]
              (swap! new-configuration-vector conj [full-path token])
              (swap! configuration-vector #(vec (concat (subvec % 0 index) (subvec % (inc index))))))))))
    (map second @new-configuration-vector)))


(defn create-add-mapping
  [model class-instance-iterator relative-path path attribute-name]
  (let [last-of-path (last path)
        evaluated-path (eval-path (concat relative-path (drop-last path)) model)
        new-attribute-name (first (for [[attribute-name attribute-token] (get-in model (conj evaluated-path :attributes))
                                        :when (= last-of-path (:name attribute-token))]
                                    attribute-name))
        model (if (nil? new-attribute-name)
                (update-in
                  model
                  evaluated-path
                  #(merge-with concat % {:add-text-mapping [{:class-instance-iterator class-instance-iterator
                                                             :attribute-name attribute-name}]}))
                (update-in
                  model
                  (conj evaluated-path :attributes new-attribute-name)
                  #(merge-with concat % {:add-attribute-mapping [{:class-instance-iterator class-instance-iterator
                                                                  :attribute-name attribute-name}]})))]
    model))


(defn create-associated-with
  [model associated-with-list]
  (let [model (get-in model (eval-path (apply concat (map :path associated-with-list)) model))
        last-associated-with (last associated-with-list)
        path-role (:path-role last-associated-with)
        association-name (:association-name last-associated-with)
        target-role (:target-role last-associated-with)
        path-class-name (get-target-class-of-role association-name path-role)]
    {:target-role target-role
     :association-name association-name
     :path-role path-role
     :path-instance (let [add-token-list (:add-token model)]
                      (first (apply concat (for [class-name (get-class-and-all-subclasses-list path-class-name)]
                                             (filter #(= class-name (:class-name %)) add-token-list)))))}))


(defn create-add-token
  [model token]
  (let [associated-with-list (drop-last (rest token))
        associated-with-list (for [count-to-drop (range (count associated-with-list))]
                               (create-associated-with model (drop-last count-to-drop associated-with-list)))
        class-name (:name (first token))
        class-instance-iterator (atom (iterator-create :class class-name associated-with-list))
        path (apply concat (map :path (rest token)))
        model (update-in
                model
                (eval-path path model)
                #(merge-with concat % {:add-token [{:class-name class-name
                                                    :iterator class-instance-iterator}]}))
        model (reduce
                #(apply create-add-mapping (cons %1 (cons class-instance-iterator (cons path %2))))
                model
                (for [mapping (find-all-items-by-type (:mappings (last token)) :mapping)]
                  [(:path mapping) (:name mapping)]))]
    model))


(defn finalize-model
  [model]
  (let [children (:children model)
        new-children (reduce
                       #(assoc %1 (first %2) (second %2))
                       {}
                       (filter second (map #(vector (first %) (finalize-model (second %))) children)))
        token-usable-itself (some model [:add-token :add-attribute-mapping :add-text-mapping])
        token-has-usable-children (not-empty new-children)]
    (if (or token-usable-itself token-has-usable-children)
      (dissoc (assoc model :children new-children) :text :name)
      nil)))


(defn create-model
  [configuration-list input-model]
  (let [tokens-list (sort-configuration-list (flatten-configuration-list configuration-list))
        model (:root input-model)
        model (reduce create-add-token model tokens-list)
        final-root (finalize-model model)
        final-model (assoc input-model :root (if final-root final-root {}))]
    final-model))
