(ns clj_hypergraph_db.sql_parser.sql_config_parser
  (:import [clojure reflect$access_flag])
  (:require [clj_hypergraph_db.common_parser.common_config_parser :refer :all]))



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
  [relation-name relation-definition between-tables referring-columns]
  (def-item :relation
            :relation-name relation-name
            :relation-definition relation-definition
            :between between-tables
            :referring referring-columns))

(defn between
  [& between-tables]
  (def-item :between-tables
            :betbetween-tables between-tables))

(defn referring
  [& referring-columns]
  (def-item :referring-columns
            :referring-columns referring-columns))
