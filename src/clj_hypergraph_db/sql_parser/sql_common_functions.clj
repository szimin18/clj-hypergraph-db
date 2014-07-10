(ns clj_hypergraph_db.sql_parser.sql_common_functions
  (:import [org.xml.sax XMLReader]
           [java.io File]
           [com.mysql.jdbc Driver]
           [java.sql DriverManager])
  (:require [clj_hypergraph_db.persistance.persistance_manager :refer :all]
            [clj_hypergraph_db.xml_parser.xml_common_functions :refer :all]))

(defn get-connection
  [database-name user-name password]
  (DriverManager/getConnection (str "jdbc:mysql://localhost/" database-name "?user=" user-name "&password=" password)))