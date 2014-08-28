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


(defn swap-model-forward
  ([model-atom path]
   (swap-model-forward model-atom path []))
  ([model-atom path new-path]
   (if (empty? path)
     new-path
     (let [first-of-path (first path)
           model (last @model-atom)]
       (if (= first-of-path :..)
         (do
           (swap! model-atom drop-last)
           (let [[last-stack-token last-stack-key] (last @model-atom)]
             (swap! model-atom drop-last)
             (swap! model-atom conj (assoc model :children (assoc (:children model) last-stack-key last-stack-token)))
             (swap-model-forward model-atom (rest path) (conj new-path [last-stack-key]))))
         (let [[new-child-key new-child-token] (first (for [[child-name child-token] (:children model)
                                                            :when (= first-of-path (:name child-token))]
                                                        [child-name child-token]))
               new-children-of-model (dissoc (:children model) new-child-key)]
           (swap! model-atom drop-last)
           (swap! model-atom conj [(assoc model :children new-children-of-model) new-child-key])
           (swap! model-atom conj new-child-token)
           (swap-model-forward model-atom (rest path) (conj new-path new-child-key))))))))


(defn swap-model-backward
  [model-atom path]
  (if (empty? path)
    :ok
    (let [first-of-path (first path)
          model (last @model-atom)]
      (if (vector? first-of-path)
        (let [first-of-path (first first-of-path)
              new-model (get (:children model) first-of-path)
              new-model-without-child (assoc model :children (dissoc (:children model) first-of-path))]
          (swap! model-atom drop-last)
          (swap! model-atom conj [new-model-without-child first-of-path])
          (swap! model-atom conj new-model))
        (list)))))


(defn eval-path
  ([path model]
   (first (eval-path path [] model)))
  ([path new-path model]
   (if (empty? path)
     [new-path model]
     (let [first-of-path (first path)
           [new-child-key new-child-token] (first (for [[child-name child-token] (:children model)
                                                        :when (= first-of-path (:name child-token))]
                                                    [child-name child-token]))]
       (eval-path (rest path) (conj new-path new-child-key) new-child-token)))))


(defn eval-leaf-path
  [path model]
  (let [[new-path model] (eval-path (drop-last path) [] model)
        last-of-path (last path)
        new-attribute-name (first (for [[attribute-name attribute-token] (:attributes model)
                                        :when (= last-of-path (:name attribute-token))]
                                    attribute-name))]
    [new-path (if new-attribute-name new-attribute-name last-of-path) (if new-attribute-name :attribute :text)]))
