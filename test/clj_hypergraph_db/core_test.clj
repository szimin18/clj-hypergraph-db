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
  [class-name]
  (let [iterator (iterator-create :class class-name)
        current-instance (atom (iterator-next iterator))
        instances (atom [])]
    (while @current-instance
      (swap! instances conj
             (apply merge-with #(conj %1 (first %2))
                    (for [attribute-link-handle (-> @current-instance hg-get hg-link-first-target hg-incident hg-find-all)
                          :let [attribute-link (hg-get attribute-link-handle)]]
                      {(hg-link-value attribute-link) #{(hg-get (hg-link-first-target attribute-link))}})))
      (reset! current-instance (iterator-next iterator)))
    @instances))


(defn persist-hg-data
  [hg-data-atom]
  (let [classes (apply merge (for [class-name (keys (:classes @model))
                                   :let [attribute-names (set (apply concat (map #((@model :classes) %)
                                                                                 (get-class-and-all-superclasses-list class-name))))
                                         class-instances (get-class-instances class-name)]]
                               {class-name (vec (for [class-instance class-instances]
                                                  (reduce-kv
                                                    #(if (contains? attribute-names %2) (assoc %1 %2 %3) %1)
                                                    {}
                                                    class-instance)))}))]
    (reset! hg-data-atom {:classes classes})))


(defn compare-class-data
  [class-instances-1 class-instances-2 class-name pk-set]
  (println "Comparing instances of class" class-name)
  (println "First HG contains" (count class-instances-1) "instance(s)")
  (println "Second HG contains" (count class-instances-2) "instance(s)")
  (let [uncommon-1 (filter #(= -1 (.indexOf class-instances-2 %)) class-instances-1)
        uncommon-2 (filter #(= -1 (.indexOf class-instances-1 %)) class-instances-2)]
    (when (not-empty uncommon-1)
      (println "No match for" class-name "instances from first HG:")
      (doseq [instance uncommon-1]
        (println instance)))
    (when (not-empty uncommon-2)
      (println "No match for" class-name "instances from second HG:")
      (doseq [instance uncommon-2]
        (println instance)))
    (when (and (empty? uncommon-1) (empty? uncommon-2))
      (println "All instances have their matches."))
    (println)))


(defn compare-hg-data
  [{classes-1 :classes} {classes-2 :classes}]
  (doseq [[class-name {pk-set :pk-set}] (:classes @model)]
    (compare-class-data (get classes-1 class-name) (get classes-2 class-name) class-name pk-set)))


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
