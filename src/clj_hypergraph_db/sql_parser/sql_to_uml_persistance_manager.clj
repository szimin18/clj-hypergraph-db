(ns clj_hypergraph_db.sql_parser.sql_to_uml_persistance_manager
  (:import  [java.io File]
            [com.mysql.jdbc Driver]
            [org.postgresql Driver]
            [java.sql DriverManager])
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.persistance.persistance_manager :refer :all]
            [clj_hypergraph_db.sql_parser.sql_common_functions :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]))


(defn load-input-data
  [{{input-model-tables :tables} :input-model
    configuration-map :extent-config} access-map]
  (let [extent-tables (find-all-items-by-type configuration-map :foreach)
        {database-name :database-name
         user-name :user-name
         password :password} (first access-map)
        statement (.createStatement (get-connection database-name user-name password))]
    (doseq [{table :table
             body :body} extent-tables
            :let [{extent-entity-name :name
                   extent-entity-mappings :mappings} (find-first-item-by-type body :add-instance)]
            :when extent-entity-name]
      (doseq [{model-table-name :table-name
               columns :columns
               model-table-definition :table-definition} input-model-tables
              :when (= model-table-definition (first table))
              :let [result-set (->> model-table-name (str "select * from ") (.executeQuery statement))]]
        (while (.next result-set)
          (let [new-instance (add-class-instance extent-entity-name)
                meta-data (.getMetaData result-set)]
            (doseq [i (range 1 (inc (.getColumnCount meta-data)))
                    :let [column-definition (some #(if (= (.getColumnName meta-data i) (:column-name %)) (:column-definition %)) columns)
                          {mapping-name :name
                           mapping-type :type} (some #(if (= column-definition (first (:column %))) %) extent-entity-mappings)]
                    :when (#{:mapping :mapping-pk} mapping-type)
                    :let [data (.getString result-set i)]
                    :when data]
              (add-attribute-instance new-instance mapping-name data))))))
    (doseq [{[table] :table
             body :body} extent-tables
            :let [associations (find-all-items-by-type body :add-association)]
            :when (not-empty associations)]
      ;(println associations)
      (doseq [{model-table-definition :table-definition
               model-table-name :table-name
               columns :columns} input-model-tables
              :when (= model-table-definition table)
              :let [result-set (->> model-table-name (str "select * from ") (.executeQuery statement))]]
        (doseq [{association-name :name
                 association-roles :roles} associations]
          (while (.next result-set)
            (let [new-association (atom (add-association-instance association-name))
                  meta-data (.getMetaData result-set)]
              (doseq [i (range 1 (inc (.getColumnCount meta-data)))
                      :let [column (some #(if (= (.getColumnName meta-data i) (:column-name %)) %) columns)
                            [role-name] (some #(if (= (:column-definition column) (:column %)) (:name %)) association-roles)]
                      :when role-name
                      :let [data (.getString result-set i)]]
                ;(println model-table-definition)
                ;(println association-name " " role-name " " data)
                (swap! new-association add-role-instance-pk association-name role-name data)))))))))
