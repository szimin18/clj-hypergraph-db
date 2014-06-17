(for [:glue:Domains :AdminDomain]
  (add-instance :AdminDomain
                (mapping [:Distributed :Distributed-text-node] :Distributed)
                (mapping [:Owner :Owner-text-node] :Owner)
                (mapping [:WWW :WWW-text-node] :WWW)
                (mapping [:Description :Description-text-node] :Description)
                (mapping [:Validity-attribute] :Validity)
                (mapping [:ID :ID-text-node :ID] :ID)
                (mapping [:CreationTime-attribute] :CreationTime)
                (mapping [:Name :Name-text-node] :Name)
                (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)))


(for [:glue:Domains :AdminDomain :Location]
  (add-instance :Location
                (mapping [:LocalID :LocalID-text-node] :ID)
                (mapping [:Name :Name-text-node] :Name)
                (mapping [] )))



;(token :Location
;       (attribute :CreationTime-attribute)
;       (attribute :Validity-attribute)
;       (token :LocalID
;              (text :LocalID-text-node))
;       (token :Name
;              (text :Name-text-node))
;       (token :Address
;              (text :Address-text-node))
;       (token :Place
;              (text :Place-text-node))
;       (token :Country
;              (text :Country-text-node))
;       (token :PostCode
;              (text :PostCode-text-node))
;       (token :Latitude
;              (text :Latitude-text-node))
;       (token :Longitude
;              (text :Longitude-text-node))
;       (token :Extensions
;              (token :Extension
;                     (attribute :Key-attribute)
;                     (text :Extension-text-node))))








;(class :Extension
;       (attributes
;         :LocalID
;         :Key
;         :Value))
;
;(class :Entity
;       (attributes
;         :ID
;         :Validity
;         :CreationTime
;         :OtherInfo
;         :Name))
;
;(class :Domain
;       (attributes
;         :WWW
;         :Description)
;       (extends :Entity))
;
;(class :Share
;       (attributes
;         :Description)
;       (extends :Entity))
;
;(class :Service
;       (attributes
;         :StatusInfo
;         :QualityLevel
;         :Capability
;         :Type
;         :Complexity)
;       (extends :Entity))
;
;(class :Location
;       (attributes
;         :Latitude
;         :Longitude
;         :PostCode
;         :Country
;         :Place
;         :Address)
;       (extends :Entity))
;
;(class :AdminDomain
;       (attributes
;         :Distributed
;         :Owner)
;       (extends :Domain))
;
;(class :UserDomain
;       (attributes
;         :Member
;         :UserManager
;         :Level)
;       (extends :Domain))
;
;(class :Contact
;       (attributes
;         :Type
;         :Detail)
;       (extends :Entity))
;
;(class :Manager
;       (attributes
;         :ProductVersion
;         :ProductName)
;       (extends :Entity))
;
;(class :Resource
;       (extends :Entity))
;
;(class :Endpoint
;       (attributes
;         :StartTime
;         :HealthStateInfo
;         :SupportedProfile
;         :InterfaceVersion
;         :InterfaceExtension
;         :HealthState
;         :URL
;         :DowntimeAnnounce
;         :QualityLevel
;         :IssuerCA
;         :DowntimeStart
;         :DowntimeInfo
;         :WSDL
;         :ServingState
;         :Implementor
;         :Semantics
;         :Technology
;         :Capability
;         :ImplementationName
;         :ImplementationVersion
;         :DowntimeEnd
;         :TrustedCA)
;       (extends :Entity))
;
;(class :Activity
;       (extends :Entity))
;
;(class :Policy
;       (attributes
;         :Rule
;         :Scheme)
;       (extends :Entity))
;
;(class :AccessPolicy
;       (extends :Policy))
;
;(class :MappingPolicy
;       (extends :Policy))