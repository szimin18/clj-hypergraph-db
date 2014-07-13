(ns clj_hypergraph_db.sql_parser.sql_to_hdm_model_parser
  (:import  [java.io File]
            [com.mysql.jdbc Driver]
            [java.sql DriverManager])
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.persistance.persistance_manager :refer :all]
            [clj_hypergraph_db.sql_parser.sql_common_functions :refer :all]))

(comment (defn get-string-results
  [result-set & column-names]
  (let [return (atom [])]
    (while (.next result-set)
      (swap! return conj (apply vector (map #(.getString result-set %) column-names))))
    @return)))

(defn create-sql-extent-model
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
            (println (:table-name foreach-model-table))
            (.setString prepared-statement-table 1 (:table-name foreach-model-table))
            (println prepared-statement-table)
            (let [result-set (.executeQuery prepared-statement-table)]
              (println (.getString result-set))))
            ))
     )))
