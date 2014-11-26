(database :xml
          (default-path "resources/cities-example-output.xml"))

(token "Earth" :Earth
       (token "City" :City
              (token "Name" :Name
                     (text :Name-text-node))
              (token "CarsCount" :CarsCount
                     (text :CarsCount-text-node))
              (token "AverageAge" :AverageAge
                     (text :AverageAge-text-node))
              (token "Citizens" :Citizens
                     (token "Person" :Person
                            (token "Name" :Name
                                   (text :Name-text-node))
                            (token "Surname" :Surname
                                   (text :Surname-text-node))
                            (token "Age" :Age
                                   (text :Age-text-node))
                            (token "Cars" :Cars
                                   (token "Car" :Car
                                          (token "ID" :ID
                                                 (text :ID-text-node))
                                          (token "Accessories" :Accessories
                                                 (token "FullList" :FullList
                                                        (text :FullList-text-node))
                                                 (token "Accessory" :Accessory
                                                        (text :Accessory-text-node)))))))))