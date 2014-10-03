(hdm "configuration/hdm-uml-model.clj")

(input "configuration/ldap-input-model.clj" :default
       (extent "configuration/ldap-input-extent.clj"))

;(input "configuration/xml-input-model.clj" :default
;       (extent "configuration/xml-input-extent.clj"))

;(input "configuration/sql-input-model.clj" :default
;       (extent "configuration/sql-input-extent.clj"))


(output "configuration/xml-input-model.clj" ["resources/output.xml"]
        (extent "configuration/xml-output-extent.clj"))
