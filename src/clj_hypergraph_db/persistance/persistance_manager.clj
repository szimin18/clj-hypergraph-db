(ns clj_hypergraph_db.persistance.persistance_manager
  (:import [org.hypergraphdb HGEnvironment HGPlainLink HGValueLink HGHandle HGQuery$hg HyperGraph]
           [org.hypergraphdb.query HGQueryCondition]
           [org.hypergraphdb.algorithms HGBreadthFirstTraversal SimpleALGenerator]
           [java.io File]))


(def hypergraph (atom nil))


(defn delete-file-recursively
  "Delete java.io.File 'file'. If it's a directory, recursively delete all its contents."
  [file]
  (do
    (if (.isDirectory file)
      (doseq [child (.listFiles file)]
        (delete-file-recursively child)))
    (.delete file)))


(defn create-database
  "
  Creates a database or opens existing one from the folder specified by argument
  "
  [path]
  (do
    (delete-file-recursively (File. path))
    (reset! hypergraph (HGEnvironment/get path))))


(defn close-database
  "
  Closes the database
  "
  []
  (.close @hypergraph))


(defn add-node
  [data]
  (.add @hypergraph data))


(defn add-link
  ([target-list]
  (.add @hypergraph (HGPlainLink. (into-array HGHandle target-list))))
  ([data target-list]
  (.add @hypergraph (HGValueLink. data (into-array HGHandle target-list)))))


(defn get-hypergraph-instance
  []
  @hypergraph)


(defn peek-database
  []
  (let [traversal (HGBreadthFirstTraversal. (HGQuery$hg/assertAtom @hypergraph :metaclass) (SimpleALGenerator. @hypergraph))]
    (while (.hasNext traversal)
      (let [pair (.next traversal)
            link (.get @hypergraph (.getFirst pair))
            node (.get @hypergraph (.getSecond pair))]
        (try
          (print (.getValue link) "# ")
          (catch Exception e))
        (doseq [number (range (.getArity link))] (print (.get @hypergraph (.getTargetAt link number))))
        (println)
        ;(println node)
        ))))
