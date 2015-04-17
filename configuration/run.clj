(hdm "configuration/hdm-uml-model.clj")

;
; inputs
;

(input "configuration/xml-input-model.clj" :default
       (extent "configuration/xml-input-extent.clj"))

;(input "configuration/ldap-input-model.clj" :default
;       (extent "configuration/ldap-input-extent.clj"))

;(input "configuration/sql-input-model.clj" :default
;       (extent "configuration/sql-input-extent.clj"))

;
; outputs
;

;(output "configuration/xml-input-model.clj" ["resources/output.xml"]
;        (extent "configuration/xml-output-extent.clj"))

;(output "configuration/sql-input-model.clj" [{:database-name "glue_output"
;                                              :user-name "user"
;                                              :password "password"}]
;        (extent "configuration/sql-output-extent.clj"))

;(output "configuration/sql-input-model.clj" [{:database-name "glue_output"
;                                              :user-name "user"
;                                              :password "password"}]
;        (extent "configuration/sql-output-extent-adv.clj"))
