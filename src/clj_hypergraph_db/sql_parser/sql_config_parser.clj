(ns clj_hypergraph_db.sql_parser.sql_config_parser
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
    :table-name table-name :table-definition table-definition :columns columns))

(defn column
  [column-name column-definition & flags]
  (def-item :column
    :column-name column-name :column-definition column-definition :flags flags))