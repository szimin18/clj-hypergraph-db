(ns clj_hypergraph_db.parser
  (:use [clojure.tools.logging :only (info)])
  (:use [clj_hypergraph_db.model]))



(defn parse
  "
  Parses a list of definitions of the form (def-type ...) by evaluating each of them separately.
  Returns a list of values returned by each evaluated form.
  "
  [definitions]
  (do
    (info "starts parsing")

    ;; transform the input list by evaluating each form in the list
    ;; in clj_hypergraph_db.model namespace
    (map #(binding [*ns* (find-ns 'clj_hypergraph_db.model)] (eval %)) definitions)))