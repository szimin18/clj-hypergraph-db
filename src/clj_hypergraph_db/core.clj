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


(defn def-db
  [db-type]
  (get database-model-parsing-namespaces db-type))

(defn parse
  ""
  [file]
  (let [db-type (first file)tokens (rest file)]
  (doall
    (map
      eval
      (read-string
        (reduce
          (fn
            [text token]
            (clojure.string/replace
              text
              (str token)
              (str (def-db db-type) token)))
          tokens
          (keys (ns-publics 'clj_hypergraph_db.hypergraph_model_parsing_functions))))))))


(defn -main
  "I don't do a whole lot."
  []
  (do
    (create-database "hgdbtest")
    (compute-configuration (load-file "hg-configuration.clj"))
    (info @atoms)))
