(ns clj_hypergraph_db.ldap_parser.ldap_to_uml_persistance_manager
  (:require [clj-ldap.client :as ldap]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]))


(defn load-input-data
  [{add-instances :add-instance
    add-associations :add-association} access-vector]
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
        (fn [object-class-values-map]
          (doseq [{class-name :name
                   mappings :mappings} add-instance-list
                  :let [class-instance (add-class-instance class-name)]] ;todo add attempt to find instance in database by pk
            (doseq [[path attribute-name] mappings
                    :let [value (get object-class-values-map path)]
                    :when value]
              (add-attribute-instance class-instance attribute-name value))))))
    (doseq [[object-class add-association-list] add-associations]
      (ldap/search!
        ldap-server
        "Mds-Vo-name=local,o=grid"
        {:filter (str "(objectclass=" object-class ")")
         :queue-size 100}
        (fn [object-class-values-map]
          (doseq [{association-name :name
                   [pk-path pk-role-name] :mapping-pk
                   mappings-fk :mappings-fk} add-association-list]
            (doseq [[path role-name] mappings-fk
                    :let [value (get object-class-values-map path)]
                    :when value
                    :let [association-instance-atom (atom (add-association-instance association-name))]]
              (swap! association-instance-atom add-role-instance-pk association-name pk-role-name (get object-class-values-map pk-path))
              (swap! association-instance-atom add-role-instance-pk association-name role-name value))))))))
