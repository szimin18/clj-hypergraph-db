(ns clj_hypergraph_db.sql_parser.sql_model_prototyper
  (:import [org.xml.sax XMLReader]
           [java.io File]
           [com.mysql.jdbc Driver]
           [org.postgresql Driver]
           [java.sql DriverManager])
  (:require [clj_hypergraph_db.persistance.persistance_manager :refer :all]
            [clj_hypergraph_db.sql_parser.sql_config_parser]))


(defn get-string-results
  [result-set & column-names]
  (loop [return []]
    (if (.next result-set)
      (recur conj return (vec (map #(.getString result-set %) column-names)))
      return)))


(defn create-prototype-of-sql-configuration
  [configuration-file-path access-vector]
  (try
    (.remove (File. configuration-file-path))
    (catch Exception e))
  (let [[database-name user-name password] access-vector
        string-builder (StringBuilder.)
        connection (DriverManager/getConnection (str "jdbc:mysql://localhost/" database-name "?user=" user-name "&password=" password))
        statement (.createStatement connection)
        result-set (.executeQuery statement (str "select distinct table_name from information_schema.columns where table_schema = '" database-name "'"))
        prepared-statement-columns (.prepareStatement connection (str "select * "
                                                                      "from information_schema.columns "
                                                                      "where table_schema = '" database-name "' "
                                                                      "and table_name = ? "))
        prepared-statement-column-constraints (.prepareStatement connection (str "select * "
                                                                                 "from information_schema.key_column_usage "
                                                                                 "where table_schema = '" database-name "' "
                                                                                 "and table_name = ? "
                                                                                 "and column_name = ? "))]
    (.append string-builder (str "(database :mysql"
                                 "          (default-credentials \"" database-name "\" \"" user-name "\" \"" password "\"))\n\n"))
    (doseq [[table-name] (get-string-results result-set "table_name")]
      (.setString prepared-statement-columns 1 table-name)
      (.setString prepared-statement-column-constraints 1 table-name)
      (.append string-builder (str "\n\n(table \"" table-name "\" " (keyword table-name)))
      (doseq [[column-name is-nullbale] (get-string-results (.executeQuery prepared-statement-columns) "column_name" "is_nullable")]
        (.setString prepared-statement-column-constraints 2 column-name)
        (let [constraint-data (.executeQuery prepared-statement-column-constraints)]
          (.append string-builder (str
                                    "\n       (column \"" column-name "\" " (keyword column-name)
                                    (if (contains? (into #{} (get-string-results constraint-data "constraint_name")) ["PRIMARY"]) " :pk" "")
                                    (if (= is-nullbale "NO") " :notnull" "")
                                    ")"))))
      (.append string-builder ")"))
    (let [results (get-string-results
                    (.executeQuery statement (str "select * "
                                                  "from information_schema.key_column_usage "
                                                  "where table_schema = '" database-name "' "
                                                  "and constraint_name <> 'PRIMARY' "))
                    "constraint_name"
                    "table_name"
                    "column_name"
                    "referenced_table_name"
                    "referenced_column_name")]
      (doseq [[constraint-name table-name referenced-table-name] (distinct (map #(vector (nth % 0) (nth % 1) (nth % 3)) results))]
        (.append string-builder "\n\n")
        (.append string-builder (str "(relation \"" constraint-name "\" " (keyword constraint-name) "\n"))
        (.append string-builder (str "          (between " (keyword table-name) " " (keyword referenced-table-name) ")"))
        (doseq [[column-name referenced-column-name] (map #(vector (nth % 2) (nth % 4)) (filter #(= constraint-name (first %)) results))]
          (.append string-builder (str "\n          (referring " (keyword column-name) " " (keyword referenced-column-name) ")")))
        (.append string-builder ")")))
    (spit configuration-file-path (.toString string-builder))))

