(ns clj_hypergraph_db.ldap_parser.ldap_to_uml_persistance_manager
  (:require [clj-ldap.client :as ldap]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]))


(defn load-input-data
  [{add-instances :add-instance} access-vector]
  (let [[host port dn password] access-vector
        ldap-server (ldap/connect {:host (str host ":" port)
                                   :bind-dn dn
                                   :password password})]
    (doseq [[object-class add-instance-list] add-instances]
      (ldap/search!
        ldap-server
        "Mds-Vo-name=local,o=grid"
        {:filter (str "(objectclass=" object-class ")")
         :queue-size 100}
        (fn [x]
          (doseq [{name :name mappings :mappings} add-instance-list
                  :let [class-instance (add-class-instance name)]]
            (doseq [[path attribute-name] mappings
                    :let [value (get x path)]
                    :when value]
              (add-attribute-instance class-instance attribute-name value))))))))
