(ns clj_hypergraph_db.core
  (:require [clj_hypergraph_db.hg_parser.hypergraph_config_parser :refer :all]
            [clj_hypergraph_db.xml_parser.xml_config_parser :refer :all])
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
  (reset! database (HGEnvironment/get path)))


(defn close-database
  "
  Closes the database
  "
  []
  (.close @database))


(defn generate-node-handler
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
         {:handle (generate-node-handler (if (nil? attributes) '() attributes))
          :attributes attributes})
  )


(defmulti parse-token :type)
(defmethod parse-token :class [token] (apply create-class (cons (token :name) (get token :attributes '()))))
(defmethod parse-token :default [token] '())


(defn compute-configuration
  ""
  [file]
  (do
    (doall (for [token file] (parse-token token)))))


(def database-model-parsing-namespaces
  {:hypergraph    'clj_hypergraph_db.hg_parser.hypergraph_config_parser
   :xml           'clj_hypergraph_db.xml_parser.xml_config_parser})


(defn parse
  "
  Parses a list of definitions of the form (def-type ...) by evaluating each of them separately.
  Returns a list of values returned by each evaluated form.
  "
  [file]
  (let [tokens (read-string file)
        db-type (second (first tokens))]
    ;; transform the input list by evaluating each form in the list
    ;; in appropeiate model's namespace
    (map #(binding [*ns* (find-ns (db-type database-model-parsing-namespaces))] (eval %)) tokens)))


(defn -main
  "I don't do a whole lot."
  []
  (do
    (create-database "hgdbtest")
    (compute-configuration (load-file "hg-configuration.clj"))
    (info @atoms)))
