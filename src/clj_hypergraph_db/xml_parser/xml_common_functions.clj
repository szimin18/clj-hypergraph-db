(ns clj_hypergraph_db.xml_parser.xml_common_functions
  (:import [java.io File]))


(defn string-to-file-url
  [filename]
<<<<<<< HEAD
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
=======
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
>>>>>>> origin/modeling-attempts
