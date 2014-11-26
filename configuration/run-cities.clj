(hdm "configuration/cities-hdm-uml-model.clj")

;
; inputs
;

(input "configuration/cities-xml-input-model.clj" :default
       (extent "configuration/cities-xml-input-extent.clj"))

;
; outputs
;

(output "configuration/cities-xml-output-model.clj" :default
        (extent "configuration/cities-xml-output-extent.clj"))
