(ns clj_hypergraph_db.core
  (:require [clj_hypergraph_db.persistance.persistance_manager :refer :all]
            [clj_hypergraph_db.hg_parser.hypergraph_config_parser :refer :all]
            [clj_hypergraph_db.xml_parser.xml_config_parser :refer :all]
            [clj_hypergraph_db.xml_parser.xml_model_parser :refer :all]
            [clj_hypergraph_db.xml_parser.xml_persistance_manager :refer :all]
            [clj_hypergraph_db.xml_parser.xml_model_prototyper :refer :all]
            [clj_hypergraph_db.sql_parser.sql_model_prototyper :refer :all])
  (:gen-class :main true)
  (:use [clojure.tools.logging :only (info)]))


(def config-parser-namespaces
  {:hypergraph    'clj_hypergraph_db.hg_parser.hypergraph_config_parser
   :xml           'clj_hypergraph_db.xml_parser.xml_config_parser})


(def model-paser-namespaces
  {:xml           'clj_hypergraph_db.xml_parser.xml_model_parser})


(defn parse
  [file metaclass-handle]
  (let [tokens (read-string file)
        db-type (second (first tokens))
        parsed-configuration (map #(binding [*ns* (find-ns (db-type config-parser-namespaces))] (eval %)) tokens)
        modeled-configuration (binding [*ns* (find-ns (db-type model-paser-namespaces))] (create-model parsed-configuration metaclass-handle))]
    modeled-configuration))


(defn -main
  []
  (create-prototype-of-sql-configuration "glue_ogf" "user" "password" "configuration/sql-input-model.clj"))


;(defn -main
;  []
;  (do
;    (create-database "hgdbtest")
;    (let [metaclass-handle (add-node :metaclass)]
;      (load-input-data (parse (str "(" (slurp "xml-input-model.clj") ")") metaclass-handle) "sample-xml.xml")
;      (write-output-data (parse (str "(" (slurp "xml-output-model.clj") ")") metaclass-handle) "sample-output-xml.xml")
;      ;(peek-database)
;      )
;    (close-database)))





;({:handle-to-top #<WeakHandle weakHandle(56d998bf-649d-4662-828b-18565cf93aa8)>
;  :handle #<WeakHandle weakHandle(35a7c5d3-500b-4ab2-bbd7-240ec181534f)>
;  :relative-path ({:type :token
;                   :name "People"}
;                  {:type :token
;                   :name "Person"})
;  :name :Person
;  :type :class
;  :attributes ({:handle #<WeakHandle weakHandle(c6ef04f1-e7ff-4d82-9dac-7d2f84e597c5)>
;                :type :pk
;                :attributes (:Name :Surname)}
;               {:handle-to-top #<WeakHandle weakHandle(235aa814-310c-4260-b71d-fd7a45545890)>
;                :handle #<WeakHandle weakHandle(1b28f578-c479-4dda-b0c9-a3eb1bc18fb3)>
;                :name :Surname
;                :type :field
;                :attributes ({:name :string
;                              :type :type}
;                             {:type :path
;                              :attributes ({:type :attribute
;                                            :name :Surname})})}
;               {:handle-to-top #<WeakHandle weakHandle(daa4fb7d-a577-4436-8aa0-78afa4213fa5)>
;                :handle #<WeakHandle weakHandle(169f6b4d-79aa-43f6-b2dd-61d2bc93a86c)>
;                :name :Name
;                :type :field
;                :attributes ({:name :string
;                              :type :type}
;                             {:type :path
;                              :attributes ({:type :token
;                                            :name "Name"}
;                                           {:type :data
;                                            :name :Name-data})})})})
