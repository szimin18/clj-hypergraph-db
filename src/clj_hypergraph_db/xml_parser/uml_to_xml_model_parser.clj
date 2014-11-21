(ns clj_hypergraph_db.xml_parser.uml_to_xml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_functions :refer :all]
            [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.xml_parser.xml_common_functions :refer :all]
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


(defn satisfied-by-inserted
  [[_ & rest-of-tokens] already-inserted-set]
  (let [associated-with-list-paths (map :path (drop-last rest-of-tokens))]
    (every? already-inserted-set (for [i (range (count associated-with-list-paths))]
                                   (apply concat (drop-last i associated-with-list-paths))))))


(defn sort-config
  [configuration-list]
  (let [new-configuration-vector (atom [])
        configuration-vector (atom (vec configuration-list))]
    (while (not-empty @configuration-vector)
      (doseq [index (range (dec (count @configuration-vector)) -1 -1)]
        (let [token (nth @configuration-vector index)]
          (when (->> @new-configuration-vector (map first) set (satisfied-by-inserted token))
            (let [full-path (apply concat (map :path (rest token)))]
              (swap! new-configuration-vector conj [full-path token])
              (swap! configuration-vector #(vec (concat (subvec % 0 index) (subvec % (inc index))))))))))
    (map second @new-configuration-vector)))


(defn create-add-mapping
  [model class-instance-iterator relative-path path attribute-name]
  (let [last-of-path (last path)
        evaluated-path (->> path drop-last (concat relative-path) (eval-path model))
        attribute-string-name (some #(if (-> % val :name (= last-of-path)) (key %)) (get-in model (conj evaluated-path :attributes)))]
    (update-in model evaluated-path merge-concat (if attribute-string-name
                                                   {:add-attribute-mapping [{:class-instance-iterator class-instance-iterator
                                                                             :attribute-name attribute-name
                                                                             :attribute-string-name attribute-string-name}]}
                                                   {:add-text-mapping [{:class-instance-iterator class-instance-iterator
                                                                        :attribute-name attribute-name}]}))))


(defn create-associated-with
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


(defn create-add-token
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


(defn finalize-model
  [model]
  (let [new-children (->> model :children (map #(update-in % [1] finalize-model)) (filter second) (into {}))]
    (if (or (not-empty new-children) (some model [:add-token :add-attribute-mapping :add-text-mapping]))
      (assoc model :children new-children))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


(defn separate-tokens
  [{token-type :type
    token-body :body
    :as model-token}
   previous-type]
  (println [previous-type token-type])
  (case [previous-type token-type]
    [:none :root] (do
                    (println "a" (count (for [for-each-token token-body
                                              :when (= :for-each (:type for-each-token))
                                              :let [;x (println for-each-token)
                                                    x (separate-tokens for-each-token :root)
                                                    y (println "y" (count x))]
                                              separated-token x #_(separate-tokens for-each-token :root)]
                                          separated-token)))
                    (for [for-each-token token-body
                        :when (= :for-each (:type for-each-token))
                        separated-token (separate-tokens for-each-token :root)]
                    separated-token))
    [:root :for-each] (do (println "b" (count (for [associated-with-token token-body
                                                    :when (= :associated-with (:type associated-with-token))
                                                    separated-token (separate-tokens associated-with-token :for-each)]
                                                (assoc model-token :body (cons separated-token (remove #{associated-with-token} token-body))))))
                        (for [associated-with-token token-body
                            :when (= :associated-with (:type associated-with-token))
                            separated-token (separate-tokens associated-with-token :for-each)]
                        (assoc model-token :body (cons separated-token (remove #{associated-with-token} token-body)))))
    [model-token]))


(def variable-uses-map
  {:mappng [:from]
   :mapping-each [:from]
   :bind [:from]
   :aggregate [:arg]
   :call [:args]})


(def variable-changes-map
  {:bind [:to]})


(defn get-token-uses-or-changes
  [config-token uses-or-changes]
  (let [uses-or-changes (get config-token uses-or-changes)]
    (filter keyword? (if (coll? uses-or-changes)
                       uses-or-changes
                       [uses-or-changes]))))


(defn attach-variable-usages
  [{token-type :type
    token-body :body
    token-args :args
    token-from :from
    :as config-token}]
  (if token-type
    (let [new-token-body (if token-body
                           (vec (map attach-variable-usages token-body)))
          new-token-args (if token-args
                           (vec (map attach-variable-usages token-args)))
          new-token-from (if (and (= :bind token-type) (coll? token-from))
                           (attach-variable-usages token-from))
          token-uses (for [uses-name (variable-uses-map token-type)
                           uses (get-token-uses-or-changes config-token uses-name)]
                       uses)
          token-changes (for [changes-name (variable-changes-map token-type)
                              changes (get-token-uses-or-changes config-token changes-name)]
                          changes)
          new-token (assoc config-token
                           :uses (set (concat
                                        (for [body-token new-token-body
                                              :let [uses-set (:uses body-token)]
                                              uses uses-set]
                                          uses)
                                        (for [args-token new-token-args
                                              :let [uses-set (:uses args-token)]
                                              uses uses-set]
                                          uses)
                                        (if new-token-from
                                          (:uses new-token-from)
                                          [])
                                        token-uses))
                           :changes (set (concat
                                           (for [body-token new-token-body
                                                 :let [changes-set (:changes body-token)]
                                                 changes changes-set]
                                             changes)
                                           (for [args-token new-token-args
                                                 :let [changes-set (:changes args-token)]
                                                 changes changes-set]
                                             changes)
                                           token-changes)))
          new-token (if new-token-body
                      (assoc new-token :body new-token-body)
                      new-token)
          new-token (if new-token-args
                      (assoc new-token :args new-token-args)
                      new-token)
          new-token (if new-token-from
                      (assoc new-token :from new-token-from)
                      new-token)]
      new-token)
    config-token))


(defn create-model
  [config-list input-model]
  (let [functions (reduce
                    (fn [fn-map {name :name body :body}] (assoc fn-map name body))
                    {}
                    (find-all-items-by-type config-list :function))
        extent-model (attach-variable-usages
                       {:body (remove #(#{:function} (:type %)) config-list)
                        :type :root})
        ;extent-model (separate-tokens extent-model :none)
        ]
    (assoc input-model
           :functions functions
           :extent-model extent-model))
  #_(update-in input-model [:root] #(or (->> config-list flatten-config sort-config (reduce create-add-token %) finalize-model) {})))
