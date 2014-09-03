(ns clj_hypergraph_db.persistance.persistance_manager
  (:import [org.hypergraphdb HGEnvironment HGPlainLink HGValueLink HGHandle HGQuery$hg HyperGraph]
           [org.hypergraphdb.query And]
           [org.hypergraphdb.algorithms HGBreadthFirstTraversal SimpleALGenerator]
           [java.io File]))


(def hypergraph (atom nil))
(def hypergraph-path (atom nil))


(defn delete-file-recursively
  "Delete java.io.File 'file'. If it's a directory, recursively delete all its contents."
  [file]
  (do
    (when (.isDirectory file)
      (doseq [child (.listFiles file)]
        (delete-file-recursively child)))
    (.delete file)))


(defn hg-create
  [path]
  (do
    (reset! hypergraph-path path)
    (delete-file-recursively (File. path))
    (reset! hypergraph (HGEnvironment/get path))))


(defn hg-close
  []
  (do
    (.close @hypergraph)
    (delete-file-recursively (File. @hypergraph-path))))


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
   (->> (And. hg-condition1) (HGQuery$hg/findOne @hypergraph)))
  ([hg-condition1 hg-condition2]
   (->> (And. hg-condition1 hg-condition2) (HGQuery$hg/findOne @hypergraph)))
  ([hg-condition1 hg-condition2 hg-condition3]
   (->> (And. hg-condition1 hg-condition2 hg-condition3) (HGQuery$hg/findOne @hypergraph))))


(defn hg-find-all
  ([hg-condition1]
   (->> (And. hg-condition1) (HGQuery$hg/findAll @hypergraph)))
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


(defn hg-link-target-at
  [link index]
  (.getTargetAt link index))


(defn hg-link-arity
  [link]
  (.getArity link))


(defn hg-link-value
  [link]
  (.getValue link))
