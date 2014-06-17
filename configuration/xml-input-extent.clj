(for [:glue:Domains :AdminDomain]
  (add-instance :AdminDomain
                (mapping [:Distributed :Distributed-text-node] :Distributed)
                (mapping [:Owner :Owner-text-node] :Owner)
                (mapping [:WWW :WWW-text-node] :WWW)
                (mapping [:Description :Description-text-node] :Description)
                (mapping [:ID :ID-text-node :ID] :ID)
                (mapping [:Name :Name-text-node] :Name)
                (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                (mapping [:CreationTime-attribute] :CreationTime)
                (mapping [:Validity-attribute] :Validity)))


(for [:glue:Domains :AdminDomain :Location]
  (add-instance :Location
                (mapping [:Distributed :Distributed-text-node] :Distributed)
                (mapping [:Owner :Owner-text-node] :Owner)
                (mapping [:WWW :WWW-text-node] :WWW)
                (mapping [:Description :Description-text-node] :Description)
                (mapping [:LocalID :LocalID-text-node :ID] :ID)
                (mapping [:Name :Name-text-node] :Name)
                (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                (mapping [:Latitude-attribute] :Latitude)
                (mapping [:Longitude-attribute] :Longitude)
                (mapping [:PostCode-attribute] :PostCode)
                (mapping [:Country-attribute] :Country)
                (mapping [:Place-attribute] :Place)
                (mapping [:Address-attribute] :Address)))

(for [:glue:Domains :AdminDomain :Contact]
  (add-instance :Contact
                (mapping [:Distributed :Distributed-text-node] :Distributed)
                (mapping [:Owner :Owner-text-node] :Owner)
                (mapping [:WWW :WWW-text-node] :WWW)
                (mapping [:Description :Description-text-node] :Description)
                (mapping [:LocalID :LocalID-text-node :ID] :ID)
                (mapping [:Name :Name-text-node] :Name)
                (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                (mapping [:Latitude-attribute] :Latitude)
                (mapping [:Longitude-attribute] :Longitude)
                (mapping [:PostCode-attribute] :PostCode)
                (mapping [:Country-attribute] :Country)
                (mapping [:CreationTime-attribute] :CreationTime)
                (mapping [:Validity-attribute] :Validity)))

(for [:glue:Domains :UserDomain]
  (add-instance :UserDomain (mapping [:Distributed :Distributed-text-node] :Distributed)
                (mapping [:Owner :Owner-text-node] :Owner)
                (mapping [:WWW :WWW-text-node] :WWW)
                (mapping [:Description :Description-text-node] :Description)
                (mapping [:ID :ID-text-node :ID] :ID)
                (mapping [:Name :Name-text-node] :Name)
                (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                (mapping [:Level-text-node] :Level)
                (mapping [:UserManager-text-node] :UserManager)
                (mapping [:Member-text-node] :Member)))

