(foreach [:activity]
         (add-instance :Activity
                       (mapping-pk [:Id] :ID)
                       (mapping [:Name] :Name)
                       (mapping [:CreationTime] :CreationTime)
                       (mapping [:Validity] :Validity))
         (add-association :SubmittedByEndpointActivity
                          (role [:Activity] :Id)
                          (role [:Endpoint] :endpointId))
         (add-association :MappedIntoActivityResource
                          (role [:Activity] :Id)
                          (role [:Resource] :resourceId))
         (add-association :RunsActivityShare
                          (role [:Activity] :Id)
                          (role [:Resource] :resourceId))
         (add-association :CreatesActivityUserDomain
                          (role [:Activity] :Id)
                          (role [:UserDomain] :userDomainId)))

(foreach [:activityactivity]
         (add-association :RelatesToActivityActivity
                          (role [:Activity] :activityId1)
                          (role [:ToActivity] :activityId2)))

(foreach [:contact]
         (add-instance :Contact
                       (mapping-pk [:Id] :ID)
                       (mapping [:Name] :Name)
                       (mapping [:CreationTime] :CreationTime)
                       (mapping [:Validity] :Validity)
                       (mapping [:Detail] :OtherInfo)
                       (mapping [:Type] :Type)))

(foreach [:endpoint]
         (add-instance :Endpoint
                       (mapping-pk [:Id] :ID)
                       (mapping [:Name] :Name)
                       (mapping [:CreationTime] :CreationTime)
                       (mapping [:URL] :URL)
                       (mapping [:Technology] :Technology)
                       (mapping [:InterfaceName] :InterfaceName)
                       (mapping [:Implementor] :Implementor)
                       (mapping [:ImplementationName] :ImplementationName)
                       (mapping [:ImplementationVersion] :ImplementationVersion)
                       (mapping [:QualityLevel] :QualityLevel)
                       (mapping [:HealthState] :HealthState)
                       (mapping [:HealthStateInfo] :HealthStateInfo)
                       (mapping [:ServingState] :ServingState)
                       (mapping [:StartTime] :StartTime)
                       (mapping [:IssuerCA] :IssuerCA)
                       (mapping [:DowntimeAnnounce] :DowntimeAnnounce)
                       (mapping [:DowntimeInfo] :DowntimeInfo))
         (add-association :ExposesServiceEndpoint
                          (role [:Endpoint] :Id)
                          (role [:Service] :serviceId)))

(foreach [:location]
         (add-instance :Location
                       (mapping-pk [:Id] :ID)
                       (mapping [:Name] :Name)
                       (mapping [:CreationTime] :CreationTime)
                       (mapping [:Address] :Address)
                       (mapping [:Validity] :Validity)
                       (mapping [:Place] :Place)
                       (mapping [:PostCode] :PostCode)
                       (mapping [:Latitude] :Latitude)
                       (mapping [:Longitude] :Longitude)))

(foreach [:manager]
         (add-instance :Manager
                       (mapping [:managerId] :ID)
                       (mapping [:Name] :Name)
                       (mapping [:CreationTime] :CreationTime)
                       (mapping [:Validity] :Validity)
                       (mapping [:ProductName] :ProductName)
                       (mapping [:ProductVersion] :ProductVersion))
         #_(add-association [:Id])
         (add-association :OffersServiceManager
                          (role [:Service] :serviceId)
                          (role [:Manger] :managerId)))
;ASSOC MANAGER: MANAGER   (column "managerId" :managerId :notnull) // IT IS NOT RELATION !



(foreach [:resource]
         (add-instance :Resource
                       (mapping-pk [:Id] :ID)
                       (mapping [:Name] :Name)
                       (mapping [:CreationTime] :CreationTime)
                       (mapping [:Validity] :Validity))
         (add-association :ManagesManagerResource
                          (role [:Resource] :Id)
                          (role [:Manager] :managerId)))

(foreach [:service]
         (add-instance :Service
                       (mapping-pk [:Id] :ID)
                       (mapping [:Name] :Name)
                       (mapping [:CreationTime] :CreationTime)
                       (mapping [:Validity] :Validity)
                       (mapping [:Type] :Type)
                       (mapping [:QualityLevel] :QualityLevel)
                       (mapping [:Complexity] :Complexity))
         (add-association :PrimaryLocatedAtServiceLocation
                          (role [:Service] :Id)
                          (role [:Location] :locationId))
         (add-association :PrimaryLocatedAtDomainLocation
                          (role [:Service] :Id)
                          (role [:Domain] :adminDomainId)))

(foreach [:share]
         (add-instance :Share
                       (mapping-pk [:Id] :ID)
                       (mapping [:Name] :Name)
                       (mapping [:CreationTime] :CreationTime)
                       (mapping [:Validity] :Validity)
                       (mapping [:Description] :Description))
         (add-association :OffersServiceShare
                          (role [:Share] :Id)
                          (role [:Service] :serviceId)))


(foreach [:shareendpoint]
         (add-association :OffersEndpointShare
                        (role [:Endpoint] :endpointId)
                        (role [:Share] :shareId)))

(foreach [:admindomain]
         (add-instance :AdminDomain (mapping-pk [:adminDomainId] :ID)
                       (mapping [:Distributed] :Distributed)))


(foreach [:domain]
         (add-instance :Domain
                       (mapping-pk [:Id] :ID)
                       (mapping [:Name] :Name)
                       (mapping [:CreationTime] :CreationTime)
                       (mapping [:Validity] :Validity)
                       (mapping [:Description] :Description))
         (add-association :PrimaryLocatedAtDomainLocation
                          (role [:Domain] :Id)
                          (role [:Location] :locationId)))

(foreach [:domaincontact]
         (add-association :HasContactDomain
                          (role [:Domain] :domainId)
                          (role [:Contact] :contactId)))

(foreach [:userdomain]
         (add-instance :UserDomain
                       (mapping-pk [:Id] :ID)
                       (mapping [:Level] :Level))
         (add-association :ParticipatesInUserDomainUserDomain
                          (role [:UserDomain] :Id)
                          (role [:ToUserDomain] :userDomainId)))

(foreach [:userdomainpolicy
          (add-association :HasPoliciesUserDomainPolicy
                           (role [:UserDomain] :userDomainId)
                           (role [:Policy] :policyIdl))])


(foreach [:policy]
         (add-instance :Policy
                       (mapping-pk [:Id] :ID)
                       (mapping [:Name] :Name)
                       (mapping [:CreationTime] :CreationTime)
                       (mapping [:Validity] :Validity)
                       (mapping [:Scheme] :Scheme)))


(foreach [:mappingpolicy]
         (add-instance :mappingPolicy
                       (mapping-pk [:Id] :ID)
                       (mapping [:Name] :Name)
                       (mapping [:CreationTime] :CreationTime)
                       (mapping [:Validity] :Validity)
                       (mapping [:Scheme] :Scheme))
         (add-association :CanBeMappedIntoMappingPolicyShare
                          (role [:MappingPolicy] :Id)
                          (role [:Share] :shareId)))

(foreach [:accesspolicy]
         (add-instance :AcessPolicy
                       (mapping-pk [:Id] :Id))
         (add-association :CanAccessAccessPolicyEndpoint
                          (role [:AccessPolicy] :Id)
                          (role [:Endpoint] :endpointId)))

(foreach [:servicecontact]
         (add-association :HasContactServices
                          (role [:Service] :serviceId)
                          (role [:Contact] :contactId)))
