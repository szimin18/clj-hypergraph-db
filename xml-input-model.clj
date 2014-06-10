
(def-db :xml)

(def-token "People"
           (def-token "Person"
                      (def-attribute :Surname)
                      (def-token "Name"
                                 (def-data :Name-data))))
