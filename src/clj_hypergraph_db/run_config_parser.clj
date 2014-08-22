(ns clj_hypergraph_db.run_config_parser
  (require [clj_hypergraph_db.common_parser.common_config_parser :refer :all]))


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
