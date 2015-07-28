(for-each :GLUE2AdminDomain
         (add-instance :AdminDomain
                       (mapping :GLUE2EntityName :Name)
                       (mapping :GLUE2EntityOtherInfo :OtherInfo)
                       (mapping :GLUE2EntityCreationTime :CreationTime)
                       (mapping :GLUE2EntityValidity :Validity)
                       (mapping :GLUE2DomainID :ID)
                       (mapping :GLUE2DomainDescription :Description)
                       (mapping :GLUE2DomainWWW :WWW)
                       (mapping :GLUE2AdminDomainDistributed :Distributed)
                       (mapping :GLUE2AdminDomainOwner :Owner))
         (add-association :ParticipatesInAdminDomainAdminDomain
                          (mapping-fk :GLUE2AdminDomainAdminDomainForeignKey :AdminDomain)
                          (mapping-pk :GLUE2DomainID :ToAdminDomain)))

(for-each :GLUE2UserDomain
         (add-instance :UserDomain
                       (mapping :GLUE2EntityName :Name)
                       (mapping :GLUE2EntityOtherInfo :OtherInfo)
                       (mapping :GLUE2EntityCreationTime :CreationTime)
                       (mapping :GLUE2EntityValidity :Validity)
                       (mapping :GLUE2DomainID :ID)
                       (mapping :GLUE2DomainDescription :Description)
                       (mapping :GLUE2DomainWWW :WWW)
                       (mapping :GLUE2UserDomainLevel :Level)
                       (mapping :GLUE2UserDomainUserManager :UserManager)
                       (mapping :GLUE2UserDomainMember :Member))
         (add-association :ParticipatesInUserDomainUserDomain
                          (mapping-fk :GLUE2UserDomainUserDomainForeignKey :UserDomain)
                          (mapping-pk :GLUE2DomainID :ToUserDomain)))

(for-each :GLUE2Extension
         (add-instance :Extension
                       (mapping :GLUE2ExtensionKey :Key)
                       (mapping :GLUE2ExtensionValue :Value))
         #_(add-association :HasExtentsionEntity
                          (mapping-fk :GLUE2ExtensionEntityForeignKey :Extension)
                          (mapping-pk :GLUE2ExtensionKey :Key)
                          (mapping-pk :GLUE2ExtensionValue :Value)))

(for-each :GLUE2Location
         (add-instance :Location
                       (mapping :GLUE2EntityName :Name)
                       (mapping :GLUE2EntityOtherInfo :OtherInfo)
                       (mapping :GLUE2EntityCreationTime :CreationTime)
                       (mapping :GLUE2EntityValidity :Validity)
                       (mapping :GLUE2LocationID :ID)
                       (mapping :GLUE2LocationAddress :Address)
                       (mapping :GLUE2LocationPlace :Place)
                       (mapping :GLUE2LocationCountry :Country)
                       (mapping :GLUE2LocationPostCode :PostCode)
                       (mapping :GLUE2LocationLatitude :Latitude)
                       (mapping :GLUE2LocationLongitude :Longitude))
         (add-association :PrimaryLocatedAtDomainLocation
                          (mapping-fk :GLUE2LocationDomainForeignKey :Domain)
                          (mapping-pk :GLUE2LocationID :Location))
         (add-association :PrimaryLocatedAtServiceLocation
                          (mapping-fk :GLUE2LocationServiceForeignKey :Service)
                          (mapping-pk :GLUE2LocationID :Location)))

(for-each :GLUE2Contact
         (add-instance :Contact
                       (mapping :GLUE2EntityName :Name)
                       (mapping :GLUE2EntityOtherInfo :OtherInfo)
                       (mapping :GLUE2EntityCreationTime :CreationTime)
                       (mapping :GLUE2EntityValidity :Validity)
                       (mapping :GLUE2ContactID :ID)
                       (mapping :GLUE2ContactDetail :Detail)
                       (mapping :GLUE2ContactType :Type))
         (add-association :HasContactServices
                          (mapping-fk :GLUE2ContactServiceForeignKey :Service)
                          (mapping-pk :GLUE2ContactID :Contact))
         (add-association :HasContactDomain
                          (mapping-fk :GLUE2ContactDomainForeignKey :Domain)
                          (mapping-pk :GLUE2ContactID :Contact)))

(for-each :GLUE2Service
         (add-instance :Service
                       (mapping :GLUE2EntityName :Name)
                       (mapping :GLUE2EntityOtherInfo :OtherInfo)
                       (mapping :GLUE2EntityCreationTime :CreationTime)
                       (mapping :GLUE2EntityValidity :Validity)
                       (mapping :GLUE2ServiceID :ID)
                       (mapping :GLUE2ServiceType :Type)
                       (mapping :GLUE2ServiceQualityLevel :QualityLevel)
                       (mapping :GLUE2ServiceCapability :Capability)
                       (mapping :GLUE2ServiceStatusInfo :StatusInfo)
                       (mapping :GLUE2ServiceComplexity :Complexity))
         (add-association :ManagesAdminDomainService
                          (mapping-fk :GLUE2ServiceAdminDomainForeignKey :AdminDomain)
                          (mapping-pk :GLUE2ServiceID :Service))
         (add-association :RelatesToServiceService
                          (mapping-fk :GLUE2ServiceServiceForeignKey :Service)
                          (mapping-pk :GLUE2ServiceID :ToService)))

(for-each :GLUE2Endpoint
         (add-instance :Endpoint
                       (mapping :GLUE2EntityName :Name)
                       (mapping :GLUE2EntityOtherInfo :OtherInfo)
                       (mapping :GLUE2EntityCreationTime :CreationTime)
                       (mapping :GLUE2EntityValidity :Validity)
                       (mapping :GLUE2EndpointID :ID)
                       (mapping :GLUE2EndpointURL :URL)
                       (mapping :GLUE2EndpointInterfaceName :InterfaceName)
                       (mapping :GLUE2EndpointQualityLevel :QualityLevel)
                       (mapping :GLUE2EndpointHealthState :HealthState)
                       (mapping :GLUE2EndpointServingState :ServingState)
                       (mapping :GLUE2EndpointCapability :Capability)
                       (mapping :GLUE2EndpointTechnology :Technology)
                       (mapping :GLUE2EndpointInterfaceVersion :InterfaceVersion)
                       (mapping :GLUE2EndpointInterfaceExtension :InterfaceExtension)
                       (mapping :GLUE2EndpointWSDL :WSDL)
                       (mapping :GLUE2EndpointSupportedProfile :SupportedProfile)
                       (mapping :GLUE2EndpointSemantics :Semantics)
                       (mapping :GLUE2EndpointImplementor :Implementor)
                       (mapping :GLUE2EndpointImplementationName :ImplementationName)
                       (mapping :GLUE2EndpointImplementationVersion :ImplementationVersion)
                       (mapping :GLUE2EndpointHealthStateInfo :HealthStateInfo)
                       (mapping :GLUE2EndpointStartTime :StartTime)
                       (mapping :GLUE2EndpointIssuerCA :IssuerCA)
                       (mapping :GLUE2EndpointTrustedCA :TrustedCA)
                       (mapping :GLUE2EndpointDowntimeAnnounce :DowntimeAnnounce)
                       (mapping :GLUE2EndpointDowntimeStart :DowntimeStart)
                       (mapping :GLUE2EndpointDowntimeEnd :DowntimeEnd)
                       (mapping :GLUE2EndpointDowntimeInfo :DowntimeInfo))
         (add-association :ExposesServiceEndpoint
                          (mapping-fk :GLUE2EndpointServiceForeignKey :Service)
                          (mapping-pk :GLUE2EndpointID :Endpoint)))

(for-each :GLUE2Share
         (add-instance :Share
                       (mapping :GLUE2EntityName :Name)
                       (mapping :GLUE2EntityOtherInfo :OtherInfo)
                       (mapping :GLUE2EntityCreationTime :CreationTime)
                       (mapping :GLUE2EntityValidity :Validity)
                       (mapping :GLUE2ShareID :ID)
                       (mapping :GLUE2ShareDescription :Description))
         (add-association :OffersServiceShare
                          (mapping-fk :GLUE2ShareServiceForeignKey :Service)
                          (mapping-pk :GLUE2ShareID :Share))
         (add-association :OffersEndpointShare
                          (mapping-fk :GLUE2ShareEndpointForeignKey :Endpoint)
                          (mapping-pk :GLUE2ShareID :Share))
         (add-association :DefinedOnShareResource
                          (mapping-fk :GLUE2ShareResourceForeignKey :Resource)
                          (mapping-pk :GLUE2ShareID :Share)))

(for-each :GLUE2Manager
         (add-instance :Manager
                       (mapping :GLUE2EntityName :Name)
                       (mapping :GLUE2EntityOtherInfo :OtherInfo)
                       (mapping :GLUE2EntityCreationTime :CreationTime)
                       (mapping :GLUE2EntityValidity :Validity)
                       (mapping :GLUE2ManagerID :ID)
                       (mapping :GLUE2ManagerProductName :ProductName)
                       (mapping :GLUE2ManagerProductVersion :ProductVersion))
         (add-association :OffersServiceManager
                          (mapping-fk :GLUE2ManagerServiceForeignKey :Service)
                          (mapping-pk :GLUE2ManagerID :Manager)))

(for-each :GLUE2Resource
         (add-instance :Resource
                       (mapping :GLUE2EntityName :Name)
                       (mapping :GLUE2EntityOtherInfo :OtherInfo)
                       (mapping :GLUE2EntityCreationTime :CreationTime)
                       (mapping :GLUE2EntityValidity :Validity)
                       (mapping :GLUE2ResourceID :ID))
         (add-association :ManagesManagerResource
                          (mapping-fk :GLUE2ResourceManagerForeignKey :Manager)
                          (mapping-pk :GLUE2ResourceID :Resource)))

(for-each :GLUE2Activity
         (add-instance :Activity
                       (mapping :GLUE2EntityName :Name)
                       (mapping :GLUE2EntityOtherInfo :OtherInfo)
                       (mapping :GLUE2EntityCreationTime :CreationTime)
                       (mapping :GLUE2EntityValidity :Validity)
                       (mapping :GLUE2ActivityID :ID))
         (add-association :CreatesActivityUserDomain
                          (mapping-fk :GLUE2ActivityUserDomainForeignKey :UserDomain)
                          (mapping-pk :GLUE2ActivityID :Activity))
         (add-association :SubmittedByEndpointActivity
                          (mapping-fk :GLUE2ActivityEndpointForeignKey :Endpoint)
                          (mapping-pk :GLUE2ActivityID :Activity))
         (add-association :RunsActivityShare
                          (mapping-fk :GLUE2ActivityShareForeignKey :Share)
                          (mapping-pk :GLUE2ActivityID :Activity))
         (add-association :MappedIntoActivityResource
                          (mapping-fk :GLUE2ActivityResourceForeignKey :Resource)
                          (mapping-pk :GLUE2ActivityID :Activity))
         (add-association :RelatesToActivityActivity
                          (mapping-fk :GLUE2ActivityActivityForeignKey :Activity)
                          (mapping-pk :GLUE2ActivityID :ToActivity)))

(for-each :GLUE2AccessPolicy
         (add-instance :AccessPolicy
                       (mapping :GLUE2EntityName :Name)
                       (mapping :GLUE2EntityOtherInfo :OtherInfo)
                       (mapping :GLUE2EntityCreationTime :CreationTime)
                       (mapping :GLUE2EntityValidity :Validity)
                       (mapping :GLUE2PolicyID :ID)
                       (mapping :GLUE2PolicyScheme :Scheme)
                       (mapping :GLUE2PolicyRule :Rule))
         (add-association :HasPoliciesUserDomainPolicy
                          (mapping-fk :GLUE2PolicyUserDomainForeignKey :UserDomain)
                          (mapping-pk :GLUE2PolicyID :Policy))
         (add-association :CanAccessAccessPolicyEndpoint
                          (mapping-fk :GLUE2AccessPolicyEndpointForeignKey :Endpoint)
                          (mapping-pk :GLUE2PolicyID :AccessPolicy)))

(for-each :GLUE2MappingPolicy
         (add-instance :MappingPolicy
                       (mapping :GLUE2EntityName :Name)
                       (mapping :GLUE2EntityOtherInfo :OtherInfo)
                       (mapping :GLUE2EntityCreationTime :CreationTime)
                       (mapping :GLUE2EntityValidity :Validity)
                       (mapping :GLUE2PolicyID :ID)
                       (mapping :GLUE2PolicyScheme :Scheme)
                       (mapping :GLUE2PolicyRule :Rule))
         (add-association :HasPoliciesUserDomainPolicy
                          (mapping-fk :GLUE2PolicyUserDomainForeignKey :UserDomain)
                          (mapping-pk :GLUE2PolicyID :Policy))
         (add-association :CanBeMappedIntoMappingPolicyShare
                          (mapping-fk :GLUE2MappingPolicyShareForeignKey :Share)
                          (mapping-pk :GLUE2PolicyID :MappingPolicy)))
