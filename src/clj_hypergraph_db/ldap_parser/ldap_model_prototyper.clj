(ns clj_hypergraph_db.ldap_parser.ldap_model_prototyper
  (:import [java.io File])
  (:use [clojure.string :only [split]])
  (:require [clj-ldap.client :as ldap]
            [clj_hypergraph_db.xml_parser.xml_common_functions :refer :all]))


(defn create-attribute-type
  [current-attribute-map attribute-tokens-list]
  (let [[first-token second-token & rest-tokens] attribute-tokens-list]
    (case first-token
      nil current-attribute-map
      "NAME" (recur (assoc current-attribute-map :name (re-find #"\w[\s\w\.-]*\w|\w" second-token)) rest-tokens)
      "DESC" (recur (assoc current-attribute-map :description (re-find #"\w[\s\w\.-]*\w|\w" second-token)) rest-tokens)
      "EQUALITY" (recur (assoc current-attribute-map :equality (re-find #"\w[\s\w\.-]*\w|\w" second-token)) rest-tokens)
      "SUBSTR" (recur (assoc current-attribute-map :substring (re-find #"\w[\s\w\.-]*\w|\w" second-token)) rest-tokens)
      "SYNTAX" (recur (assoc current-attribute-map :syntax (re-find #"\w[\s\w\.-]*\w|\w" second-token)) rest-tokens)
      (recur current-attribute-map (cons second-token rest-tokens)))))


(defn add-attribute-type
  [attributes-map attribute-string]
  (let [attribute-type (create-attribute-type {} (re-seq #"[\w.-]+|'[\w\s.-]+'" attribute-string))]
    (assoc attributes-map (:name attribute-type) attribute-type)))


(defn create-object-class
  [current-classes-map class-tokens-list]
  (let [[first-token second-token & rest-tokens] class-tokens-list]
    (case first-token
      nil current-classes-map
      "NAME" (recur (assoc current-classes-map :name (re-find #"\w[\s\w\.-]*\w|\w" second-token)) rest-tokens)
      "DESC" (recur (assoc current-classes-map :description (re-find #"\w[\s\w\.-]*\w|\w" second-token)) rest-tokens)
      "SUP" (recur (assoc current-classes-map :superclass (re-find #"\w[\s\w\.-]*\w|\w" second-token)) rest-tokens)
      "STRUCTURAL" (recur (assoc current-classes-map :structural true) (cons second-token rest-tokens))
      "AUXILIARY" (recur current-classes-map (cons second-token rest-tokens))
      "MAY" (recur (assoc current-classes-map :may (re-seq #"\w[\w\.-]*\w|\w" second-token)) rest-tokens)
      "MUST" (recur (assoc current-classes-map :must (re-seq #"\w[\w\.-]*\w|\w" second-token)) rest-tokens)
      (recur current-classes-map (cons second-token rest-tokens)))))


(defn add-object-class
  [classes-map class-string]
  (let [object-class (create-object-class {} (re-seq #"[\w\.-]+|'[\w\s\.-]+'|\([\w\s\$\.-]+\)" class-string))]
    (assoc classes-map (:name object-class) object-class)))


(defn object-class-to-string
  [object-class]
  (let [name (:name object-class)
        superclass (:superclass object-class)
        structural (:structural object-class)
        may (:may object-class)
        must (:must object-class)
        string-builder (StringBuilder.)]
    (.append string-builder (str "\n\n(object-class " (keyword name) " \"" name \"))
    (when superclass
      (.append string-builder (str "\n              (superclass " (keyword superclass) ")")))
    (when structural
      (.append string-builder (str "\n              (structural)")))
    (doseq [attribute must]
      (when-let [attribute-name (:name attribute)]
        (.append string-builder (str "\n              (must " (keyword attribute-name) " \"" attribute-name "\")"))))
    (doseq [attribute may]
      (when-let [attribute-name (:name attribute)]
        (.append string-builder (str "\n              (may " (keyword attribute-name) " \"" attribute-name "\")"))))
    (.append string-builder ")")
    (.toString string-builder)))


(defn create-prototype-configuration
  [configuration-file-path access-vector]
  (let [[host port dn password] access-vector
        ldap-server (ldap/connect {:host (str host ":" port)
                                   :bind-dn dn
                                   :password password})
        model-config (->> {:scope :base
                           :filter "(objectclass=subschema)"
                           :attributes [:objectclasses :attributetypes]}
                          (ldap/search ldap-server "cn=subschema")
                          first)
        attribute-types (reduce add-attribute-type {} (:attributeTypes model-config))
        object-classes (reduce add-object-class {} (:objectClasses model-config))
        object-classes (map (fn [class-config]
                              (update-in
                                (update-in
                                  class-config
                                  [:may]
                                  (fn [attributes-list]
                                    (map #(get attribute-types %) attributes-list)))
                                [:must]
                                (fn [attributes-list]
                                  (map #(get attribute-types %) attributes-list))))
                            (vals object-classes))
        metadata-string (str "(database :ldap\n"
                             "          (default-host \"" host "\")\n"
                             "          (default-port \"" port "\")\n"
                             "          (default-dn \"" dn "\")\n"
                             "          (default-password \"" password "\"))")
        classes-string (apply str (map object-class-to-string object-classes))]
    (try
      (.remove (File. configuration-file-path))
      (catch Exception e))
    (spit configuration-file-path (str metadata-string classes-string))))

;result (ldap/search ldap-server "Mds-Vo-name=local,o=grid" {:filter "(objectclass=subschema)"
;                                                            :attributes [:objectcasses :attributetypes]})