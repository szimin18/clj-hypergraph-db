(ns clj_hypergraph_db.sql_parser.uml_to_sql_persistance_manager
  (:import  [java.io File]
            [com.mysql.jdbc Driver]
            [java.sql DriverManager])
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.persistance.persistance_manager :refer :all]
            [clj_hypergraph_db.sql_parser.sql_common_functions :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]))


(def variables-table (atom {}))
(def defined-function (atom nil))
(def current-instance (atom nil))

(defn get-foreign-key-from-path
  "Only with unique associations"
  [[role attribute] current-instance assoc-name]
  (when-let [assoc-handle (-> @model :associations assoc-name :handle)]
    #_(println assoc-name role attribute)
    (if-let [assoc-instance (hg-find-one (hg-incident @current-instance) (hg-incident-at assoc-handle 0))]
      (let [instance (hg-link-target-at (hg-get assoc-instance) (get-roles-index assoc-name role))]
        (do #_(println (first (get-instance-attributes instance attribute))) (first (get-instance-attributes instance attribute))))
      #_(println "No association found"))))

(defn evaluate
  [mapping]
  (cond
    (= (:type mapping) :mapping-relation)
    #_(if (coll? (:name mapping))
      #_(evaluate (:name mapping) current-instance values variables-table function-table))
    (get-foreign-key-from-path (:relation-path mapping) current-instance (first (:relation mapping)))

    (= (:type mapping) :mapping)
    (if (coll? (:name mapping))
      (evaluate (:name mapping))
      (let
        [attribute (if ((:name mapping) @variables-table) ((:name mapping) @variables-table)(first (get-instance-attributes @current-instance (:name mapping))))]
        attribute))

    #_(= (:type (:name mapping) :call))
    (= (:type mapping) :call)
    (let
      #_(cond
          (nil? arg) nil
          (arg @variables-table) (arg @variables-table)
          (keyword? arg) (first (get-instance-attributes @current-instance arg))
          (coll? arg ) (evaluate arg)
          :else arg
          )
      [args (map (fn [arg] (if (nil? arg) arg (evaluate arg))) (:args mapping))]
      #_(println mapping)
      #_(println args)
      (apply str (apply (@defined-function (:fn-name  mapping)) args))
      )
    (keyword? mapping)
    (if (mapping @variables-table)
        (mapping @variables-table)
        (first (get-instance-attributes @current-instance mapping)))

    :else mapping
    )
  )

(defn bind
  [from to]
  (reset! variables-table (assoc-in @variables-table [to] (evaluate from))))


(defn insert-into-tables
  [tables output-model-tables statement]
  (doseq
    [{name :name body :body} tables
     :let [iterator (iterator-create :class name)
           instance-variables (find-all-items-by-type body :bind)]]

    (reset! current-instance (iterator-next iterator))

    (while @current-instance
      (doseq [{from :from to :to} instance-variables]
        #_(println "from:" from "to:" to)
        (bind from to))

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
            (.append columns column-name)
            (.append columns ",")
            (let [value (evaluate mapping)]
              (if (string? value)
                (do
                  (.append values "'")
                  (.append values value)
                  (.append values "'")
                  )
                (.append values value)
                )
              (.append values ","))
            ;(if (= :mapping (:type mapping))
            ;  (evaluate mapping current-instance values variables-table function-table)
            ;  )
            ;(if (= :mapping-relation (:type mapping))
            ;  (evaluate mapping current-instance values variables-table function-table)
            ;  )
            )
          (println (str "insert into " table-name " (" (.substring columns 0 (- (.length columns) 1)) ") values(" (.substring values 0 (- (.length values) 1)) ")"))
          #_(.executeUpdate statement (str "insert into " table-name " (" (.substring columns 0 (- (.length columns) 1)) ") values(" (.substring values 0 (- (.length values) 1)) ")"))
          )
        )
      (reset! current-instance (iterator-next iterator))
      )
    )
  )

(defn insert-into-associations
  [association-tables output-model-tables statement]

  (doseq [{assoc-name :name body :body} association-tables
          :let [iterator (iterator-create :association assoc-name)]]

    #_(println assoc-name)
    #_(println body)
    (doseq [current-instance (iterator-lazy-seq iterator)]

      (doseq [{mappings :mappings [table] :table} (find-all-items-by-type body :add-entity)
              :let [columns (StringBuilder.) values (StringBuilder.)]]

        (doseq [{table-definition :table-definition table-name :table-name model-columns :columns} output-model-tables
                :let []
                :when (= table-definition table)]

          (doseq
            [mapping mappings
             model-column model-columns
             :let [[column-definition] (:column mapping)
                   column-name (:column-name model-column)]
             :when (= column-definition (:column-definition model-column))]

            (let [[role attribute] (:relation-path mapping)
                  instance (hg-link-target-at (hg-get current-instance) (get-roles-index assoc-name role))
                  value (first (get-instance-attributes instance attribute))]
              (.append columns column-name)
              (.append columns ",")

              (if (string? value)
                (do
                  (.append values "'")
                  (.append values value)
                  (.append values "'")
                  )
                (.append values value)
                )
              (.append values ",")
              )
            )
          (println (str "insert into " table-name " (" (.substring columns 0 (- (.length columns) 1)) ") values(" (.substring values 0 (- (.length values) 1)) ")"))
          #_(.executeUpdate statement (str "insert into " table-name " (" (.substring columns 0 (- (.length columns) 1)) ") values(" (.substring values 0 (- (.length values) 1)) ")"))
          )))))


(defn write-output-data
  [{{output-model-tables :tables} :output-model
   configuration-map :extent-config} access-map]
  (let [extent-tables (find-all-items-by-type configuration-map :foreach)
        credentials (first access-map)
        association-tables (find-all-items-by-type configuration-map :association)
        connection (get-connection (:database-name credentials) (:user-name credentials) (:password credentials))
        statement (.createStatement connection)
        extracted-function (reduce (fn [fn-map {name :name body :body}] (assoc fn-map name body)) {} (find-all-items-by-type configuration-map :function))
        global-variables (find-all-items-by-type configuration-map :bind)]

    (reset! defined-function extracted-function)

    (swap! variables-table assoc :VAR "HARDCODED")
    #_(bind :VAR "HARDCODED")
    (doseq [{key :to  value :from } global-variables]
      (swap! variables-table assoc key value))



    (insert-into-tables extent-tables output-model-tables statement)
    (insert-into-associations association-tables output-model-tables statement)


    ))




