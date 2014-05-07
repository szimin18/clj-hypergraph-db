(ns clj_hypergraph_db.persistance.persistance_manager
  (:import [org.hypergraphdb HGEnvironment HGPlainLink HGValueLink HGHandle]
           [java.io File]))


(def database (atom nil))


(defn create-database
  "
  Creates a database or opens existing one from the folder specified by argument
  "
  [path]
  (do
    (.delete (File. "hgdbtest"))
    (reset! database (HGEnvironment/get path))))


(defn close-database
  "
  Closes the database
  "
  []
  (.close @database))


(defn add-node
  [data]
  (.add @database data))


(defn add-plain-link
  [target-node-handles-list]
  (.add @database (new HGPlainLink (into-array HGHandle target-node-handles-list))))


(defn add-value-link
  [data target-node-handles-list]
  (.add @database (new HGValueLink data (into-array HGHandle target-node-handles-list))))