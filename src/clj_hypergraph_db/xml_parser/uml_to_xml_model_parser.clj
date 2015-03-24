(ns clj_hypergraph_db.xml_parser.uml_to_xml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_functions :refer :all]
            [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.xml_parser.xml_common_functions :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager_neo4j :refer :all]))


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


(defn remove-some-body
  [token-body]
  (remove (fn [{type :type :as token}]
            (or
              (#{:function :associated-with :add-token} type)
              (and
                (= :for-each type)
                (->> (:body token)
                     (map :type)
                     (some #{:associated-with :add-token})))))
          token-body))


(defn separate-tokens
  [{token-type :type
    token-body :body
    :as model-token}
   & previous-token-types]
  (let [rest-of-body (remove-some-body token-body)]
    (case [(first previous-token-types) token-type]
      [nil :root] (for [child-token token-body
                          :when (= :for-each (:type child-token))
                          separated-token (separate-tokens child-token token-type)]
                      (cons separated-token rest-of-body))
      ([:root :for-each]
       [:foreach :associated-with]
       [:associated-with :associated-with]) (for [child-token token-body
                                                  :when (#{:associated-with :add-token} (:type child-token))
                                                  separated-token (separate-tokens child-token token-type)]
                                              (assoc model-token
                                                     :body (cons separated-token rest-of-body)))
      [model-token])))


(defn satisfied-by-inserted
  [[_ & rest-of-tokens] already-inserted-set]
  (let [associated-with-list-paths (map :path (drop-last rest-of-tokens))]
    (every? already-inserted-set (for [i (range (count associated-with-list-paths))]
                                   (apply concat (drop-last i associated-with-list-paths))))))


(defn attach-paths
  [[{token-type :type
     token-body :body
     :as model-token} :as full-body]]
  (case token-type
    :for-each (let [paths (attach-paths token-body)]
                [(map #(apply concat (drop-last % paths)) (range (count paths)))
                 full-body])
    :associated-with (cons (:with model-token) (attach-paths token-body))
    :add-token [(:path model-token)]))


(defn sort-model
  [tokens-list]
  (loop [old-list (map attach-paths tokens-list)
         new-list []
         already-inserted #{}]
    (let [{to-move true
           not-to-move false} (group-by #(every? already-inserted (rest (first %))) old-list)]
      (if to-move
        (recur not-to-move (concat new-list to-move) (into already-inserted (map ffirst to-move)))
        (map second new-list)))))


(defn remove-unused
  [model-token]
  model-token)


(defn create-add-mapping
  [mapping-type model class-instance-iterator relative-path mapping-from mapping-to variables-map]
  (let [last-of-path (last mapping-to)
        evaluated-path (concat relative-path (eval-path (get-in model relative-path) (drop-last mapping-to)))
        attribute-string-name (some #(if (-> % val :name (= last-of-path)) (key %)) (get-in model (conj evaluated-path :attributes)))]
    (update-in model evaluated-path merge-concat
               (if attribute-string-name
                 {:add-attribute-mapping
                 #_(keyword (str "add-attribute-" (name mapping-type))) [{:variables-map variables-map
                                                                         :class-instance-iterator class-instance-iterator
                                                                         :mapping-from mapping-from
                                                                         :attribute-string-name attribute-string-name}]}
                 {:add-text-mapping
                 #_(keyword (str "add-text-" (name mapping-type))) [{:variables-map variables-map
                                                                    :class-instance-iterator class-instance-iterator
                                                                    :mapping-from mapping-from}]}))))


(defn create-associated-with
  [model
   {association-name :association-name
    path-role :with-role
    target-role :target-role}]
  (let [add-token-list (:add-token model)
        path-instance-iterator (->> (get-target-class-of-role association-name path-role)
                                    get-class-and-all-subclasses-list
                                    (some (fn [class-name] (some #(if (= class-name (:class-name %)) %) add-token-list)))
                                    :iterator)]
    (associated-with-create target-role association-name path-role path-instance-iterator)))


(defn create-add-token
  [model
   [{class-name :name
     :as first-of-extent-token} & rest-of-extent-token]]
  (loop [{token-type :type
          [first-token-body & rest-token-body :as token-body] :body
          :as extent-token} first-of-extent-token
         associated-with-list []
         relative-model model
         relative-path []
         accumulated-rest-of-body rest-of-extent-token]
    (case token-type
      :for-each (recur first-token-body associated-with-list relative-model relative-path accumulated-rest-of-body)
      :associated-with (let [new-relative-model (get-by-path relative-model (:with extent-token))]
                         (recur first-token-body
                                (conj associated-with-list (create-associated-with new-relative-model extent-token))
                                new-relative-model
                                (concat relative-path (eval-path relative-model (:with extent-token)))
                                (concat accumulated-rest-of-body rest-token-body)))
      :add-token (let [iterator (apply iterator-create-class class-name associated-with-list)
                       accumulated-rest-of-body (concat accumulated-rest-of-body
                                                        (remove #(= :mapping (:type %)) token-body))
                       relative-path (concat relative-path (eval-path relative-model (:path extent-token)))
                       variables-map (reduce
                                       #(assoc %1 %2 (atom nil))
                                       {}
                                       (set (apply concat (map :changes accumulated-rest-of-body))))]
                   (reduce
                     (fn [model [mapping-type mapping-from mapping-to]]
                       (create-add-mapping mapping-type model iterator relative-path mapping-from mapping-to variables-map))
                     (update-in model relative-path merge-concat
                                {:add-token [{:class-name class-name
                                              :iterator iterator
                                              :body accumulated-rest-of-body
                                              :variables-map variables-map}]})
                     (concat
                       (for [{mapping-from :from
                              mapping-to :to} (find-all-items-by-type token-body :mapping)]
                         [:mapping mapping-from mapping-to])
                       (for [{mapping-from :from
                              mapping-to :to} (find-all-items-by-type token-body :mapping-each)]
                         [:mapping-each mapping-from mapping-to])))))))


(defn finalize-model
  [model]
  (let [new-children (->> model :children (map #(update-in % [1] finalize-model)) (filter second) (into {}))]
    (if (or (not-empty new-children) (some model [:add-token :add-attribute-mapping :add-text-mapping
                                                  :add-attribute-mapping-each :add-text-mapping-each]))
      (assoc model :children new-children))))


(defn create-model
  [config-list {input-model-root :root}]
  {:functions (reduce
                (fn [fn-map {name :name body :body}] (assoc fn-map name body))
                {}
                (find-all-items-by-type config-list :function))
   :extent-model (or
                   (->> config-list
                        (remove #(#{:function} (:type %)))
                        (assoc {:type :root} :body)
                        attach-variable-usages
                        separate-tokens
                        sort-model
                        (map remove-unused)
                        (reduce create-add-token input-model-root)
                        finalize-model)
                   {})})
