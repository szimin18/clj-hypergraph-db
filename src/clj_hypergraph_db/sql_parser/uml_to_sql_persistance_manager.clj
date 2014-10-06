(ns clj_hypergraph_db.sql_parser.uml_to_sql_persistance_manager
  (:import  [java.io File]
            [com.mysql.jdbc Driver]
            [java.sql DriverManager])
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.persistance.persistance_manager :refer :all]
            [clj_hypergraph_db.sql_parser.sql_common_functions :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]))


;(defn get-values
;  [{iterator :class-instance-iterator
;    attribute-name :attribute-name}]
;  (when iterator
;    (when-let [handle (iterator-get @iterator)]
;      (get-instance-attributes handle attribute-name))))
;
;
;(defn tabs
;  [tab-count]
;  (apply str (repeat tab-count \tab)))
;
;
;(declare write-subtoken)
;
;
;(defn write-all-subtokens
;  [children print-writer tab-count token-starter]
;  (some true? (doall (map #(write-subtoken % print-writer (inc tab-count) token-starter) children))))
;
;
;(defn write-subtoken
;  [[token-name {add-token-list :add-token
;                add-attribute-mapping-list :add-attribute-mapping
;                add-text-mapping-list :add-text-mapping
;                children :children}] print-writer tab-count token-starter]
;  (let [token-name-occured (atom false)]
;    ;print add-tokens
;    (doseq [add-token add-token-list]
;      (let [iterator @(:iterator add-token)
;            current-handle (atom (iterator-next iterator))]
;        (when @current-handle
;          (reset! token-name-occured (token-starter print-writer)))
;        ;print token
;        (while @current-handle
;          ;start token
;          (.print print-writer (str (tabs tab-count) "<" token-name))
;          ;print attributes
;          (doseq [add-attribute-mapping add-attribute-mapping-list]
;            (let [attribute-string-name (:attribute-string-name add-attribute-mapping)]
;              (doseq [attribute-value (get-values add-attribute-mapping)]
;                (.print print-writer (str " " attribute-string-name "=\"" attribute-value "\"")))))
;          ;end token
;          (.print print-writer ">")
;          (if-let [attribute-value (some #(first (get-values %)) add-text-mapping-list)]
;            (.print print-writer attribute-value)
;            (do
;              (.println print-writer)
;              (write-all-subtokens children print-writer tab-count token-starter)
;              (.print print-writer (tabs tab-count))))
;          ;print ending token
;          (.println print-writer (str "</" token-name ">"))
;          ;reset iterator
;          (reset! current-handle (iterator-next iterator)))
;        (iterator-reset iterator)))
;    ;print text values
;    (when-not @token-name-occured
;      (doseq [add-text-mapping add-text-mapping-list]
;        (let [attribute-values (get-values add-text-mapping)]
;          (when (not-empty attribute-values)
;            (reset! token-name-occured (token-starter print-writer)))
;          (doseq [attribute-value attribute-values]
;            (.println print-writer (str (tabs tab-count) "<" token-name ">" attribute-value "</" token-name ">"))))))
;    ;write subtokens of container
;    (or
;      @token-name-occured
;      (let [token-started (atom false)
;            token-starter (fn [print-writer]
;                            (do
;                              '(token-starter print-writer)
;                              (when-not @token-started
;                                (reset! token-started true)
;                                (.println print-writer (str (tabs tab-count) "<" token-name ">")))
;                              true))]
;        (when (write-all-subtokens children print-writer tab-count token-starter)
;          (.println print-writer (str (tabs tab-count) "</" token-name ">"))
;          true)))))


(defn write-output-data
  [{{output-model-tables :tables} :output-model
   configuration-map :extent-config} access-map]
  (let [extent-tables (find-all-items-by-type configuration-map :foreach)
        credentials (first access-map)
        statement (.createStatement (get-connection (:database-name credentials) (:user-name credentials) (:password credentials)))]
    (println credentials)
    (println statement)
    (println extent-tables)
      (doseq
          [{table :table body :body} extent-tables
           :let [{extent-entity-name :name
                  extent-entity-mappings :mappings} (find-first-item-by-type body :add-entity)]
           :when extent-entity-name]
        (println extent-entity-name)
        )
    )
  )
