(ns clj_hypergraph_db.persistance.persistance_manager
  (:import [org.hypergraphdb HGEnvironment HGPlainLink HGValueLink HGHandle HGQuery$hg HyperGraph]
           [org.hypergraphdb.query And]
           [org.hypergraphdb.algorithms HGBreadthFirstTraversal SimpleALGenerator]
           [java.io File])
  (:use [korma.db]
        [korma.core])
  (:require [clojure.java.jdbc :as sql]
            [clj_hypergraph_db.common_parser.common_functions :refer :all]))


(defn delete-file-recursively
  "Delete java.io.File 'file'. If it's a directory, recursively delete all its contents."
  [file]
  (when (.isDirectory file)
    (doseq [child (.listFiles file)]
      (delete-file-recursively child)))
  (.delete file))


(defn create-database-specification
  []
  (sqlite3 {:db "db/korma.db"
            :user "user"
            :password "password"}))


(defn create-database
  [database-specification]
  (.delete (File. (:db database-specification)))
  (defdb test-db database-specification))


(defn remove-database
  [database-specification]
  ;TODO resore removal of temp db after ending tests
  #_(.delete (File. (:db database-specification))))


(defn- create-table
  [database-specification table-create-sql]
  (sql/with-db-connection
    [connection database-specification]
    (sql/execute!
      connection
      [table-create-sql])))


(defn create-class-table
  [database-specification table-symbol]
  (let [table-name (name table-symbol)]
    (create-table database-specification
                  (format "CREATE TABLE %s (id INTEGER NOT NULL, PRIMARY KEY (id))" table-name))
    (eval `(defentity ~table-symbol))))


(defn create-attribute-table
  [database-specification class-table-symbol table-symbol]
  (let [table-name (name table-symbol)
        id-column-name (str (name class-table-symbol) "_id")]
    (create-table database-specification
                  (format (str "CREATE TABLE %s "
                               "(id INTEGER PRIMARY KEY AUTOINCREMENT, "
                               "%s INTEGER NOT NULL, "
                               "value VARCHAR(255) NOT NULL) "
                               ;"PRIMARY KEY (id, %s, value))"
                               )
                          table-name id-column-name id-column-name))
    (eval `(defentity ~table-symbol
                      (belongs-to ~class-table-symbol)))))


(defn test-korma
  []
  (let [database-specification (create-database-specification)
        sym1 (gensym (name :User))
        ]


    (.delete (File. "db/korma.db"))
    ;(println "testing korma itself...")
    (defdb test-db database-specification)
    ;(declare users)
    (create-class-table database-specification sym1)

    (eval `(defentity ~sym1
               (database test-db)))


    (eval `(insert ~sym1
            (values {:first "john" :last "doe"})))

    (println (select sym1
                     (fields :first :last)))

    (.delete (File. "db/korma.db"))


    ))



















(def hypergraph (atom nil))
(def hypergraph-path (atom nil))


(defn hg-create
  [path]
  (reset! hypergraph-path path)
  (delete-file-recursively (File. path))
  (reset! hypergraph (HGEnvironment/get path)))


(defn hg-close
  []
  (.close @hypergraph)
  (delete-file-recursively (File. @hypergraph-path)))


(defn hg-add-node
  [data]
  (.add @hypergraph data))


(defn hg-add-link
  ([target-list]
   (.add @hypergraph (HGPlainLink. (into-array HGHandle target-list))))
  ([data target-list]
   (.add @hypergraph (HGValueLink. data (into-array HGHandle target-list)))))


(defn hg-remove
  [handle]
  (.remove @hypergraph handle))


(defn hg-get
  [handle]
  (.get @hypergraph handle))


(defn hg-find-one
  ([hg-condition1]
   (->> hg-condition1 (HGQuery$hg/findOne @hypergraph)))
  ([hg-condition1 hg-condition2]
   (->> (And. hg-condition1 hg-condition2) (HGQuery$hg/findOne @hypergraph)))
  ([hg-condition1 hg-condition2 hg-condition3]
   (->> (And. hg-condition1 hg-condition2 hg-condition3) (HGQuery$hg/findOne @hypergraph))))


(defn hg-find-all
  ([hg-condition1]
   (->> hg-condition1 (HGQuery$hg/findAll @hypergraph)))
  ([hg-condition1 hg-condition2]
   (->> (And. hg-condition1 hg-condition2) (HGQuery$hg/findAll @hypergraph)))
  ([hg-condition1 hg-condition2 hg-condition3]
   (->> (And. hg-condition1 hg-condition2 hg-condition3) (HGQuery$hg/findAll @hypergraph))))


(defn hg-eq
  [value]
  (HGQuery$hg/eq value))


(defn hg-incident
  [handle]
  (HGQuery$hg/incident handle))


(defn hg-incident-at
  [handle index]
  (HGQuery$hg/incidentAt handle index))


(defn hg-link-first-target
  [link]
  (.getTargetAt link 1))


(defn hg-link-target-at
  [link index]
  (.getTargetAt link index))


(defn hg-link-arity
  [link]
  (.getArity link))


(defn hg-link-value
  [link]
  (.getValue link))
