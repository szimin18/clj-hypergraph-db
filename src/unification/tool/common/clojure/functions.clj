(ns unification.tool.common.clojure.functions)


(defn find-file-type
  [file-name]
  (let [file (read-string (str "(" (slurp file-name) ")"))]
    (some
      #(if (#{"database" "model-type"} (name (first %))) (second %))
      file)))
