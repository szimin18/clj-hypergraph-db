(ns clj_hypergraph_db.xml_parser.xml_common_functions
  (:import [java.io File]))


(defn string-to-file-url
  [filename]
  (let [path (.getAbsolutePath (File. filename))]
    (let [path (if (= File/separatorChar \/)
                 path
                 (.replace path File/separatorChar \/))]
      (let [path (if (.startsWith path "/")
                   path
                   (str "/" path))]
        (str "file:" path)))))