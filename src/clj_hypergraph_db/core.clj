(ns clj_hypergraph_db.core
  (:gen-class :main true)
  (:require [clj_hypergraph_db.persistance.persistance_manager :refer :all] ;peek database
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all] ;get-class-instances
            [clj_hypergraph_db.common_parser.common_model_parser :refer :all]))

(def run-namespaces
  {:hdm {:uml {:config 'clj_hypergraph_db.hdm_parser.hdm_uml_config_parser
               :model 'clj_hypergraph_db.hdm_parser.hdm_uml_model_parser
               :manager 'clj_hypergraph_db.hdm_parser.hdm_uml_model_manager
               :extents {:xml {}
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
    (apply (resolve (symbol (read-string (str namespace "/" function-string)))) args)))


(defn run
  [run-filename]
  (do
    (create-database "hgdbtest")
    (let [run-config-file (read-string (str "(" (slurp run-filename) ")"))
          run-config (evaluate 'clj_hypergraph_db.run_config_parser run-config-file)
          hdm-config-file (read-string (str "(" (slurp (:filename (find-first-item-by-type run-config :hdm))) ")"))
          hdm-model-type (second (first hdm-config-file))
          hdm-namespaces ((run-namespaces :hdm) hdm-model-type)
          hdm-config-namespace (:config hdm-namespaces)
          hdm-model-namespace (:model hdm-namespaces)
          hdm-manager-namespace (:manager hdm-namespaces)
          hdm-config (evaluate hdm-config-namespace hdm-config-file)
          hdm-model (apply-resolved-function "create-model" hdm-model-namespace hdm-config)]
      (apply-resolved-function "set-model" hdm-manager-namespace hdm-model)
      (doseq [input-token (find-all-items-by-type run-config :input)]
        (let [input-config-file (read-string (str "(" (slurp (:filename input-token)) ")"))
              input-type (second (first input-config-file))
              input-namespaces ((run-namespaces :models) input-type)
              input-config-namespace (:config input-namespaces)
              input-model-namespace (:model input-namespaces)
              input-config (evaluate input-config-namespace input-config-file)
              input-model (apply-resolved-function "create-model" input-model-namespace input-config)
              input-access (:access input-token)
              input-access (if (= :default input-access) (:default-access input-model) input-access)]
          (doseq [extent-token (find-all-items-by-type (:extents input-token) :extent)]
            (let [extent-config-file (read-string (str "(" (slurp (:filename extent-token)) ")"))
                  extent-namespaces ((((run-namespaces :models) input-type) :extents) hdm-model-type)
                  extent-config-namespace (:config extent-namespaces)
                  extent-model-namespace (:model extent-namespaces)
                  extent-persistance-namespace (:persistance extent-namespaces)
                  extent-config (evaluate extent-config-namespace extent-config-file)
                  extent-model (apply-resolved-function "create-model" extent-model-namespace extent-config input-model)]
              (apply-resolved-function "load-input-data" extent-persistance-namespace extent-model input-access))))))



    ;(println (get-class-instances :UserDomain))
    ;(println (get-class-instances :AdminDomain))


    (close-database)))


(defn create-prototype
  [output-filename model-type access-vector]
  (apply-resolved-function "create-prototype-configuration" ((run-namespaces :prototypers) model-type) output-filename access-vector))


(defn -main
  []
  (do
    ;(run "configuration/run.clj")
    (create-prototype "configuration/xml-input-model.clj" :xml ["resources/BES-Example.xml"])
    ;(create-prototype "configuration/sql-input-model.clj" :sql ["glue_ogf" "user" "password"])
    ))
