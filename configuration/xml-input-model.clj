(token :glue:Domains
       (attribute :xmlns:glue-attribute)
       (attribute :xmlns:xsi-attribute)
       (attribute :xsi:schemaLocation-attribute)
       (token :AdminDomain
              (attribute :CreationTime-attribute)
              (attribute :Validity-attribute)
              (attribute :BaseType-attribute)
              (token :ID
                     (text :ID-text-node))
              (token :Name
                     (text :Name-text-node))
              (token :Description
                     (text :Description-text-node))
              (token :WWW
                     (text :WWW-text-node))
              (token :OtherInfo
                     (text :OtherInfo-text-node))
              (token :Distributed
                     (text :Distributed-text-node))
              (token :Owner
                     (text :Owner-text-node))
              (token :Extensions
                     (token :Extension
                            (attribute :Key-attribute)
                            (text :Extension-text-node)))
              (token :Location
                     (attribute :CreationTime-attribute)
                     (attribute :Validity-attribute)
                     (token :LocalID
                            (text :LocalID-text-node))
                     (token :Name
                            (text :Name-text-node))
                     (token :Address
                            (text :Address-text-node))
                     (token :Place
                            (text :Place-text-node))
                     (token :Country
                            (text :Country-text-node))
                     (token :PostCode
                            (text :PostCode-text-node))
                     (token :Latitude
                            (text :Latitude-text-node))
                     (token :Longitude
                            (text :Longitude-text-node))
                     (token :Extensions
                            (token :Extension
                                   (attribute :Key-attribute)
                                   (text :Extension-text-node))))
              (token :Contact
                     (attribute :CreationTime-attribute)
                     (attribute :Validity-attribute)
                     (token :LocalID
                            (text :LocalID-text-node))
                     (token :URL
                            (text :URL-text-node))
                     (token :Type
                            (text:Type-text-node))
                     (token :OtherInfo
                            (text :OtherInfo-text-node))
                     (token :Extensions
                            (token :Extension
                                   (attribute :Key-attribute)
                                   (text :Extension-text-node))))
              (token :Services
                     (token :Service
                            (attribute :BaseType-attribute)
                            (token :ID
                                   (text :ID-text-node))
                            (token :Name
                                   (text :Name-text-node))
                            (token :Capability
                                   (text :Capability-text-node))
                            (token :Type
                                   (text :Type-text-node))
                            (token :QualityLevel
                                   (text :QualityLevel-text-node))
                            (token :StatusPage)
                            (token :Complexity
                                   (text :Complexity-text-node))
                            (token :Extensions
                                   (token :Extension
                                          (attribute :Key-attribute)
                                          (text :Extension-text-node)))
                            (token :Location
                                   (token :LocalID
                                          (text :LocalID-text-node))
                                   (token :Name
                                          (text :Name-text-node))
                                   (token :Address
                                          (text :Address-text-node))
                                   (token :Place
                                          (text :Place-text-node))
                                   (token :Country
                                          (text :Country-text-node))
                                   (token :PostCode
                                          (text :PostCode-text-node))
                                   (token :Latitude
                                          (text :Latitude-text-node))
                                   (token :Longitude
                                          (text :Longitude-text-node))
                                   (token :Extensions
                                          (token :Extension
                                                 (attribute :Key-attribute)
                                                 (text :Extension-text-node))))
                            (token :Contact
                                   (token :LocalID
                                          (text :LocalID-text-node))
                                   (token :URL
                                          (text :URL-text-node))
                                   (token :Type
                                          (text :Type-text-node))
                                   (token :OtherInfo
                                          (text :OtherInfo-text-node))
                                   (token :Extensions
                                          (token :Extension
                                                 (attribute :Key-attribute)
                                                 (text :Extension-text-node))))
                            (token :Endpoint
                                   (attribute :BaseType-attribute)
                                   (token :ID
                                          (text :ID-text-node))
                                   (token :Name
                                          (text :Name-text-node))
                                   (token :URL
                                          (text :URL-text-node))
                                   (token :Capability
                                          (text :Capability-text-node))
                                   (token :Technology
                                          (text :Technology-text-node))
                                   (token :Interface
                                          (text :Interface-text-node))
                                   (token :InterfaceExtension
                                          (text :InterfaceExtension-text-node))
                                   (token :WSDL
                                          (text :WSDL-text-node))
                                   (token :SupportedProfile
                                          (text :SupportedProfile-text-node))
                                   (token :Semantics
                                          (text :Semantics-text-node))
                                   (token :Implementor
                                          (text :Implementor-text-node))
                                   (token :ImplementationName
                                          (text :ImplementationName-text-node))
                                   (token :ImplementationVersion
                                          (text :ImplementationVersion-text-node))
                                   (token :QualityLevel
                                          (text :QualityLevel-text-node))
                                   (token :HealthState
                                          (text :HealthState-text-node))
                                   (token :HealthStateInfo
                                          (text :HealthStateInfo-text-node))
                                   (token :ServingState
                                          (text :ServingState-text-node))
                                   (token :StartTime
                                          (text :StartTime-text-node))
                                   (token :IssuerCA
                                          (text :IssuerCA-text-node))
                                   (token :TrustedCA
                                          (text :TrustedCA-text-node))
                                   (token :DowntimeAnnounce
                                          (text :DowntimeAnnounce-text-node))
                                   (token :DowntimeStart
                                          (text :DowntimeStart-text-node))
                                   (token :DowntimeEnd
                                          (text :DowntimeEnd-text-node))
                                   (token :DowntimeInfo
                                          (text :DowntimeInfo-text-node))
                                   (token :Extensions
                                          (token :Extension
                                                 (attribute :Key-attribute)
                                                 (text :Extension-text-node)))
                                   (token :AccessPolicy
                                          (attribute :CreationTime-attribute)
                                          (attribute :Validity-attribute)
                                          (attribute :BaseType-attribute)
                                          (token :LocalID
                                                 (text :LocalID-text-node))
                                          (token :Scheme
                                                 (text :Scheme-text-node))
                                          (token :Rule
                                                 (text :Rule-text-node)))
                                   (token :Associations
                                          (token :ActivityID
                                                 (text :ActivityID-text-node))))
                            (token :Activities
                                   (token :Activity
                                          (attribute :BaseType-attribute)
                                          (token :ID
                                                 (text :ID-text-node))
                                          (token :Extensions
                                                 (token :Extension
                                                        (attribute :Key-attribute)
                                                        (text :Extension-text-node)))
                                          (token :Associations
                                                 (token :EndpointID
                                                        (text :EndpointID-text-node))
                                                 (token :UserDomainID
                                                        (text :UserDomainID-text-node))
                                                 (token :ActivityID
                                                        (text :ActivityID-text-node)))))
                            (token :Associations
                                   (token :ServiceID
                                          (text :ServiceID-text-node))))
                     (token :ComputingService
                            (attribute :CreationTime-attribute)
                            (attribute :Validity-attribute)
                            (attribute :BaseType-attribute)
                            (token :ID
                                   (text :ID-text-node))
                            (token :Name
                                   (text :Name-text-node))
                            (token :Capability
                                   (text :Capability-text-node))
                            (token :Type
                                   (text :Type-text-node))
                            (token :QualityLevel
                                   (text:QualityLevel-text-node))
                            (token :StatusPage
                                   (text :StatusPage-text-node))
                            (token :Complexity)
                            (token :OtherInfo
                                   (text :OtherInfo-text-node))
                            (token :Extensions
                                   (token :Extension
                                          (attribute :Key-attribute)
                                          (text :Extension-text-node)))
                            (token :Location
                                   (attribute :CreationTime-attribute)
                                   (attribute :Validity-attribute)
                                   (token :LocalID
                                          (text :LocalID-text-node))
                                   (token :Name
                                          (text :Name-text-node))
                                   (token :Address
                                          (text :Address-text-node))
                                   (token :Place
                                          (text :Place-text-node))
                                   (token :Country
                                          (text :Country-text-node))
                                   (token :PostCode
                                          (text :PostCode-text-node))
                                   (token :Latitude
                                          (text :Latitude-text-node))
                                   (token :Longitude
                                          (text :Longitude-text-node)))
                            (token :Contact
                                   (attribute :CreationTime-attribute)
                                   (attribute :Validity-attribute)
                                   (token :LocalID
                                          (text :LocalID-text-node))
                                   (token :URL
                                          (text :URL-text-node))
                                   (token :Type
                                          (text :Type-text-node))
                                   (token :OtherInfo
                                          (text :OtherInfo-text-node)))
                            (token :ComputingEndpoint
                                   (attribute :CreationTime-attribute)
                                   (attribute :Validity-attribute)
                                   (attribute :BaseType-attribute)
                                   (token :ID
                                          (text :ID-text-node))
                                   (token :Name
                                          (text :Name-text-node))
                                   (token :URL
                                          (text :URL-text-node))
                                   (token :Technology
                                          (text :Technology-text-node))
                                   (token :Interface
                                          (text :Interface-text-node))
                                   (token :InterfaceExtension
                                          (text :InterfaceExtension-text-node))
                                   (token :WSDL
                                          (text :WSDL-text-node))(token :SupportedProfile
                                                                        (text :SupportedProfile-text-node))
                                   (token :Semantics
                                          (text :Semantics-text-node))
                                   (token :Implementor
                                          (text :Implementor-text-node))
                                   (token :ImplementationName
                                          (text :ImplementationName-text-node))
                                   (token :ImplementationVersion
                                          (text :ImplementationVersion-text-node))
                                   (token :QualityLevel
                                          (text :QualityLevel-text-node))
                                   (token :HealthState
                                          (text :HealthState-text-node))
                                   (token :HealthStateInfo
                                          (text :HealthStateInfo-text-node))
                                   (token :ServingState
                                          (text :ServingState-text-node))
                                   (token :StartTime
                                          (text :StartTime-text-node))
                                   (token :IssuerCA
                                          (text :IssuerCA-text-node))
                                   (token :DowntimeAnnounce
                                          (text :DowntimeAnnounce-text-node))
                                   (token :DowntimeStart
                                          (text :DowntimeStart-text-node))
                                   (token :DowntimeEnd
                                          (text :DowntimeEnd-text-node))
                                   (token :DowntimeInfo
                                          (text :DowntimeInfo-text-node))
                                   (token :Staging
                                          (text :Staging-text-node))
                                   (token :JobDescription
                                          (text :JobDescription-text-node))
                                   (token :Extensions
                                          (token :Extension
                                                 (attribute :Key-attribute)
                                                 (text :Extension-text-node)))
                                   (token :AccessPolicy
                                          (attribute :CreationTime-attribute)
                                          (attribute :Validity-attribute)
                                          (attribute :BaseType-attribute)
                                          (token :LocalID
                                                 (text :LocalID-text-node))
                                          (token :Scheme
                                                 (text :Scheme-text-node))
                                          (token :Rule
                                                 (text :Rule-text-node)))
                                   (token :Associations
                                          (token :ComputingShareLocalID)
                                          (token :ComputingActivityID)))
                            (token :ComputingShares
                                   (token :ComputingShare
                                          (attribute :BaseType-attribute)
                                          (token :LocalID
                                                 (text :LocalID-text-node))
                                          (token :Name
                                                 (text :Name-text-node))
                                          (token :Description)
                                          (token :MappingQueue
                                                 (text :MappingQueue-text-node))
                                          (token :MaxWallTime
                                                 (text :MaxWallTime-text-node))
                                          (token :MaxTotalWallTime
                                                 (text :MaxTotalWallTime-text-node))
                                          (token :ServingState
                                                 (text :ServingState-text-node))
                                          (token :Extensions
                                                 (token :Extension
                                                        (attribute :Key-attribute)
                                                        (text :Extension-text-node)))))
                            (token :ComputingActivitites
                                   (token :ComputingActivitiy
                                          (attribute :BaseType-attribute)
                                          (token :ID)
                                          (token :Name)
                                          (token :Type
                                                 (text :Type-text-node))
                                          (token :IDFromEndpoint)
                                          (token :State)
                                          (token :UserDomain)
                                          (token :Owner)))
                            (token :Associations
                                   (token :ServiceID
                                          (text :ServiceID-text-node)))))
              (token :Associations
                     (token :AdminDomainID
                            (text :AdminDomainID-text-node))))
       (token :UserDomain
              (attribute :BaseType-attribute)
              (token :ID
                     (text :ID-text-node))
              (token :Name
                     (text :Name-text-node))
              (token :Description
                     (text :Description-text-node))
              (token :WWW
                     (text :WWW-text-node))
              (token :OtherInfo
                     (text :OtherInfo-text-node))
              (token :Level
                     (text :Level-text-node))
              (token :UserManager
                     (text :UserManager-text-node))
              (token :Member
                     (text :Member-text-node))
              (token :Extensions
                     (token :Extension
                            (attribute :Key-attribute)
                            (text :Extension-text-node)))
              (token :Associations
                     (token :UserDomainID
                            (text :UserDomainID-text-node)))))