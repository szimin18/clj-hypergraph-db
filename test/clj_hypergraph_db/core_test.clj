(ns clj_hypergraph_db.core_test
  (:require [clojure.test :refer :all]
            [clj_hypergraph_db.core :refer :all]
            [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.persistance.persistance_manager :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]))


(def test-run-files
  ["
    (hdm \"configuration/hdm-uml-model.clj\")

    ;
    ; inputs
    ;

    (input \"configuration/xml-input-model.clj\" :default
           (extent \"configuration/xml-input-extent.clj\"))

    ;(input \"configuration/ldap-input-model.clj\" :default
    ;       (extent \"configuration/ldap-input-extent.clj\"))

    ;(input \"configuration/sql-input-model.clj\" :default
    ;       (extent \"configuration/sql-input-extent.clj\"))

    ;
    ; outputs
    ;

    (output \"configuration/xml-input-model.clj\" [\"test_resources/test-output.xml\"]
            (extent \"configuration/xml-output-extent.clj\"))

    ;(output \"configuration/sql-input-model.clj\" [{:database-name \"glue_output\"
    ;                                                :user-name \"user\"
    ;                                                :password \"password\"}]
    ;        (extent \"configuration/sql-output-extent.clj\"))

    ;
    ; test-inputs
    ;

    (test-input \"configuration/xml-input-model.clj\" [\"test_resources/test-output.xml\"]
                (extent \"configuration/xml-input-extent.clj\"))
  "])


(defn get-class-instances
  [class-name raw-classes]
  (let [iterator (iterator-create :class class-name)
        current-instance (atom (iterator-next iterator))
        instances (atom [])]
    (while @current-instance
      (let [instance (apply merge-with #(conj %1 (first %2))
                            (for [attribute-link-handle (-> @current-instance hg-get hg-link-first-target hg-incident hg-find-all)
                                  :let [attribute-link (hg-get attribute-link-handle)]]
                              {(hg-link-value attribute-link) #{(hg-get (hg-link-first-target attribute-link))}}))]
        (swap! instances conj instance)
        (swap! raw-classes assoc @current-instance instance))
      (reset! current-instance (iterator-next iterator)))
    @instances))


(defn get-association-instances
  [association-name]
  (let [iterator (iterator-create :association association-name)
        current-instance (atom (iterator-next iterator))
        instances (atom [])]
    (while @current-instance
      (let [association-link (hg-get @current-instance)]
        (swap! instances conj (for [index (range 1 (hg-link-arity association-link))]
                                (hg-link-target-at association-link index))))
      (reset! current-instance (iterator-next iterator)))
    @instances))


(defn persist-hg-data
  [hg-data-atom]
  (let [raw-classes (atom {})
        classes (apply merge (for [class-name (keys (:classes @model))
                                   :let [attribute-names (set (apply concat (map #(keys (:attributes ((@model :classes) %)))
                                                                                 (get-class-and-all-superclasses-list class-name))))]]
                               {class-name (vec (for [class-instance (get-class-instances class-name raw-classes)]
                                                  (reduce-kv
                                                    #(if (contains? attribute-names %2) (assoc %1 %2 %3) %1)
                                                    {}
                                                    class-instance)))}))
        raw-classes @raw-classes
        associations (apply merge (for [association-name (keys (:associations @model))
                                        :let [association-instances (get-association-instances association-name)]]
                                    {association-name (vec (for [association-instance association-instances]
                                                             (vec (map raw-classes association-instance))))}))]
    (reset! hg-data-atom {:classes classes
                          :associations associations})))


(defn get-distinct-uncommon-set
  [instances pk-list]
  (map
    #(vector (first %) (first (second %)))
    (filter
      #(= 1 (count (val %)))
      (reduce
        #(assoc %1 (second %2) (cons (first %2) (%1 %2 [])))
        {}
        (map-indexed
          (fn
            [index instance]
            [index
             (reduce
               #(assoc %1 %2 (first (instance %2)))
               {}
               pk-list)])
          instances)))))


(defn intersection
  [l1 l2]
  (let [s2 (set (map first l2))
        v2 (vec l2)]
    (reduce
      (fn [result item]
        (if (contains? s2 (first item))
          (conj result (conj item (.indexOf v2 item)))
          result))
      #{}
      l1)))


(defn compare-class-data
  [class-instances-1 class-instances-2 class-name pk-list]
  (println)
  (println "Comparing instances of class" class-name)
  (let [uncommon-1 (vec (filter #(= -1 (.indexOf class-instances-2 %)) class-instances-1))
        uncommon-2 (vec (filter #(= -1 (.indexOf class-instances-1 %)) class-instances-2))]
    (if (and (empty? uncommon-1) (empty? uncommon-2) (= (count class-instances-1) (count class-instances-2)))
      (println "All" (count class-instances-1) " instance(s) have their matches.")
      (let [distinct-pk-list-1 (get-distinct-uncommon-set uncommon-1 pk-list)
            distinct-pk-list-2 (get-distinct-uncommon-set uncommon-2 pk-list)
            pk-maps-intersection (intersection distinct-pk-list-1 distinct-pk-list-2)
            intersection-indexes-1 (set (map #(get % 1) pk-maps-intersection))
            ;x (println intersection-indexes-1)
            very-uncommon-1 (filter identity (map-indexed #(if-not (contains? intersection-indexes-1 %1) %2) uncommon-1))
            intersection-indexes-2 (set (map #(get % 2) pk-maps-intersection))
            ;x (println intersection-indexes-2)
            very-uncommon-2 (filter identity (map-indexed #(if-not (contains? intersection-indexes-2 %1) %2) uncommon-2))]
        (println "First HG contains" (count class-instances-1) "instance(s)")
        (println "Second HG contains" (count class-instances-2) "instance(s)")
        (doseq [[pk-map index-1 index-2] pk-maps-intersection
                :let [instance-1 (atom (reduce
                                         #(assoc %1 %2 (inc (%1 %2 0)))
                                         {}
                                         (for [[attribute-name attribute-values] (get uncommon-1 index-1)
                                               attribute-value attribute-values]
                                           [attribute-name attribute-value])))
                      instance-2 (atom (reduce
                                         #(assoc %1 %2 (inc (%1 %2 0)))
                                         {}
                                         (for [[attribute-name attribute-values] (get uncommon-2 index-2)
                                               attribute-value attribute-values]
                                           [attribute-name attribute-value])))]]
          (doseq [[attribute attribute-count] @instance-1
                  :let [count-to-remove (min attribute-count (@instance-2 attribute 0))]
                  :when (not (zero? count-to-remove))]
            (swap! instance-1 update-in [attribute] #(- % count-to-remove))
            (swap! instance-2 update-in [attribute] #(- % count-to-remove)))
          (println)
          (println "Potential match for instance with pk:" pk-map)
          (doseq [[[attribute-name attribute-value] attribute-count] @instance-1]
            (dotimes [_ attribute-count]
              (println "  No match for" attribute-name "attribute instance from first HG instance with value:" attribute-value)))
          (doseq [[[attribute-name attribute-value] attribute-count] @instance-2]
            (dotimes [_ attribute-count]
              (println "  No match for" attribute-name "attribute instance from second HG instance with value:" attribute-value))))
        (when (not-empty very-uncommon-1)
          (println)
          (println "No match for" class-name "instance(s) from first HG:")
          (doseq [instance very-uncommon-1]
            (println instance)))
        (when (not-empty very-uncommon-2)
          (println)
          (println "No match for" class-name "instance(s) from second HG:")
          (doseq [instance very-uncommon-2]
            (println instance))))))
  (println))


(defn compare-association-data
  [association-instances-1 association-instances-2 association-name])


(defn compare-hg-data
  [{classes-1 :classes
    associations-1 :associations}
   {classes-2 :classes
    associations-2 :associations}]
  (doseq [class-name (keys (:classes @model))]
    (compare-class-data (get classes-1 class-name) (get classes-2 class-name) class-name (get-pk-list class-name)))
  (doseq [[association-name assoc-config] (:associations @model)]
    (compare-association-data (get associations-1 association-name) (get associations-2 association-name) association-name)))


(defn- run-test
  [run-string]
  (let [hg-data-1 (atom nil)
        hg-data-2 (atom nil)]
    (hg-create "hgdbtest1")
    (let [run-config-file (read-string (str "(" run-string ")"))
          run-config (evaluate 'clj_hypergraph_db.run_config_parser run-config-file)
          hdm-config-file (-> run-config (find-first-item-by-type :hdm) :filename read-config-file)
          hdm-model-type (-> hdm-config-file first second)
          {hdm-config-namespace :config
           hdm-model-namespace :model
           hdm-manager-namespace :manager} (-> run-namespaces :hdm hdm-model-type)
          hdm-config (evaluate hdm-config-namespace hdm-config-file)
          hdm-model (apply-resolved-function "create-model" hdm-model-namespace hdm-config)]
      (apply-resolved-function "set-model" hdm-manager-namespace hdm-model)
      (doseq [input-token (find-all-items-by-type run-config :input)
              :let [input-config-file (-> input-token :filename read-config-file)
                    input-type (-> input-config-file first second)
                    {input-config-namespace :config
                     input-model-namespace :model} (-> run-namespaces :models input-type)
                    input-config (evaluate input-config-namespace input-config-file)
                    input-model (apply-resolved-function "create-model" input-model-namespace input-config)
                    input-access (:access input-token)
                    input-access (if (= :default input-access) (:default-access input-model) input-access)]]
        (doseq [extent-token (-> input-token :extents (find-all-items-by-type :extent))
                :let [extent-config-file (-> extent-token :filename read-config-file)
                      {extent-config-namespace :config
                       extent-model-namespace :model
                       extent-persistance-namespace :persistance} (-> run-namespaces :models input-type :extents hdm-model-type)
                      extent-config (evaluate extent-config-namespace extent-config-file)
                      extent-model (apply-resolved-function "create-model" extent-model-namespace extent-config input-model)]]
          (apply-resolved-function "load-input-data" extent-persistance-namespace extent-model input-access)))
      (persist-hg-data hg-data-1)
      (doseq [output-token (find-all-items-by-type run-config :output)
              :let [output-config-file (-> output-token :filename read-config-file)
                    output-type (-> output-config-file first second)
                    {output-config-namespace :config
                     output-model-namespace :model} (-> run-namespaces :models output-type)
                    output-config (evaluate output-config-namespace output-config-file)
                    output-model (apply-resolved-function "create-model" output-model-namespace output-config)
                    output-access (:access output-token)
                    output-access (if (= :default output-access) (:default-access output-model) output-access)]]
        (doseq [extent-token (-> output-token :extents (find-all-items-by-type :extent))
                :let [extent-config-file (-> extent-token :filename read-config-file)
                      {extent-config-namespace :config
                       extent-model-namespace :model
                       extent-persistance-namespace :persistance} (-> run-namespaces :hdm hdm-model-type :extents output-type)
                      extent-config (evaluate extent-config-namespace extent-config-file)
                      extent-model (apply-resolved-function "create-model" extent-model-namespace extent-config output-model)]]
          (apply-resolved-function "write-output-data" extent-persistance-namespace extent-model output-access))))
    (hg-close)
    (hg-create "hgdbtest2")
    (let [run-config-file (read-string (str "(" run-string ")"))
          run-config (evaluate 'clj_hypergraph_db.run_config_parser run-config-file)
          hdm-config-file (-> run-config (find-first-item-by-type :hdm) :filename read-config-file)
          hdm-model-type (-> hdm-config-file first second)
          {hdm-config-namespace :config
           hdm-model-namespace :model
           hdm-manager-namespace :manager} (-> run-namespaces :hdm hdm-model-type)
          hdm-config (evaluate hdm-config-namespace hdm-config-file)
          hdm-model (apply-resolved-function "create-model" hdm-model-namespace hdm-config)]
      (apply-resolved-function "set-model" hdm-manager-namespace hdm-model)
      (doseq [input-token (find-all-items-by-type run-config :test-input)
              :let [input-config-file (-> input-token :filename read-config-file)
                    input-type (-> input-config-file first second)
                    {input-config-namespace :config
                     input-model-namespace :model} (-> run-namespaces :models input-type)
                    input-config (evaluate input-config-namespace input-config-file)
                    input-model (apply-resolved-function "create-model" input-model-namespace input-config)
                    input-access (:access input-token)
                    input-access (if (= :default input-access) (:default-access input-model) input-access)]]
        (doseq [extent-token (-> input-token :extents (find-all-items-by-type :extent))
                :let [extent-config-file (-> extent-token :filename read-config-file)
                      {extent-config-namespace :config
                       extent-model-namespace :model
                       extent-persistance-namespace :persistance} (-> run-namespaces :models input-type :extents hdm-model-type)
                      extent-config (evaluate extent-config-namespace extent-config-file)
                      extent-model (apply-resolved-function "create-model" extent-model-namespace extent-config input-model)]]
          (apply-resolved-function "load-input-data" extent-persistance-namespace extent-model input-access)))
      (persist-hg-data hg-data-2))
    (hg-close)
    (compare-hg-data @hg-data-1 @hg-data-2)))


(deftest- start-run-test
          []
          (doseq [run-config test-run-files]
            (run-test run-config)))


(defn test-ns-hook
  []
  (start-run-test))
