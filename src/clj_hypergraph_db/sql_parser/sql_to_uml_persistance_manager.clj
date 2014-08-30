(ns clj_hypergraph_db.sql_parser.sql_to_uml_persistance_manager
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

(defn load-input-data
  [input-persistance-model access-map]
  (let
      [input-model (:input-model input-persistance-model)
       configuration-map (:extent-config input-persistance-model)
       extent-tables (find-all-items-by-type configuration-map :foreach)
       credentials (first access-map)
       atom-for-new-configuration (atom (str "(database :mysql\n          (default-credentials \""
                                             (:database-name credentials) "\" \""
                                             (:user-name credentials)"\" \""
                                             (:password credentials)"\"))\n\n"))
       connection (get-connection (:database-name credentials)  (:user-name credentials)  (:password credentials))
       statement (.createStatement connection)
       prepared-statement-table (.prepareStatement connection (str "select * " "from ?"))]
    ;(println input-model)
    ;(println "------------------------------------------")
    ;(println extent-tables)
    (doseq [[extent-table model-table] (for [extent-table extent-tables
                                                      model-table(:tables input-model)
                                                      :when (= (:table-definition model-table) (first(:table extent-table)))]
                                                  [extent-table model-table])]
      (let [table-name (:table-name model-table)
            query (str "select * from " table-name)
            result-set (.executeQuery statement query)
            columns (:columns model-table)]
        {:body ({:mappings ({:name :ID, :column [:Id], :type :mapping-pk} {:name :Level, :column [:Level], :type :mapping}), :name :UserDomain, :type :add-instance}
                {:name :userDomain_fk_UserDomainId, :type :add-association}), :table [:userdomain], :type :foreach}
        (while (.next result-set)
                new-instance (add-class-instance (:name extent-entity-body))
                meta-data (.getMetaData result-set)]
            (doseq [i (range (.getColumnCount meta-data))]
              (let [column (first (filter #(= (.getColumnName meta-data (+ 1 i)) (:column-name %)) columns))
                    mapping (first (filter #(= (:column-definition column) (first (:column %))) (:mappings extent-entity-body)))
                    data (.getString result-set (+ 1 i))]
                ;(println mapping)
                ;(println column)
                (if (or (= :mapping (:type mapping)) (= :mapping-pk (:type mapping)))
                  (do
                    (add-attribute-instance new-instance (:name extent-entity-body) (:name mapping) data)
                    (println "Attribute " (:name extent-entity-body) " added to " new-instance)
                    )
                  ;(println "No attribute to be added")
                  )

                ))
            (doseq [association [:name associations]]
              (println association)
              )

            )))
      ))
  )
