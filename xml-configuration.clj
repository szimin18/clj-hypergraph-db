
(def-db :xml)

(def-root "People"
          (def-token "Person"
                     (def-class :Person
                                (def-field :Name :string
                                           (path "Name" :Name-data))
                                (def-field :Surname :string
                                           (path :Surname))
                                (primary-key :Name :Surname))
                     (def-attribute :Surname)
                     (def-token "Name"
                                (def-data :Name-data))))
