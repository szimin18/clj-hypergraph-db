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


(defn eval-path
  ([path input-model]
   (eval-path path [] input-model))
  ([path new-path model]
   (if (zero? (count path))
     new-path
     (let [children-of-input-model (:children model)
           first-of-path (first path)
           new-child-key (first (filter
                                  #(= first-of-path (:name (children-of-input-model %)))
                                  (keys children-of-input-model)))]
       (eval-path
         (rest path)
         (conj new-path new-child-key)
         (children-of-input-model new-child-key))))))


(defn eval-leaf-path
  ([path input-model]
   (eval-leaf-path path [] input-model))
  ([path new-path model]
   (if (<= (count path) 1)
     (let [token-attributes (:attributes model)
           attribute-name (first (filter
                                   #(= (last path) (:name (token-attributes %)))
                                   (keys token-attributes)))]
       [new-path (if attribute-name attribute-name (last path)) (if attribute-name :attribute :text)])
     (let [children-of-input-model (:children model)
           first-of-path (first path)
           new-child-key (first (filter
                                  #(= first-of-path (:name (children-of-input-model %)))
                                  (keys children-of-input-model)))]
       (eval-leaf-path
         (rest path)
         (conj new-path new-child-key)
         (children-of-input-model new-child-key))))))
