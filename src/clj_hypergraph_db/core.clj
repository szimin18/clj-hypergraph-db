(ns clj_hypergraph_db.core
  (:gen-class :main true)
  (:require [clj_hypergraph_db.persistance.persistance_manager :refer :all] ;peek-database
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all] ;get-class-instances
            [clj_hypergraph_db.common_parser.common_functions :refer :all] ;prn-rec-file
            [clj_hypergraph_db.common_parser.common_model_parser :refer :all]))

(def run-namespaces
  {:hdm {:uml {:config 'clj_hypergraph_db.hdm_parser.hdm_uml_config_parser
               :model 'clj_hypergraph_db.hdm_parser.hdm_uml_model_parser
               :manager 'clj_hypergraph_db.hdm_parser.hdm_uml_model_manager
               :extents {:xml {:config 'clj_hypergraph_db.xml_parser.uml_to_xml_config_parser
                               :model 'clj_hypergraph_db.xml_parser.uml_to_xml_model_parser
                               :persistance 'clj_hypergraph_db.xml_parser.uml_to_xml_persistance_manager}
                         :sql {}}}}
   :models {:xml {:config 'clj_hypergraph_db.xml_parser.xml_config_parser
                  :model 'clj_hypergraph_db.xml_parser.xml_model_parser
                  :extents {:uml {:config 'clj_hypergraph_db.xml_parser.xml_to_uml_config_parser
                                  :model 'clj_hypergraph_db.xml_parser.xml_to_uml_model_parser
                                  :persistance 'clj_hypergraph_db.xml_parser.xml_to_uml_persistance_manager}}}
            :sql {:config 'clj_hypergraph_db.sql_parser.sql_config_parser
                  :model 'clj_hypergraph_db.sql_parser.sql_model_parser
                  :extents {:uml {:config 'clj_hypergraph_db.sql_parser.sql_to_uml_config_parser
                                  :model 'clj_hypergraph_db.sql_parser.sql_to_uml_model_parser
                                  :persistance 'clj_hypergraph_db.sql_parser.sql_to_uml_persistance_manager}}}}
   :prototypers {:xml 'clj_hypergraph_db.xml_parser.xml_model_prototyper
                 :sql 'clj_hypergraph_db.sql_parser.sql_model_prototyper}})


(defn evaluate
  [namespace to-evaluate]
  (do
    (require namespace)
    (let [namespace (find-ns namespace)]
      (map
        #(binding [*ns* namespace] (eval %))
        to-evaluate))))


(defn apply-resolved-function
  [function-string namespace & args]
  (do
    (require namespace)
    (-> namespace (str "/" function-string) read-string symbol resolve (apply args))))


(defn read-config-file
  [filename]
  (read-string (str "(" (slurp filename) ")")))


(defn run
  [run-filename]
  (do
    (create-database "hgdbtest")
    (let [run-config-file (read-config-file run-filename)
          run-config (evaluate 'clj_hypergraph_db.run_config_parser run-config-file)
          hdm-config-file (-> run-config (find-first-item-by-type :hdm) :filename read-config-file)
          hdm-model-type (-> hdm-config-file first second)
          hdm-namespaces (-> run-namespaces :hdm hdm-model-type)
          hdm-config-namespace (:config hdm-namespaces)
          hdm-model-namespace (:model hdm-namespaces)
          hdm-manager-namespace (:manager hdm-namespaces)
          hdm-config (evaluate hdm-config-namespace hdm-config-file)
          hdm-model (apply-resolved-function "create-model" hdm-model-namespace hdm-config)]
      (apply-resolved-function "set-model" hdm-manager-namespace hdm-model)
      (doseq [input-token (find-all-items-by-type run-config :input)]
        (let [input-config-file (-> input-token :filename read-config-file)
              input-type (-> input-config-file first second)
              input-namespaces (-> run-namespaces :models input-type)
              input-config-namespace (:config input-namespaces)
              input-model-namespace (:model input-namespaces)
              input-config (evaluate input-config-namespace input-config-file)
              input-model (apply-resolved-function "create-model" input-model-namespace input-config)
              input-access (:access input-token)
              input-access (if (= :default input-access) (:default-access input-model) input-access)]
          (doseq [extent-token (-> input-token :extents (find-all-items-by-type :extent))]
            (let [extent-config-file (-> extent-token :filename read-config-file)
                  extent-namespaces (-> run-namespaces :models input-type :extents hdm-model-type)
                  extent-config-namespace (:config extent-namespaces)
                  extent-model-namespace (:model extent-namespaces)
                  extent-persistance-namespace (:persistance extent-namespaces)
                  extent-config (evaluate extent-config-namespace extent-config-file)
                  extent-model (apply-resolved-function "create-model" extent-model-namespace extent-config input-model)]
              (apply-resolved-function "load-input-data" extent-persistance-namespace extent-model input-access)))))

      ;;;;; get all associations from database
      #_(doseq [[assocaition-name association] (:associations @model)]
        (println "######" assocaition-name)
        (let [assoc-iterator (iterator-create :association assocaition-name)
              assoc-instance (atom (iterator-next assoc-iterator))]
          (while @assoc-instance
            (let [instance-link (.get @hypergraph (.getTargetAt (.get @hypergraph @assoc-instance) 1))
                  roles-order (:roles-order association)]
              (doseq [role-index (range (count roles-order))]
                (println "###" (roles-order role-index))
                (println (.getTargetAt instance-link role-index))))
            (reset! assoc-instance (iterator-next assoc-iterator)))))

      ;;;;; get all instances of class from database
      #_(let [class-name :Location
            class-iterator (iterator-create :class class-name)
            class-instance (atom (iterator-next class-iterator))]
        (while @class-instance
          (println "######")
          (doseq [attribute-name (for [cls-name (get-class-and-all-superclasses-list class-name)
                                       attribuute-name (keys (:attributes (cls-name (:classes @model))))]
                                   attribuute-name)]
            (println "###" attribute-name)
            (doseq [ext (get-instance-extensions @class-instance attribute-name)]
              (println ext)))
          (reset! class-instance (iterator-next class-iterator))))

      (doseq [output-token (find-all-items-by-type run-config :output)]
        (let [output-config-file (-> output-token :filename read-config-file)
              output-type (-> output-config-file first second)
              output-namespaces (-> run-namespaces :models output-type)
              output-config-namespace (:config output-namespaces)
              output-model-namespace (:model output-namespaces)
              output-config (evaluate output-config-namespace output-config-file)
              output-model (apply-resolved-function "create-model" output-model-namespace output-config)
              output-access (:access output-token)
              output-access (if (= :default output-access) (:default-access output-model) output-access)]
          (doseq [extent-token (-> output-token :extents (find-all-items-by-type :extent))]
            (let [extent-config-file (-> extent-token :filename read-config-file)
                  extent-namespaces (-> run-namespaces :hdm hdm-model-type :extents output-type)
                  extent-config-namespace (:config extent-namespaces)
                  extent-model-namespace (:model extent-namespaces)
                  extent-persistance-namespace (:persistance extent-namespaces)
                  extent-config (evaluate extent-config-namespace extent-config-file)
                  extent-model (apply-resolved-function "create-model" extent-model-namespace extent-config output-model)]
              ;(prn-rec-file extent-model "tmp/uml-to-xml-extent-model.clj")
              ;(println extent-model)
              (apply-resolved-function "write-output-data" extent-persistance-namespace extent-model output-access))))))
    (close-database)))


(defn create-prototype
  [output-filename model-type access-vector]
  (apply-resolved-function "create-prototype-configuration" (-> run-namespaces :prototypers model-type) output-filename access-vector))


(defn -main
  []
  (do
    (run "configuration/run.clj")
    ;(create-prototype "configuration/xml-input-model.clj" :xml ["resources/BES-Example.xml"])
    ;(create-prototype "configuration/sql-input-model.clj" :sql ["glue_ogf" "user" "password"])
    ))
