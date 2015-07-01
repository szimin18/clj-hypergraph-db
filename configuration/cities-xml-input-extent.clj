
(for-each [:Earth :City]
         (add-instance :City
                       (mapping [:name-attribute] :Name)
                       (mapping [:Name :Name-text-node] :Name)))

(for-each [:Earth :City :Person]
         (add-instance :Person
                       (mapping [:Name :Name-text-node] :Name)
                       (mapping [:Surname :Surname-text-node] :Surname)
                       (mapping [:Age :Age-text-node] :Age))
         (add-association :Citizen
                          (mapping [] :Citizen)
                          (mapping [:..] :City)))

(for-each [:Earth :City :Person :Cars :Car]
         (add-instance :Car
                       (mapping [:ID-attribute] :ID)
                       (mapping [:Accessories :Accessories-text-node] :Accessories)
                       (mapping [:Accessory :Accessory-text-node] :Accessory))
         (add-association :Owner
                          (mapping [] :Car)
                          (mapping [:.. :..] :Owner)))

(for-each [:Earth :City :Person :Car]
         (add-instance :Car
                       (mapping [:ID :ID-text-node] :ID)))

