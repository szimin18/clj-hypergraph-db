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
                                             (:user-name credentials) "\" \""
                                             (:password credentials) "\"))\n\n"))
       connection (get-connection (:database-name credentials) (:user-name credentials) (:password credentials))
       statement (.createStatement connection)
       prepared-statement-table (.prepareStatement connection (str "select * " "from ?"))]
    ;(println input-model)
    ;(println "------------------------------------------")
    ;(println extent-tables)
    (doseq [[extent-table model-table] (for [extent-table extent-tables
                                             model-table (:tables input-model)
                                             :when (= (:table-definition model-table) (do #_(println extent-table ) (first (:table extent-table))))]
                                         [extent-table model-table])]

      #_(println extent-table)
      #_(println (:body extent-table))
      (if-let [extent-entity (do #_(println (find-first-item-by-type (:body extent-table) :add-instance)) (find-first-item-by-type (:body extent-table) :add-instance))]

        (let [table-name (:table-name model-table)
              query (str "select * from " table-name)
              result-set (.executeQuery statement query)
              columns (:columns model-table)]

          #_(println extent-entity)

          (while (.next result-set)

            (let
                [new-instance (add-class-instance (:name extent-entity))
                 meta-data (.getMetaData result-set)]
              (doseq [i (range (.getColumnCount meta-data))]
                (let [column (first (filter #(= (.getColumnName meta-data (+ 1 i)) (:column-name %)) columns))
                      mapping (first (filter #(= (:column-definition column) (first (:column %))) (:mappings extent-entity)))
                      data (.getString result-set (+ 1 i))]

                  (if (and data (or (= :mapping (:type mapping)) (= :mapping-pk (:type mapping))))
                    (do
                      ;(println (:name extent-entity))
                      ;(println (:name mapping))
                      ;(println data)

                      (add-attribute-instance new-instance (:name extent-entity) (:name mapping) data)
                      #_(println "Attribute " (:name mapping) " added to " new-instance)
                      )
                    )

                  ))


              #_(println "Added new instance - " (:name extent-entity) " as " new-instance)              )
            )
          )
        )
      )

    (doseq [[extent-table model-table] (for [extent-table extent-tables
                                             model-table (:tables input-model)
                                             :when (= (:table-definition model-table) (do #_(println extent-table ) (first (:table extent-table))))]
                                         [extent-table model-table])]
      (if-let [associations (do #_(println (find-first-item-by-type (:body extent-table) :add-instance)) (find-all-items-by-type (:body extent-table) :add-association))]
        (let [table-name (:table-name model-table)
              query (str "select * from " table-name)
              result-set (.executeQuery statement query)
              columns (:columns model-table)]

          (doseq [association associations]
            #_(println association)
            (while (.next result-set)
              (let [new-association (add-association-instance (do #_(println (:name association)) (:name association)))
                    meta-data (.getMetaData result-set)]

                (doseq [i (range (.getColumnCount meta-data))]
                  (let [column (first (filter #(= (.getColumnName meta-data (+ 1 i)) (:column-name %)) columns))]
                    (if-let [role (first (filter #(= (:column-definition column) (:column %)) (:roles association)))]
                      (let [data (.getString result-set (+ 1 i))]
                        (add-role-instance-pk new-association (:name association) (first (:name role)) data)
                        #_(println "Added new role: " (:name role) ", to " new-association " - " data)
                        )
                      )
                    )
                  )

                #_(doseq [i (range (.getColumnCount meta-data))]
                  (let [column (first (filter #(= (.getColumnName meta-data (+ 1 i)) (:column-name %)) columns))
                        role (first (filter #(= (:column-definition column) (:column %)) (:roles association)))
                        data (.getString result-set (+ 1 i))]
                    (println column)
                    (println role)
                    (println data)
                    #_(if (or (= :mapping (:type mapping)) (= :mapping-pk (:type mapping)))
                        (do
                          (add-attribute-instance new-instance (:name extent-entity) (:name mapping) data)
                          (println "Attribute " (:name extent-entity) " added to " new-instance)
                          )
                        )

                    )
                  )

                )
              )

            )

          )
        )
      )
    )
  )
