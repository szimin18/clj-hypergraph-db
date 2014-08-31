(ns clj_hypergraph_db.xml_parser.xml_common_functions
  (:import [java.io File]))


(defn string-to-file-url
  [filename]
  (let [path (.getAbsolutePath (File. filename))
        path (if (= File/separatorChar \/)
               path
               (.replace path File/separatorChar \/))
        path (if (.startsWith path "/")
               path
               (str "/" path))]
    (str "file:" path)))


(defn shorten-path
  [path]
  (let [path (atom path)
        index (atom (.indexOf @path :..))]
    (while (>= @index 0)
      (swap! path #(concat (take (dec @index) %) (drop (inc @index) %)))
      (reset! index (.indexOf @path :..)))
    @path))


(defn eval-path
  ([path model]
   (eval-path
     (if (some #{:..} path)
       (shorten-path path)
       path)
     []
     model))
  ([path new-path model]
   (if (empty? path)
     new-path
     (let [first-of-path (first path)
           [new-child-key new-child-token] (first (for [[child-name child-token] (:children model)
                                                        :when (= first-of-path (:name child-token))]
                                                    [child-name child-token]))]
       (recur (rest path) (conj new-path :children new-child-key) new-child-token)))))
