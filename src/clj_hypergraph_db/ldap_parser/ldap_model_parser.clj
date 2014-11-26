(ns clj_hypergraph_db.ldap_parser.ldap_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]))


(defn create-object-class
  [class-config]
  (let [name (:name class-config)
        string-name (:string-name class-config)
        superclass (:name (find-first-item-by-type (:other class-config) :superclass))
        structural (find-first-item-by-type (:other class-config) :structural)
        must (find-all-items-by-type (:other class-config) :must)
        may (find-all-items-by-type (:other class-config) :may)
        attributes (into {} (concat
                              (for [attribute-config must]
                                [(:name attribute-config) {:name (:string-name attribute-config)
                                                           :obligatory true}])
                              (for [attribute-config may]
                                [(:name attribute-config) {:name (:string-name attribute-config)
                                                           :obligatory false}])))]
    [name {:name string-name
           :superclass superclass
           :structural (if structural true false)
           :attributes attributes}]))


(defn create-model
  [configuration-list]
  (let [metadata (:metadata (find-first-item-by-type configuration-list :database))
        default-host (:name (find-first-item-by-type metadata :default-host))
        default-port (:name (find-first-item-by-type metadata :default-port))
        default-dn (:name (find-first-item-by-type metadata :default-dn))
        default-password (:name (find-first-item-by-type metadata :default-password))
        classes (into {} (map create-object-class (find-all-items-by-type configuration-list :object-class)))]
    {:default-access [default-host default-port default-dn default-password]
     :classes classes}))
