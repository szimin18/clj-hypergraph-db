(ns unification.tool.common.clojure.parser.clj.config.extent.output.uml.xml.parser
  (:require [unification.tool.common.clojure.parser.clj.config.common.parser :refer :all]))


(defn for-each
  [name & body]
  (def-item :for-each
            :name name
            :body body))


(defn add-token
  [path & body]
  (def-item :add-token
            :path path
            :body body))


(defn associated-with
  [with with-role association-name target-role & body]
  (def-item :associated-with
            :with with
            :with-role with-role
            :association-name association-name
            :target-role target-role
            :body body))


(defn associated-with-for
  [with with-role association-name target-role & body]
  (def-item :associated-with-for
            :with with
            :with-role with-role
            :association-name association-name
            :target-role target-role
            :body body))


(defn mapping
  [from to]
  (def-item :mapping
            :from from
            :to to))


(defn mapping-each
  [from to]
  (def-item :mapping-each
            :from from
            :to to))


(defn bind
  [from to]
  (def-item :bind
            :from from
            :to to))


(defn call
  [fn-name & args]
  (def-item :call
            :fn-name fn-name
            :args args))


(defn aggregate
  [arg]
  (def-item :aggregate
            :arg arg))


(defn- def-function
  [name body]
  (def-item :function
            :name name
            :body body))


(defn evaluate
  [filename]
  (let [namespace (find-ns 'unification.tool.common.clojure.parser.clj.config.extent.output.uml.xml.parser)]
    (vec (map
           #(binding [*ns* namespace] (eval %))
           (read-string (str "(" (slurp filename) ")"))))))
