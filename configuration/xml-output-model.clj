
(def-db :xml)

(def-token "People"
           (def-token "Person"
                      (def-attribute :Name)
                      (def-token "Surname"
                                 (def-data :Surname-data))))
