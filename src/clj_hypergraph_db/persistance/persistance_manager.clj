(ns clj_hypergraph_db.persistance.persistance_manager
  (:import [org.hypergraphdb HGEnvironment HGPlainLink HGValueLink HGHandle HGQuery$hg HyperGraph]
           [org.hypergraphdb.query HGQueryCondition And]
           [org.hypergraphdb.algorithms HGBreadthFirstTraversal SimpleALGenerator]
           [java.io File]))


(def hypergraph (atom nil))
(def hypergraph-path (atom nil))


(defn delete-file-recursively
  "Delete java.io.File 'file'. If it's a directory, recursively delete all its contents."
  [file]
  (do
    (if (.isDirectory file)
      (doseq [child (.listFiles file)]
        (delete-file-recursively child)))
    (.delete file)))


(defn create-database
  [path]
  (do
    (reset! hypergraph-path path)
    (delete-file-recursively (File. path))
    (reset! hypergraph (HGEnvironment/get path))))


(defn close-database
  []
  (do
    (.close @hypergraph)
    (delete-file-recursively (File. @hypergraph-path))))


(defn add-node
  [data]
  (.add @hypergraph data))


(defn add-link
  ([target-list]
   (.add @hypergraph (HGPlainLink. (into-array HGHandle target-list))))
  ([data target-list]
   (.add @hypergraph (HGValueLink. data (into-array HGHandle target-list)))))


(defn remove-handle
  [handle]
  (.remove @hypergraph handle))


(defn get-hypergraph
  []
  @hypergraph)
