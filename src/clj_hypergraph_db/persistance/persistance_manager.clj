(ns clj_hypergraph_db.persistance.persistance_manager
  (:import [org.hypergraphdb HGEnvironment HGPlainLink HGValueLink HGHandle HGQuery$hg HyperGraph]
           [org.hypergraphdb.query HGQueryCondition]
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


(defn add-link
  ([target-node-handles-list]
  (.add @database (HGPlainLink. (into-array HGHandle target-node-handles-list))))
  ([data target-node-handles-list]
  (.add @database (HGValueLink. data (into-array HGHandle target-node-handles-list)))))


(defn get-all-objects-of-class
  [class-name]
  (let [class-list (atom [])]
    (doseq [handle (.findAll @database (HGQuery$hg/eq class-name))]
      (let [traversal (HGBreadthFirstTraversal. handle (SimpleALGenerator. @database) 1)
            field-map (atom {})]
        (while (.hasNext traversal)
          (let [pair (.next traversal)
                link (.get @database (.getFirst pair))
                node (.get @database (.getSecond pair))]
            (try
              (swap! field-map assoc (.getValue link) node)
              (catch Exception e))))
        (if (nil? (:class @field-map))
          (swap! class-list conj @field-map))))
    @class-list))


(defn peek-database
  []
  (let [traversal (HGBreadthFirstTraversal. (HGQuery$hg/assertAtom @database :metaclass) (SimpleALGenerator. @database))]
    (while (.hasNext traversal)
      (let [pair (.next traversal)
            link (.get @database (.getFirst pair))
            node (.get @database (.getSecond pair))]
        (try
          (print (.getValue link) "# ")
          (catch Exception e))
        (doseq [number (range (.getArity link))] (print (.get @database (.getTargetAt link number)) " "))
        (println)
        ;(println node)
        ))))
