(database :xml
          (default-path "resources/cities-example.xml"))

(token "Earth" :Earth
       (token "City" :City
              (attribute "name" :name-attribute)
              (token "Person" :Person
                     (token "Name" :Name
                            (text :Name-text-node))
                     (token "Surname" :Surname
                            (text :Surname-text-node))
                     (token "Age" :Age
                            (text :Age-text-node))
                     (token "Cars" :Cars
                            (token "Car" :Car
                                   (attribute "ID" :ID-attribute)
                                   (token "Accessories" :Accessories
                                          (text :Accessories-text-node))
                                   (token "Accessory" :Accessory
                                          (text :Accessory-text-node))))
                     (token "Car" :Car
                            (token "ID" :ID
                                   (text :ID-text-node))))
              (token "Name" :Name
                     (text :Name-text-node))))