(ns clj_hypergraph_db.ldap_parser.ldap_model_prototyper
  (:import [java.io File])
  (:require [clj-ldap.client :as ldap]))


(defn create-attribute-type
  [attribute-string]
  (loop [current-attribute-map {}
         [first-token second-token & rest-tokens] (re-seq #"[\w.-]+|'[\w\s.-]+'" attribute-string)]
    (case first-token
      nil [(:name current-attribute-map) current-attribute-map]
      "NAME" (recur (assoc current-attribute-map :name (re-find #"\w[\w\.-]*\w|\w" second-token)) rest-tokens)
      "DESC" (recur (assoc current-attribute-map :description (re-find #"\w[\s\w\.-]*\w|\w" second-token)) rest-tokens)
      "EQUALITY" (recur (assoc current-attribute-map :equality (re-find #"\w[\s\w\.-]*\w|\w" second-token)) rest-tokens)
      "SUBSTR" (recur (assoc current-attribute-map :substring (re-find #"\w[\s\w\.-]*\w|\w" second-token)) rest-tokens)
      "SYNTAX" (recur (assoc current-attribute-map :syntax (re-find #"\w[\s\w\.-]*\w|\w" second-token)) rest-tokens)
      (recur current-attribute-map (cons second-token rest-tokens)))))


(defn create-object-class
  [object-class-string]
  (loop [current-classes-map {}
         [first-token second-token & rest-of-tokens] (re-seq #"[\w\.-]+|'[\w\s\.-]+'|\([\w\s\$\.-]+\)" object-class-string)]
    (case first-token
      nil current-classes-map
      "NAME" (recur (assoc current-classes-map :name (re-find #"\w[\w\.-]*\w|\w" second-token)) rest-of-tokens)
      "DESC" (recur (assoc current-classes-map :description (re-find #"\w[\s\w\.-]*\w|\w" second-token)) rest-of-tokens)
      "SUP" (recur (assoc current-classes-map :superclass (re-find #"\w[\w\.-]*\w|\w" second-token)) rest-of-tokens)
      "STRUCTURAL" (recur (assoc current-classes-map :structural true) (cons second-token rest-of-tokens))
      "AUXILIARY" (recur current-classes-map (cons second-token rest-of-tokens))
      "MAY" (recur (assoc current-classes-map :may (re-seq #"\w[\w\.-]*\w|\w" second-token)) rest-of-tokens)
      "MUST" (recur (assoc current-classes-map :must (re-seq #"\w[\w\.-]*\w|\w" second-token)) rest-of-tokens)
      (recur current-classes-map (cons second-token rest-of-tokens)))))


(defn create-prototype-configuration
  [configuration-file-path access-vector]
  (let [[host port dn password] access-vector
        string-builder (doto (StringBuilder.)
                         (.append (str "(database :ldap\n"
                                       "          (default-host \"" host "\")\n"
                                       "          (default-port \"" port "\")\n"
                                       "          (default-dn \"" dn "\")\n"
                                       "          (default-password \"" password "\"))")))
        ldap-server (ldap/connect {:host (str host ":" port)
                                   :bind-dn dn
                                   :password password})
        {attribute-types-list :attributeTypes
         object-classes-list :objectClasses} (->> {:scope :base
                                                   :filter "(objectclass=subschema)"
                                                   :attributes [:objectclasses :attributetypes]}
                                                  (ldap/search ldap-server "cn=subschema")
                                                  first)
        attribute-types (into {} (map create-attribute-type attribute-types-list))
        update-attributes-fn (fn [attributes-list]
                               (map #(get attribute-types %) attributes-list))
        object-classes (map #(update-in
                              (update-in
                                (create-object-class %)
                                [:may]
                                update-attributes-fn)
                              [:must]
                              update-attributes-fn)
                            object-classes-list)]
    (doseq [{name :name
             superclass :superclass
             structural :structural
             may :may
             must :must} object-classes]
      (.append string-builder (str "\n\n(object-class " (keyword name) " \"" name \"))
      (when superclass
        (.append string-builder (str "\n              (superclass " (keyword superclass) ")")))
      (when structural
        (.append string-builder (str "\n              (structural)")))
      (doseq [{name :name} must
              :when name]
        (.append string-builder (str "\n              (must " (keyword name) " \"" name "\")")))
      (doseq [{name :name} may
              :when name]
        (.append string-builder (str "\n              (may " (keyword name) " \"" name "\")")))
      (.append string-builder ")")
      (.toString string-builder))
    (try
      (.remove (File. configuration-file-path))
      (catch Exception e))
    (spit configuration-file-path (.toString string-builder))))
