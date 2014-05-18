
(def-db :xml)

(def-token "People"
           (def-token "Person"
                      (def-class :Person
                                 (def-field :Surname :string
                                            (path "Surname" :Surname-data))
                                 (def-field :Name :string
                                            (path :Name))
                                 (primary-key :Name :Surname))
                      (def-attribute :Name)
                      (def-token "Surname"
                                 (def-data :Surname-data))))
