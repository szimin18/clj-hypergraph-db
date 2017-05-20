(ns unification.tool.common.clojure.parser.clj.config.model.ldap.parser
  (:require [unification.tool.common.clojure.parser.clj.config.common.parser :refer :all]))


(defn default-host
  [name]
  (def-item :default-host
            :name name))


(defn default-port
  [name]
  (def-item :default-port
            :name name))


(defn default-dn
  [name]
  (def-item :default-dn
            :name name))


(defn default-password
  [name]
  (def-item :default-password
            :name name))


(defn object-class
  [name string-name & other]
  (def-item :object-class
            :name name
            :string-name string-name
            :other other))


(defn superclass
  [name]
  (def-item :superclass
            :name name))


(defn structural
  []
  (def-item :structural))


(defn must
  [name string-name]
  (def-item :must
            :name name
            :string-name string-name))


(defn may
  [name string-name]
  (def-item :may
            :name name
            :string-name string-name))


(defn evaluate
  [filename]
  (let [namespace (find-ns 'unification.tool.common.clojure.parser.clj.config.model.ldap.parser)]
    (vec (map
           #(binding [*ns* namespace] (eval %))
           (read-string (str "(" (slurp filename) ")"))))))
