(ns unification.tool.common.clojure.parser.clj.config.run.model.parser
  (require [unification.tool.common.clojure.parser.clj.config.common.model.parser :refer :all]))


(defn hdm
  [filename]
  (def-item :hdm
            :filename filename))


(defn input
  [filename access & extents]
  (def-item :input
            :filename filename
            :access access
            :extents extents))


(defn output
  [filename access & extents]
  (def-item :output
            :filename filename
            :access access
            :extents extents))


(defn extent
  [filename]
  (def-item :extent
            :filename filename))


(defn evaluate
  [filename]
  (let [namespace (find-ns 'unification.tool.common.clojure.parser.clj.config.run.model.parser)]
    (vec (map
           #(binding [*ns* namespace] (eval %))
           (read-string (str "(" (slurp filename) ")"))))))
