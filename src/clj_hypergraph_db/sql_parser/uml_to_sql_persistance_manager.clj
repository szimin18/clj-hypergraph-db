(ns clj_hypergraph_db.sql_parser.uml_to_sql_persistance_manager
  (:import  [java.io File]
            [com.mysql.jdbc Driver]
            [java.sql DriverManager])
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.persistance.persistance_manager :refer :all]
            [clj_hypergraph_db.sql_parser.sql_common_functions :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]))


(defn get-foreign-key-from-path
  "Only with unique associations"
  [[assoc-name role key] current-instance current-class]
  (if-let
    [assoc-handle (-> @model :associations assoc-name :handle)]
    (if-let [assoc-instance (first (hg-find-all (hg-incident @current-instance) (hg-incident-at assoc-handle 0)))]
      nil
      )
    nil

  ))

(defn write-output-data
  [{{output-model-tables :tables} :output-model
   configuration-map :extent-config} access-map]
  (let [extent-tables (find-all-items-by-type configuration-map :foreach)
        credentials (first access-map)
        connection (get-connection (:database-name credentials) (:user-name credentials) (:password credentials))
        statement (.createStatement connection)
        prepared-statement (.prepareStatement connection (str "insert into ? (?) values(?)"))]

    (doseq
        [{name :name body :body} extent-tables
         :let [iterator (iterator-create :class name)
               curent-instance (atom (iterator-next iterator))]]
      #_(println curent-instance)
      (while @curent-instance
        (doseq
            [{mappings :mappings [table] :table} (find-all-items-by-type body :add-entity)
             :let [columns (StringBuilder.) values (StringBuilder.)]]
          (doseq [{table-definition :table-definition table-name :table-name model-columns :columns} output-model-tables
                  :let []
                  :when (= table-definition table)]
            (doseq
              [mapping mappings
               model-column model-columns
              :let[column-definition (-> mapping :column first)
                   column-name (:column-name model-column)]
              :when (= column-definition (:column-definition model-column))]
              (if (= :mapping (:type mapping))
                (do
                  (.append columns column-name)
                  (.append columns ",")
                  (if (string? (first (get-instance-attributes @curent-instance (:name mapping))))
                    (do
                      (.append values "'")
                      (.append values (first (get-instance-attributes @curent-instance (:name mapping))))
                      (.append values "'")
                      )
                    (.append values (first (get-instance-attributes @curent-instance (:name mapping))))
                    )
                  (.append values ",")
                  )
                )
              (if (= :mapping-single-relation (:type mapping))
                (do
                  (.append columns column-name)
                  (.append columns ",")
                  #_(println (:relation-path mapping))
                  #_(println curent-instance)
                  (.append values (get-foreign-key-from-path (:relation-path mapping) curent-instance name))
                  (.append values ",")
                  )
                )
              )

            (println (.substring columns 0 (- (.length columns) 1)))
            (println (.substring values 0 (- (.length values) 1)))
            (.executeUpdate statement (str "insert into " table-name " (" (.substring columns 0 (- (.length columns) 1)) ") values(" (.substring values 0 (- (.length values) 1)) ")"))
            )
          )
        (reset! curent-instance (iterator-next iterator))
        ))))

