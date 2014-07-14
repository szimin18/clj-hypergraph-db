(ns clj_hypergraph_db.sql_parser.sql_to_hdm_model_parser
  (:import  [java.io File]
            [com.mysql.jdbc Driver]
            [java.sql DriverManager])
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.persistance.persistance_manager :refer :all]
            [clj_hypergraph_db.sql_parser.sql_common_functions :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]))


(defn get-entry
  [result-set]
  (let [return (atom [])
        meta-data (.getMetaData result-set)]
      (doseq [i (range (.getColumnCount meta-data))]
        (swap! return conj (.getString result-set (+ 1 i))))
    @return))


(defn import-sql-into-hdm
  [configuration-list input-model]
  (let [foreach-tables (find-all-items-by-type configuration-list :foreach)
        credentials (:default-configuration input-model)
        atom-for-new-configuration (atom (str "(database :mysql\n          (default-credentials \""
                                              (:database-name credentials) "\" \""
                                              (:user-name credentials)"\" \""
                                              (:password credentials)"\"))\n\n"))
        connection (get-connection (:database-name credentials)  (:user-name credentials)  (:password credentials))
        statement (.createStatement connection)
        prepared-statement-table (.prepareStatement connection (str "select * " "from ?"))]

    ;foreach-tables
    (doseq [foreach-table foreach-tables]
      (doseq [foreach-model-table (:tables input-model)]
        (if (= (:table-definition foreach-model-table) (first(:table foreach-table)))
          (do
            (let [table-name (:table-name foreach-model-table)
                  query (str "select * from " table-name)
                  result-set (.executeQuery statement query)
                  columns (:columns foreach-model-table)]

              (while (.next result-set)
                (let [body (first (:body foreach-table))
                      new-instance (add-class-instance (:name body))
                      meta-data (.getMetaData result-set)]
                  (doseq [i (range (.getColumnCount meta-data))]
                    (let [column (first (filter #(= (.getColumnName meta-data (+ 1 i)) (:column-name %)) columns))
                          mapping (first (filter #(= (:column-definition column) (first (:column %))) (:mappings body)))
                          data (.getString result-set (+ 1 i))]
                      ;(println mapping)
                      ;(println column)
                      (if (or (= :mapping (:type mapping)) (= :mapping-pk (:type mapping)))
                        (do
                          (add-attribute-instance new-instance (:name body) (:name mapping) data)
                          ;(println "Attribute " (:name body) " added to " new-instance)
                        )
                        ;(println "No attribute to be added")
                        )

                    ))

                  )))
            ))
     ))))
