(ns clj_hypergraph_db.hdm_parser.hdm_uml_model_manager
  (:require [clj_hypergraph_db.hdm_parser.hdm_uml_model_parser :refer :all]
            [clj_hypergraph_db.persistance.persistance_manager :refer :all]))


(def model (atom nil))


(defn create-hdm-uml-persistance-model
  [configuration-filename]
  (reset!
    model
    (binding [*ns* (find-ns 'clj_hypergraph_db.hdm_parser.hdm_uml_model_parser)]
      (create-hdm-uml-model
        (map
          #(binding [*ns* (find-ns 'clj_hypergraph_db.hdm_parser.hdm_uml_config_parser)] (eval %))
          (read-string (str "(" (slurp configuration-filename) ")")))))))


(defn add-class-instance
  [class-name]
  (let [class-handle (:handle ((:classes @model) class-name))
        instance-handle (add-node class-name)]
    (add-link :instance (list class-handle instance-handle))
    instance-handle))


(defn add-attribute-instance
  [class-instance-handle class-name attribute-name attribute-data]
  (let [attribute-handle (:handle ((:attributes ((:classes @model) class-name)) attribute-name))
        instance-handle (add-node attribute-data)]
    (add-link :instance (list attribute-handle instance-handle))
    (add-link attribute-name (list class-instance-handle instance-handle))
    instance-handle))