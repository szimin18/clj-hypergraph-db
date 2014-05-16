(ns clj_hypergraph_db.persistance.persistance_manager
  (:import [org.hypergraphdb HGEnvironment HGPlainLink HGValueLink HGHandle HGQuery$hg]
           [org.hypergraphdb.algorithms HGBreadthFirstTraversal SimpleALGenerator]
           [java.io File]))


(def database (atom nil))


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
  (.add @database (HGPlainLink. (into-array HGHandle target-node-handles-list))))


(defn add-value-link
  [data target-node-handles-list]
  (.add @database (HGValueLink. data (into-array HGHandle target-node-handles-list))))


(defn peek-database
  []
  (let [traversal (HGBreadthFirstTraversal. (HGQuery$hg/assertAtom @database :metaclass) (SimpleALGenerator. @database))]
    (while (.hasNext traversal)
      (let [pair (.next traversal)
            link-handle (.getFirst pair)
            node-handle (.getSecond pair)
            link (.get @database link-handle)
            node (.get @database node-handle)]
        (try
          (print (.getValue link) "# ")
          (catch Exception e))
        (doseq [number (range (.getArity link))] (print (.get @database (.getTargetAt link number)) " "))
        (println)
        ;(println node)
        ))))
