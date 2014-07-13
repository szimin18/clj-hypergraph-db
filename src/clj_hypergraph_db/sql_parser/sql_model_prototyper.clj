(ns clj_hypergraph_db.sql_parser.sql_model_prototyper
  (:import [org.xml.sax XMLReader]
           [java.io File]
           [com.mysql.jdbc Driver]
           [java.sql DriverManager])
  (:require [clj_hypergraph_db.persistance.persistance_manager :refer :all]
            [clj_hypergraph_db.sql_parser.sql_config_parser]))


(defn get-string-results
  [result-set & column-names]
  (let [return (atom [])]
    (while (.next result-set)
      (swap! return conj (apply vector (map #(.getString result-set %) column-names))))
    @return))


(defn create-prototype-of-sql-configuration
  [database-name user-name password configuration-file-path]
  (do
    (try
      (.remove (File. configuration-file-path))
      (catch Exception e))
    (let [atom-for-new-configuration (atom (str "(database :mysql\n          (default-credentials \"" database-name "\" \"" user-name "\" \"" password "\"))\n\n"))
          ;connection (get-connection database-name user-name password)
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
      (doseq [[table-name] (get-string-results result-set "table_name")]
        (.setString prepared-statement-columns 1 table-name)
        (.setString prepared-statement-column-constraints 1 table-name)
        (swap! atom-for-new-configuration str "(table \"" table-name "\" " (keyword table-name))
        (doseq [[column-name is-nullbale] (get-string-results (.executeQuery prepared-statement-columns) "column_name" "is_nullable")]
          (.setString prepared-statement-column-constraints 2 column-name)
          (let [constraint-data (.executeQuery prepared-statement-column-constraints)]
            (swap! atom-for-new-configuration str
                   "\n       (column \"" column-name "\" " (keyword column-name)
                   (if (contains? (apply hash-set (get-string-results constraint-data "constraint_name")) ["PRIMARY"]) " :pk" "")
                   (if (= is-nullbale "NO") " :notnull" "")
                   ")")))
        (swap! atom-for-new-configuration str ")\n\n"))
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
          (swap! atom-for-new-configuration str "(relation \"" constraint-name "\" " (keyword constraint-name))
          (swap! atom-for-new-configuration str "\n          (between " (keyword table-name) " " (keyword referenced-table-name) ")")
          (doseq [[column-name referenced-column-name] (map #(vector (nth % 2) (nth % 4)) (filter #(= constraint-name (first %)) results))]
            (swap! atom-for-new-configuration str
                   "\n          (referring " (keyword column-name) " " (keyword referenced-column-name) ")"))
          (swap! atom-for-new-configuration str ")\n\n")))
      (spit configuration-file-path @atom-for-new-configuration))))

