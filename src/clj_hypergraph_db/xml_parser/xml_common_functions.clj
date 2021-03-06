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
  (let [index (.indexOf path :..)]
    (if (= -1 index)
      path
      (recur (concat (take (dec index) path) (drop (inc index) path))))))


(defn eval-path
  [model path]
  (loop [model model
         path (shorten-path path)
         new-path []]
    (if-let [first-of-path (first path)]
      (let [[new-child-key new-child-token] (some #(if (-> (second %) :name (= first-of-path)) %) (:children model))]
        (recur new-child-token (rest path) (conj new-path :children new-child-key)))
      new-path)))


(defn get-by-path
  [model path]
  (loop [model model
         path (shorten-path path)]
    (if-let [first-of-path (first path)]
      (recur (some #(if (-> (second %) :name (= first-of-path)) (second %)) (:children model)) (rest path))
      model)))
