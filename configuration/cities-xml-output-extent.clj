
(foreach :City
         (add-token [:Earth :City]
                    (mapping :Name [:Name :Name-text-node])))

(foreach :Person
         (associated-with [:Earth :City] :City :Citizen :Citizen
                          (add-token [:Citizens :Person]
                                     (mapping :Name [:Name :Name-text-node])
                                     (mapping :Surname [:Surname :Surname-text-node])
                                     (mapping :Age [:Age :Age-text-node]))))

(foreach :Car
         (associated-with [:Earth :City :Citizens :Person] :Owner :Owner :Car
                          (add-token [:Cars :Car]
                                     (mapping :ID [:ID :ID-text-node])
                                     (mapping :Accessory [:Accessories :Accessory :Accessory-text-node]))))
