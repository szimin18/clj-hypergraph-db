(ns clj_hypergraph_db.xml_parser.uml_to_xml_persistance_manager
  (:require [clj_hypergraph_db.xml_parser.xml_common_functions :refer :all]
            [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all])
  (:import [java.io File PrintWriter]))


(defn dispatch-variable
  [variable variables-map functions-map handle]
  (if (map? variable)
    (case (:type variable)
      :call (let [{fn-name :fn-name
                   args :args} variable]
              (apply (functions-map fn-name) (map #(dispatch-variable % variables-map functions-map handle) args)))
      :aggregate (get-instance-attributes handle (:arg variable)))
    (if (coll? variable)
      (map #(dispatch-variable % variables-map functions-map handle) variable)
      (if (keyword? variable)
        (if-let [variable-atom (variables-map variable)]
          @variable-atom
          (first (get-instance-attributes handle variable)))
        variable))))


(defn get-values
  [{iterator :class-instance-iterator
    mapping-from :mapping-from
    variables-map :variables-map}
   functions-map]
  (if-let [handle (iterator-get iterator)]
    (if (and (keyword? mapping-from) (nil? (variables-map mapping-from)))
      (get-instance-attributes handle mapping-from)
      (let [return (dispatch-variable mapping-from variables-map functions-map handle)]
        (if return
          (if (coll? return)
            return
            [return]))))))


(defn get-value
  [{iterator :class-instance-iterator
    mapping-from :mapping-from
    variables-map :variables-map}
   functions-map]
  (if-let [handle (iterator-get iterator)]
    (if (and (keyword? mapping-from) (nil? (variables-map mapping-from)))
      (first (get-instance-attributes handle mapping-from))
      (let [return (dispatch-variable mapping-from variables-map functions-map handle)]
        (or return [])))))


(defn eval-body
  [{body-token-type :type
    :as body-token}
   iterator-list
   variables-map
   functions-map]
  (case body-token-type
    :for-each (let [{class-name :name
                     for-each-body :body} body-token
                    for-each-body-tokens (remove #(= :associated-with-for (:type %)) for-each-body)
                    new-iterator (if (not-empty for-each-body-tokens)
                                   (iterator-create-class class-name))]
                #_(println "for-each" class-name)
                (doseq [{with :with
                         with-role-name :with-role
                         association-name :association-name
                         target-role-name :target-role
                         body :body
                         subtoken-type :type} for-each-body
                        :when (= :associated-with-for subtoken-type)
                        :when (not-empty body)
                        :let [new-iterator (iterator-create-class
                                             class-name
                                             (associated-with-create
                                               target-role-name
                                               association-name
                                               with-role-name
                                               (-> (filter
                                                     #{:..}
                                                     with)
                                                   count
                                                   dec
                                                   (drop-last iterator-list)
                                                   last)))]]
                  #_(println "assoc-with-for")
                  (doseq [_ (iterator-lazy-seq new-iterator)]
                    #_(println "doseq for iterator" new-iterator)
                    (doseq [subtoken body]
                      #_(println "eval" (:type subtoken))
                      (eval-body subtoken (conj iterator-list new-iterator) variables-map functions-map))))
                (if new-iterator
                  (doseq [_ (iterator-lazy-seq new-iterator)]
                    (doseq [subtoken for-each-body-tokens]
                      #_(println "eval" (:type subtoken))
                      (eval-body subtoken (conj iterator-list new-iterator) variables-map functions-map)))))
    :bind (reset! (variables-map (:to body-token)) (get-value {:class-instance-iterator (last iterator-list)
                                                               :mapping-from (:from body-token)
                                                               :variables-map variables-map}
                                                              functions-map))
    :mapping-each nil))


(defn tabs
  [tab-count]
  (apply str (repeat tab-count \tab)))


(declare write-subtoken)


(defn write-all-subtokens
  [children print-writer tab-count token-starter functions-map]
  (some true? (doall (map #(write-subtoken % print-writer (inc tab-count) token-starter functions-map) children))))


(defn write-subtoken
  [[token-string-name
    {add-token-list :add-token
     add-attribute-mapping-list :add-attribute-mapping
     add-text-mapping-list :add-text-mapping
     children :children}]
   print-writer
   tab-count
   token-starter
   functions-map]
  (let [token-name-occured (atom false)]
    ;print add-tokens
    (doseq [{class-name :class-name
             add-token-iterator :iterator
             add-token-body :body
             variables-map :variables-map} add-token-list
            :let [current-handle (atom (iterator-next add-token-iterator))]]
      (when @current-handle
        (reset! token-name-occured (token-starter print-writer)))
      ;print token
      (while @current-handle
        ;eval body
        (doseq [body-token add-token-body]
          (eval-body body-token [add-token-iterator] variables-map functions-map))
        ;start token
        (.print print-writer (str (tabs tab-count) "<" token-string-name))
        ;print attributes
        (doseq [add-attribute-mapping add-attribute-mapping-list
                :let [attribute-string-name (:attribute-string-name add-attribute-mapping)]]
          (doseq [attribute-value (get-values add-attribute-mapping  functions-map)]
            (.print print-writer (str " " attribute-string-name "=\"" attribute-value "\""))))
        ;end token
        (.print print-writer ">")
        (if-let [attribute-value (some #(first (get-values %  functions-map)) add-text-mapping-list)]
          (.print print-writer attribute-value)
          (do
            (.println print-writer)
            (write-all-subtokens children print-writer tab-count token-starter functions-map)
            (.print print-writer (tabs tab-count))))
        ;print ending token
        (.println print-writer (str "</" token-string-name ">"))
        ;reset iterator
        (reset! current-handle (iterator-next add-token-iterator)))
      (iterator-reset add-token-iterator))
    ;print text values
    (when-not @token-name-occured
      (doseq [add-text-mapping add-text-mapping-list
              :let [attribute-values (get-values add-text-mapping functions-map)]]
        (when (not-empty attribute-values)
          (reset! token-name-occured (token-starter print-writer)))
        (doseq [attribute-value attribute-values]
          (.println print-writer (str (tabs tab-count) "<" token-string-name ">" attribute-value "</" token-string-name ">")))))
    ;write subtokens of container
    (or
      @token-name-occured
      (let [token-started (atom false)
            token-starter (fn [print-writer]
                            (do
                              (token-starter print-writer)
                              (when-not @token-started
                                (reset! token-started true)
                                (.println print-writer (str (tabs tab-count) "<" token-string-name ">")))
                              true))]
        (when (write-all-subtokens children print-writer tab-count token-starter functions-map)
          (.println print-writer (str (tabs tab-count) "</" token-string-name ">"))
          true)))))


(defn write-output-data
  [{{children :children} :extent-model
    functions-map :functions}
   access-vector]
  (let [[file-path] access-vector]
    (try
      (.remove (File. file-path))
      (catch Exception e))
    (with-open [print-writer (PrintWriter. file-path)]
      (write-all-subtokens children print-writer -1 identity functions-map)
      (.flush print-writer))))
