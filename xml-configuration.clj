
(def-db :xml)

(root "People"
      (with-token "Person"
                  (def-type :Person
                               (primary-key
                                 (path :Surname)
                                 (path "Name" :Name-data)))
                  (with-attribute :Surname)
                  (with-token "Name"
                              (with-data :Name-data))))
