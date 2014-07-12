(ns clj_hypergraph_db.sql_parser.sql_config_parser
  (:import [clojure reflect$access_flag])
  (:require [clj_hypergraph_db.common_parser.common_config_parser :refer :all]))

(defn database
  [type & metadata]
  (def-item :database
            :metadata metadata))

(defn default-credentials
  [database-name user-name password]
  (def-item :credentials
            :database-name database-name
            :user-name user-name
            :password password))

(defn table
  [table-name table-definition & columns]
  (def-item :table
            :table-name table-name
            :table-definition table-definition
            :columns columns))

(defn column
  [column-name column-definition & flags]
  (def-item :column
            :column-name column-name
            :column-definition column-definition
            :flags flags))

(defn relation
  [relation-name relation-definition between-tables reffering-columns]
  (def-item :relation
            :relation-name relation-name
            :relation-definition relation-definition
            :between between-tables
            :reffering reffering-columns))

(defn between
  [& between-tables]
  (def-item :between-tables
            :betbetween-tables between-tables))

(defn referring
  [& reffering-columns]
  (def-item :reffering-columns
            :referring-columns reffering-columns))