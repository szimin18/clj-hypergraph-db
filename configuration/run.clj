(hdm "configuration/hdm-uml-model.clj")

;
; inputs
;

;(input "configuration/xml-input-model.clj" :default
;       (extent "configuration/xml-input-extent.clj"))

(input "configuration/ldap-input-model.clj" :default
       (extent "configuration/ldap-input-extent.clj"))

;(input "configuration/sql-input-model.clj" :default
;       (extent "configuration/sql-input-extent.clj"))

;
; outputs
;

;(output "configuration/xml-input-model.clj" ["resources/output.xml"]
;        (extent "configuration/xml-output-extent.clj"))

(output "configuration/sql-input-model.clj" [{:database-name "glue_output"
                                              :user-name "postgres"
                                              :password "pass"}]
        (extent "configuration/sql-output-extent.clj"))

;(output "configuration/sql-input-model.clj" [{:database-name "glue_output"
;                                              :user-name "user"
;                                              :password "password"}]
;        (extent "configuration/sql-output-extent-adv.clj"))

;(output "configuration/ldap-input-model.clj" ["127.0.0.1"
;                                              "1389"
;                                              "cn=admin,Mds-Vo-name=local,o=grid"
;                                              "alamakota"]
;        (extent "configuration/ldap-output-extent.clj"))

;(output "configuration/sql-input-model.clj" [{:database-name "glue_output"
;                                              :user-name "user"
;                                              :password "password"}]
;        (extent "configuration/sql-output-extent-adv.clj"))
