(token "glue:Domains" :glue:Domains
       (attribute "xmlns:glue" :xmlns:glue-attribute)
       (attribute "xmlns:xsi" :xmlns:xsi-attribute)
       (attribute "xsi:schemaLocation" :xsi:schemaLocation-attribute)
       (token "AdminDomain" :AdminDomain
              (attribute "CreationTime" :CreationTime-attribute)
              (attribute "Validity" :Validity-attribute)
              (attribute "BaseType" :BaseType-attribute)
              (token "ID" :ID
                     (text :ID-text-node))
              (token "Name" :Name
                     (text :Name-text-node))
              (token "Description" :Description
                     (text :Description-text-node))
              (token "WWW" :WWW
                     (text :WWW-text-node))
              (token "OtherInfo" :OtherInfo
                     (text :OtherInfo-text-node))
              (token "Distributed" :Distributed
                     (text :Distributed-text-node))
              (token "Owner" :Owner
                     (text :Owner-text-node))
              (token "Extensions" :Extensions
                     (token "Extension" :Extension
                            (attribute "Key" :Key-attribute)
                            (text :Extension-text-node)))
              (token "Location" :Location
                     (attribute "CreationTime" :CreationTime-attribute)
                     (attribute "Validity" :Validity-attribute)
                     (token "LocalID" :LocalID
                            (text :LocalID-text-node))
                     (token "Name" :Name
                            (text :Name-text-node))
                     (token"Address" :Address
                           (text :Address-text-node))
                     (token "Place" :Place
                            (text :Place-text-node))
                     (token "Country" :Country
                            (text :Country-text-node))
                     (token "PostCode" :PostCode
                            (text :PostCode-text-node))
                     (token "Latitude" :Latitude
                            (text :Latitude-text-node))
                     (token "Longitude" :Longitude
                            (text :Longitude-text-node))
                     (token "Extensions" :Extensions
                            (token "Extension" :Extension
                                   (attribute "Key" :Key-attribute)
                                   (text :Extension-text-node))))
              (token "Contact" :Contact
                     (attribute "CreationTime" :CreationTime-attribute)
                     (attribute "Validity" :Validity-attribute)
                     (token "LocalID" :LocalID
                            (text :LocalID-text-node))
                     (token "URL" :URL
                            (text :URL-text-node))
                     (token "Type" :Type
                            (text :Type-text-node))
                     (token "OtherInfo" :OtherInfo
                            (text :OtherInfo-text-node))
                     (token "Extensions" :Extensions
                            (token "Extension" :Extension
                                   (attribute "Key" :Key-attribute)
                                   (text :Extension-text-node))))
              (token "Services" :Services
                     (token "Service" :Service
                            (attribute "BaseType" :BaseType-attribute)
                            (token "ID" :ID
                                   (text :ID-text-node))
                            (token "Name" :Name
                                   (text :Name-text-node))
                            (token"Capability" :Capability
                                  (text :Capability-text-node))
                            (token "Type" :Type
                                   (text :Type-text-node))
                            (token "QualityLevel" :QualityLevel
                                   (text :QualityLevel-text-node))
                            (token "StatusPage" :StatusPage)
                            (token "Complexity" :Complexity
                                   (text :Complexity-text-node))
                            (token "Extensions" :Extensions
                                   (token "Extension" :Extension
                                          (attribute "Key" :Key-attribute)
                                          (text :Extension-text-node)))
                            (token "Location" :Location
                                   (token "LocalID" :LocalID
                                          (text :LocalID-text-node))
                                   (token "Name" :Name
                                          (text :Name-text-node))
                                   (token "Address" :Address
                                          (text :Address-text-node))
                                   (token "Place" :Place
                                          (text :Place-text-node))
                                   (token "Country" :Country
                                          (text :Country-text-node))
                                   (token "PostCode" :PostCode
                                          (text :PostCode-text-node))
                                   (token "Latitude" :Latitude
                                          (text :Latitude-text-node))
                                   (token "Longitude" :Longitude
                                          (text :Longitude-text-node))
                                   (token "Extensions" :Extensions
                                          (token "Extension" :Extension
                                                 (attribute "Key" :Key-attribute)
                                                 (text :Extension-text-node))))
                            (token "Contact" :Contact
                                   (token "LocalID" :LocalID
                                          (text :LocalID-text-node))
                                   (token "URL" :URL
                                          (text :URL-text-node))
                                   (token "Type" :Type
                                          (text :Type-text-node))
                                   (token "OtherInfo" :OtherInfo
                                          (text :OtherInfo-text-node))
                                   (token "Extensions" :Extensions
                                          (token "Extension" :Extension
                                                 (attribute "Key" :Key-attribute)
                                                 (text :Extension-text-node))))
                            (token "Endpoint" :Endpoint
                                   (attribute "BaseType" :BaseType-attribute)
                                   (token "ID" :ID
                                          (text :ID-text-node))
                                   (token "Name" :Name
                                          (text :Name-text-node))
                                   (token "URL" :URL
                                          (text :URL-text-node))
                                   (token "Capability" :Capability
                                          (text :Capability-text-node))
                                   (token "Technology" :Technology
                                          (text :Technology-text-node))
                                   (token "Interface" :Interface
                                          (text :Interface-text-node))
                                   (token "InterfaceExtension" :InterfaceExtension
                                          (text :InterfaceExtension-text-node))
                                   (token "WSDL" :WSDL
                                          (text :WSDL-text-node))
                                   (token "SupportedProfile" :SupportedProfile
                                          (text :SupportedProfile-text-node))
                                   (token "Semantics" :Semantics
                                          (text :Semantics-text-node))
                                   (token "Implementor" :Implementor
                                          (text :Implementor-text-node))
                                   (token "ImplementationName" :ImplementationName
                                          (text :ImplementationName-text-node))
                                   (token "ImplementationVersion" :ImplementationVersion
                                          (text :ImplementationVersion-text-node))
                                   (token "QualityLevel" :QualityLevel
                                          (text :QualityLevel-text-node))
                                   (token "HealthState" :HealthState
                                          (text :HealthState-text-node))
                                   (token "HealthStateInfo" :HealthStateInfo
                                          (text :HealthStateInfo-text-node))
                                   (token "ServingState" :ServingState
                                          (text :ServingState-text-node))
                                   (token "StartTime" :StartTime
                                          (text :StartTime-text-node))
                                   (token "IssuerCA" :IssuerCA
                                          (text :IssuerCA-text-node))
                                   (token "TrustedCA" :TrustedCA
                                          (text :TrustedCA-text-node))
                                   (token "DowntimeAnnounce" :DowntimeAnnounce
                                          (text :DowntimeAnnounce-text-node))
                                   (token "DowntimeStart" :DowntimeStart
                                          (text :DowntimeStart-text-node))
                                   (token "DowntimeEnd" :DowntimeEnd
                                          (text :DowntimeEnd-text-node))
                                   (token "DowntimeInfo" :DowntimeInfo
                                          (text :DowntimeInfo-text-node))
                                   (token "Extensions" :Extensions
                                          (token "Extension" :Extension
                                                 (attribute "Key" :Key-attribute)
                                                 (text :Extension-text-node)))
                                   (token "AccessPolicy" :AccessPolicy
                                          (attribute "CreationTime" :CreationTime-attribute)
                                          (attribute "Validity" :Validity-attribute)
                                          (attribute "BaseType":BaseType-attribute)
                                          (token "LocalID" :LocalID
                                                 (text :LocalID-text-node))
                                          (token "Scheme" :Scheme
                                                 (text :Scheme-text-node))
                                          (token "Rule" :Rule
                                                 (text :Rule-text-node)))
                                   (token "Associations" :Associations
                                          (token "ActivityID" :ActivityID
                                                 (text :ActivityID-text-node))))
                            (token "Activities" :Activities
                                   (token "Activity" :Activity
                                          (attribute "BaseType" :BaseType-attribute)
                                          (token "ID" :ID
                                                 (text :ID-text-node))
                                          (token "Extensions" :Extensions
                                                 (token "Extension" :Extension
                                                        (attribute "Key" :Key-attribute)
                                                        (text :Extension-text-node)))
                                          (token "Associations" :Associations
                                                 (token "EndpointID" :EndpointID
                                                        (text :EndpointID-text-node))
                                                 (token "UserDomainID" :UserDomainID
                                                        (text :UserDomainID-text-node))
                                                 (token "ActivityID" :ActivityID
                                                        (text :ActivityID-text-node)))))
                            (token "Associations" :Associations
                                   (token "ServiceID" :ServiceID
                                          (text :ServiceID-text-node))))
                     (token "ComputingService" :ComputingService
                            (attribute "CreationTime" :CreationTime-attribute)
                            (attribute "Validity" :Validity-attribute)
                            (attribute "BaseType" :BaseType-attribute)
                            (token "ID" :ID
                                   (text :ID-text-node))
                            (token "Name" :Name
                                   (text :Name-text-node))
                            (token "Capability" :Capability
                                   (text :Capability-text-node))
                            (token "Type" :Type
                                   (text :Type-text-node))
                            (token "QualityLevel" :QualityLevel
                                   (text :QualityLevel-text-node))
                            (token "StatusPage" :StatusPage
                                   (text :StatusPage-text-node))
                            (token "Complexity" :Complexity)
                            (token "OtherInfo" :OtherInfo
                                   (text :OtherInfo-text-node))
                            (token "Extensions" :Extensions
                                   (token "Extension" :Extension
                                          (attribute "Key" :Key-attribute)
                                          (text :Extension-text-node)))
                            (token "Location" :Location
                                   (attribute "CreationTime" :CreationTime-attribute)
                                   (attribute "Validity" :Validity-attribute)
                                   (token "LocalID" :LocalID
                                          (text :LocalID-text-node))
                                   (token "Name" :Name
                                          (text :Name-text-node))
                                   (token "Address" :Address
                                          (text :Address-text-node))
                                   (token "Place" :Place
                                          (text :Place-text-node))
                                   (token "Country" :Country
                                          (text :Country-text-node))
                                   (token "PostCode" :PostCode
                                          (text :PostCode-text-node))
                                   (token "Latitude" :Latitude
                                          (text :Latitude-text-node))
                                   (token "Longitude" :Longitude
                                          (text :Longitude-text-node)))
                            (token "Contact" :Contact
                                   (attribute "CreationTime" :CreationTime-attribute)
                                   (attribute "Validity" :Validity-attribute)
                                   (token "LocalID" :LocalID
                                          (text :LocalID-text-node))
                                   (token "URL" :URL
                                          (text :URL-text-node))
                                   (token "Type" :Type
                                          (text :Type-text-node))
                                   (token "OtherInfo" :OtherInfo
                                          (text :OtherInfo-text-node)))
                            (token "ComputingEndpoint" :ComputingEndpoint
                                   (attribute "CreationTime" :CreationTime-attribute)
                                   (attribute "Validity" :Validity-attribute)
                                   (attribute "BaseType" :BaseType-attribute)
                                   (token "ID" :ID
                                          (text :ID-text-node))
                                   (token "Name" :Name
                                          (text :Name-text-node))
                                   (token "URL" :URL
                                          (text :URL-text-node))
                                   (token "Technology" :Technology
                                          (text :Technology-text-node))
                                   (token "Interface" :Interface
                                          (text :Interface-text-node))
                                   (token "InterfaceExtension" :InterfaceExtension
                                          (text :InterfaceExtension-text-node))
                                   (token "WSDL" :WSDL
                                          (text :WSDL-text-node))
                                   (token "SupportedProfile" :SupportedProfile
                                          (text :SupportedProfile-text-node))
                                   (token "Semantics" :Semantics
                                          (text :Semantics-text-node))
                                   (token "Implementor" :Implementor
                                          (text :Implementor-text-node))
                                   (token "ImplementationName" :ImplementationName
                                          (text :ImplementationName-text-node))
                                   (token "ImplementationVersion" :ImplementationVersion
                                          (text :ImplementationVersion-text-node))
                                   (token "QualityLevel" :QualityLevel
                                          (text :QualityLevel-text-node))
                                   (token "HealthState" :HealthState
                                          (text :HealthState-text-node))
                                   (token "HealthStateInfo" :HealthStateInfo
                                          (text :HealthStateInfo-text-node))
                                   (token "ServingState" :ServingState
                                          (text :ServingState-text-node))
                                   (token "StartTime" :StartTime
                                          (text :StartTime-text-node))
                                   (token "IssuerCA" :IssuerCA
                                          (text :IssuerCA-text-node))
                                   (token "DowntimeAnnounce" :DowntimeAnnounce
                                          (text :DowntimeAnnounce-text-node))
                                   (token "DowntimeStart" :DowntimeStart
                                          (text :DowntimeStart-text-node))
                                   (token "DowntimeEnd" :DowntimeEnd
                                          (text :DowntimeEnd-text-node))
                                   (token "DowntimeInfo" :DowntimeInfo
                                          (text :DowntimeInfo-text-node))
                                   (token "Staging" :Staging
                                          (text :Staging-text-node))
                                   (token "JobDescription" :JobDescription
                                          (text :JobDescription-text-node))
                                   (token "Extensions" :Extensions
                                          (token "Extension" :Extension
                                                 (attribute "Key" :Key-attribute)
                                                 (text:Extension-text-node)))
                                   (token "AccessPolicy" :AccessPolicy
                                          (attribute "CreationTime" :CreationTime-attribute)
                                          (attribute "Validity" :Validity-attribute)
                                          (attribute "BaseType" :BaseType-attribute)
                                          (token "LocalID" :LocalID
                                                 (text :LocalID-text-node))
                                          (token "Scheme" :Scheme
                                                 (text :Scheme-text-node))
                                          (token "Rule" :Rule
                                                 (text :Rule-text-node)))
                                   (token "Associations" :Associations
                                          (token "ComputingShareLocalID" :ComputingShareLocalID)
                                          (token "ComputingActivityID" :ComputingActivityID)))
                            (token "ComputingShares" :ComputingShares
                                   (token "ComputingShare" :ComputingShare
                                          (attribute "BaseType" :BaseType-attribute)
                                          (token "LocalID" :LocalID
                                                 (text :LocalID-text-node))
                                          (token "Name" :Name
                                                 (text :Name-text-node))
                                          (token "Description" :Description)
                                          (token "MappingQueue" :MappingQueue
                                                 (text :MappingQueue-text-node))
                                          (token "MaxWallTime" :MaxWallTime
                                                 (text :MaxWallTime-text-node))
                                          (token "MaxTotalWallTime" :MaxTotalWallTime
                                                 (text :MaxTotalWallTime-text-node))
                                          (token "ServingState" :ServingState
                                                 (text :ServingState-text-node))
                                          (token "Extensions" :Extensions
                                                 (token "Extension" :Extension
                                                        (attribute "Key" :Key-attribute)
                                                        (text :Extension-text-node)))))
                            (token "ComputingActivitites" :ComputingActivitites
                                   (token "ComputingActivitiy" :ComputingActivitiy
                                          (attribute "BaseType" :BaseType-attribute)
                                          (token "ID" :ID)
                                          (token "Name" :Name)
                                          (token "Type" :Type
                                                 (text :Type-text-node))
                                          (token "IDFromEndpoint" :IDFromEndpoint)
                                          (token "State" :State)
                                          (token "UserDomain" :UserDomain)
                                          (token "Owner" :Owner)))
                            (token "Associations" :Associations
                                   (token "ServiceID" :ServiceID
                                          (text :ServiceID-text-node)))))
              (token "Associations" :Associations
                     (token "AdminDomainID" :AdminDomainID
                            (text :AdminDomainID-text-node))))
       (token "UserDomain" :UserDomain
              (attribute "BaseType" :BaseType-attribute)
              (token "ID" :ID
                     (text :ID-text-node))
              (token "Name" :Name
                     (text :Name-text-node))
              (token "Description" :Description
                     (text :Description-text-node))
              (token "WWW" :WWW
                     (text :WWW-text-node))
              (token "OtherInfo" :OtherInfo
                     (text :OtherInfo-text-node))
              (token "Level" :Level
                     (text :Level-text-node))
              (token "UserManager" :UserManager
                     (text :UserManager-text-node))
              (token "Member" :Member
                     (text :Member-text-node))
              (token "Extensions" :Extensions
                     (token "Extension" :Extension
                            (attribute "Key" :Key-attribute)
                            (text :Extension-text-node)))
              (token "Associations" :Associations
                     (token "UserDomainID" :UserDomainID
                            (text :UserDomainID-text-node)))))