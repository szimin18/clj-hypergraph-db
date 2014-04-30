(ns clj_hypergraph_db.core
  (:require [clj_hypergraph_db.hypergraph_model_parsing_functions :refer :all]
            [clj_hypergraph_db.xml_model_parsing_functions :refer :all])
  (:import [org.hypergraphdb HGEnvironment])
  (:gen-class :main true)
  (:use [clojure.tools.logging :only (info)]))


(def database (atom nil))
(def atoms (atom {}))


(defn create-database
  "
  Creates a database or opens existing one from the folder specified by argument
  "
  [path]
  (let [dbinstance (HGEnvironment/get path)]
    (reset! database dbinstance)))


(defn generate-handler
  ""
  [attributes]
  (.add @database attributes))


(defn create-class
  "
  Creates a type

  Arguments:
  name - name of type
  attributes - fields of type
  "
  [name & attributes]
  (swap! atoms assoc name
         {:handle (generate-handler (if (nil? attributes) '() attributes))
          :attributes attributes})
  )


;(apply assoc (cons (apply hash-map attributes) (filter #(contains? attributes %1) (keys key-value-attrib-list))))


(defmulti parse-token :type)
(defmethod parse-token :class [token] (apply create-class (cons (token :name) (get token :attributes '()))))
(defmethod parse-token :default [token] '())


(defn compute-configuration
  ""
  [file]
  (do
    (doall (for [token file] (parse-token token)))))

(def database-model-parsing-namespaces
  {:hypergraph    '("clj_hypergraph_db.hypergraph_model_parsing_functions/"   'clj_hypergraph_db.hypergraph_model_parsing_functions)
   :xml           '("clj_hypergraph_db.xml_model_parsing_functions/"          'clj_hypergraph_db.xml_model_parsing_functions)})


(defn parse
  "
  Parses a list of definitions of the form (def-type ...) by evaluating each of them separately.
  Returns a list of values returned by each evaluated form.
  "
  [file]
  (let [db-type (second (first (read-string file))) tokens (rest (read-string file))]
  (do
    (info db-type)
    (info tokens)
    (info (get database-model-parsing-namespaces db-type))
    ;; transform the input list by evaluating each form in the list
    ;; in clj_hypergraph_db.model namespace
    (map #(binding [*ns* (find-ns (get database-model-parsing-namespaces db-type))] (eval %))  (read-string file)))))



(defn -main
  "I don't do a whole lot."
  []
  (do
    (create-database "hgdbtest")
    (compute-configuration (load-file "hg-configuration.clj"))
    (info @atoms)))
