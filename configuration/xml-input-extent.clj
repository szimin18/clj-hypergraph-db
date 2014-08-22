;TODO Extensions

(foreach [:glue:Domains :AdminDomain]
         (add-instance :AdminDomain
                       (mapping [:CreationTime-attribute] :CreationTime)
                       (mapping [:Validity-attribute] :Validity)
                       (mapping [:Distributed :Distributed-text-node] :Distributed)
                       (mapping [:Owner :Owner-text-node] :Owner)
                       (mapping [:WWW :WWW-text-node] :WWW)
                       (mapping [:Description :Description-text-node] :Description)
                       (mapping [:ID :ID-text-node] :ID)
                       (mapping [:Name :Name-text-node] :Name)
                       (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)))

(foreach [:glue:Domains :AdminDomain :Location]
         (add-instance :Location
                       (mapping [:CreationTime-attribute] :CreationTime)
                       (mapping [:Validity-attribute] :Validity)
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:Name :Name-text-node] :Name)
                       (mapping [:Address :Address-text-node] :Address)
                       (mapping [:Place :Place-text-node] :Place)
                       (mapping [:Country :Country-text-node] :Country)
                       (mapping [:PostCode :PostCode-text-node] :PostCode)
                       (mapping [:Latitude :Latitude-text-node] :Latitude)
                       (mapping [:Longitude :Longitude-text-node] :Longitude)))

(foreach [:glue:Domains :AdminDomain]
         (add-association :PrimaryLocatedAtDomainLocation
                          (mapping [:Location] :Location)
                          (mapping [] :Domain)))

(foreach [:glue:Domains :AdminDomain :Contact]
         (add-instance :Contact
                       (mapping [:CreationTime-attribute] :CreationTime)
                       (mapping [:Validity-attribute] :Validity)
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                       (mapping [:Type :Type-text-node] :Type)
                       (mapping [:URL :URL-text-node] :Detail)))

(foreach [:glue:Domains :AdminDomain]
         (add-association :HasContactDomain
                          (mapping [:Contact] :Contact)
                          (mapping [] :Domain)))

(foreach [:glue:Domains :AdminDomain :Services :Service]
         (add-instance :Service
                       (mapping [:ID :ID-text-node] :ID)
                       (mapping [:Name :Name-text-node] :Name)
                       (mapping [:Capability :Capability-text-node] :Capability)
                       (mapping [:Type :Type-text-node] :Type)
                       (mapping [:QualityLevel :QualityLevel-text-node] :QualityLevel)
                       (mapping [:Complexity :Complexity-text-node] :Complexity)))

(foreach [:glue:Domains :AdminDomain]
         (add-association :ManagesAdminDomainService
                          (mapping [:Services :Service] :Service)
                          (mapping [] :AdminDomain)))

(foreach [:glue:Domains :AdminDomain :Services :Service :Location]
         (add-instance :Location
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:Name :Name-text-node] :Name)
                       (mapping [:Address :Address-text-node] :Address)
                       (mapping [:Place :Place-text-node] :Place)
                       (mapping [:Country :Country-text-node] :Country)
                       (mapping [:PostCode :PostCode-text-node] :PostCode)
                       (mapping [:Latitude :Latitude-text-node] :Latitude)
                       (mapping [:Longitude :Longitude-text-node] :Longitude)))

(foreach [:glue:Domains :AdminDomain :Services :Service]
         (add-association :PrimaryLocatedAtServiceLocation
                          (mapping [:Location] :Location)
                          (mapping [] :Service)))

(foreach [:glue:Domains :AdminDomain :Services :Service :Contact]
         (add-instance :Contact
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                       (mapping [:Type :Type-text-node] :Type)
                       (mapping [:URL :URL-text-node] :Detail)))

(foreach [:glue:Domains :AdminDomain :Services :Service]
         (add-association :HasContactServices
                          (mapping [:Contact] :Contact)
                          (mapping [] :Service)))

(foreach [:glue:Domains :AdminDomain :Services :Service :Endpoint]
         (add-instance :Endpoint
                       (mapping [:ID :ID-text-node] :ID)
                       (mapping [:Name :Name-text-node] :Name)
                       (mapping [:URL :URL-text-node] :URL)
                       (mapping [:Capability :Capability-text-node] :Capability)
                       (mapping [:Technology :Technology-text-node] :Technology)
                       (mapping [:Interface :Interface-text-node] :InterfaceName)
                       (mapping [:InterfaceExtension :InterfaceExtension-text-node] :InterfaceExtension)
                       (mapping [:WSDL :WSDL-text-node] :WSDL)
                       (mapping [:SupportedProfile :SupportedProfile-text-node] :SupportedProfile)
                       (mapping [:Semantics :Semantics-text-node] :Semantics)
                       (mapping [:Implementor :Implementor-text-node] :Implementor)
                       (mapping [:ImplementationName :ImplementationName-text-node] :ImplementationName)
                       (mapping [:ImplementationVersion :ImplementationVersion-text-node] :ImplementationVersion)
                       (mapping [:QualityLevel :QualityLevel-text-node] :QualityLevel)
                       (mapping [:HealthState :HealthState-text-node] :HealthState)
                       (mapping [:HealthStateInfo :HealthStateInfo-text-node] :HealthStateInfo)
                       (mapping [:ServingState :ServingState-text-node] :ServingState)
                       (mapping [:StartTime :StartTime-text-node] :StartTime)
                       (mapping [:IssuerCA :IssuerCA-text-node] :IssuerCA)
                       (mapping [:TrustedCA :TrustedCA-text-node] :TrustedCA)
                       (mapping [:DowntimeAnnounce :DowntimeAnnounce-text-node] :DowntimeAnnounce)
                       (mapping [:DowntimeStart :DowntimeStart-text-node] :DowntimeStart)
                       (mapping [:DowntimeEnd :DowntimeEnd-text-node] :DowntimeEnd)
                       (mapping [:DowntimeInfo :DowntimeInfo-text-node] :DowntimeInfo)))

(foreach [:glue:Domains :AdminDomain :Services :Service]
         (add-association :ExposesServiceEndpoint
                          (mapping [:Endpoint] :Endpoint)
                          (mapping [] :Service)))

(foreach [:glue:Domains :AdminDomain :Services :Service :Endpoint :AccessPolicy]
         (add-instance :AccessPolicy
                       (mapping [:CreationTime-attribute] :CreationTime)
                       (mapping [:Validity-attribute] :Validity)
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:Scheme :Scheme-text-node] :Scheme)
                       (mapping [:Rule :Rule-text-node] :Rule)))

(foreach [:glue:Domains :AdminDomain :Services :Service :Endpoint]
         (add-association :CanAccessAccessPolicyEndpoint
                          (mapping [:AccessPolicy] :AccessPolicy)
                          (mapping [] :Endpoint)))

(foreach [:glue:Domains :AdminDomain :Services :Service :Endpoint]
         (add-association :SubmittedByEndpointActivity
                          (mapping-pk [:Associations :ActivityID :ActivityID-text-node] :Activity)
                          (mapping [] :Endpoint)))

(foreach [:glue:Domains :AdminDomain :Services :Service :Activities :Activity]
         (add-instance :Activity
                       (mapping [:ID :ID-text-node] :ID)))

(foreach [:glue:Domains :AdminDomain :Services :Service :Activities :Activity]
         (add-association :SubmittedByEndpointActivity
                          (mapping-pk [:Associations :EndpointID :EndpointID-text-node] :Endpoint)
                          (mapping [] :Activity)))

(foreach [:glue:Domains :AdminDomain :Services :Service :Activities :Activity]
         (add-association :CreatesActivityUserDomain
                          (mapping-pk [:Associations :UserDomainID :UserDomainID-text-node] :UserDomain)
                          (mapping [] :Activity)))

(foreach [:glue:Domains :AdminDomain :Services :Service :Activities :Activity]
         (add-association :RelatesToActivityActivity
                          (mapping-pk [:Associations :ActivityID :ActivityID-text-node] :Activity)
                          (mapping [] :ToActivity)))

(foreach [:glue:Domains :AdminDomain :Services :Service]
         (add-association :RelatesToServiceService
                          (mapping-pk [:Associations :ServiceID :ServiceID-text-node] :Service)
                          (mapping [] :ToService)))

(foreach [:glue:Domains :AdminDomain :Services :ComputingService]
         (add-instance :Service
                       (mapping [:CreationTime-attribute] :CreationTime)
                       (mapping [:Validity-attribute] :Validity)
                       (mapping [:ID :ID-text-node] :ID)
                       (mapping [:Name :Name-text-node] :Name)
                       (mapping [:Capability :Capability-text-node] :Capability)
                       (mapping [:Type :Type-text-node] :Type)
                       (mapping [:QualityLevel :QualityLevel-text-node] :QualityLevel)
                       (mapping [:StatusPage :StatusPage-text-node] :StatusInfo)
                       (mapping [:Complexity :Complexity-text-node] :Complexity)))

(foreach [:glue:Domains :AdminDomain]
         (add-association :ManagesAdminDomainService
                          (mapping [:Services :ComputingService] :Service)
                          (mapping [] :AdminDomain)))

(foreach [:glue:Domains :AdminDomain :Services :ComputingService :Location]
         (add-instance :Location
                       (mapping [:CreationTime-attribute] :CreationTime)
                       (mapping [:Validity-attribute] :Validity)
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:Name :Name-text-node] :Name)
                       (mapping [:Address :Address-text-node] :Address)
                       (mapping [:Place :Place-text-node] :Place)
                       (mapping [:Country :Country-text-node] :Country)
                       (mapping [:PostCode :PostCode-text-node] :PostCode)
                       (mapping [:Latitude :Latitude-text-node] :Latitude)
                       (mapping [:Longitude :Longitude-text-node] :Longitude)))

(foreach [:glue:Domains :AdminDomain :Services :ComputingService]
         (add-association :PrimaryLocatedAtServiceLocation
                          (mapping [:Location] :Location)
                          (mapping [] :Service)))

(foreach [:glue:Domains :AdminDomain :Services :ComputingService :Contact]
         (add-instance :Contact
                       (mapping [:CreationTime-attribute] :CreationTime)
                       (mapping [:Validity-attribute] :Validity)
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                       (mapping [:Type :Type-text-node] :Type)
                       (mapping [:URL :URL-text-node] :Detail)))

(foreach [:glue:Domains :AdminDomain :Services :ComputingService]
         (add-association :HasContactServices
                          (mapping [:Contact] :Contact)
                          (mapping [] :Service)))

(foreach [:glue:Domains :AdminDomain :Services :ComputingService :ComputingEndpoint]
         (add-instance :Endpoint
                       (mapping [:CreationTime-attribute] :CreationTime)
                       (mapping [:Validity-attribute] :Validity)
                       (mapping [:ID :ID-text-node] :ID)
                       (mapping [:Name :Name-text-node] :Name)
                       (mapping [:URL :URL-text-node] :URL)
                       (mapping [:Technology :Technology-text-node] :Technology)
                       (mapping [:Interface :Interface-text-node] :InterfaceName)
                       (mapping [:InterfaceExtension :InterfaceExtension-text-node] :InterfaceExtension)
                       (mapping [:WSDL :WSDL-text-node] :WSDL)
                       (mapping [:SupportedProfile :SupportedProfile-text-node] :SupportedProfile)
                       (mapping [:Semantics :Semantics-text-node] :Semantics)
                       (mapping [:Implementor :Implementor-text-node] :Implementor)
                       (mapping [:ImplementationName :ImplementationName-text-node] :ImplementationName)
                       (mapping [:ImplementationVersion :ImplementationVersion-text-node] :ImplementationVersion)
                       (mapping [:QualityLevel :QualityLevel-text-node] :QualityLevel)
                       (mapping [:HealthState :HealthState-text-node] :HealthState)
                       (mapping [:HealthStateInfo :HealthStateInfo-text-node] :HealthStateInfo)
                       (mapping [:ServingState :ServingState-text-node] :ServingState)
                       (mapping [:StartTime :StartTime-text-node] :StartTime)
                       (mapping [:IssuerCA :IssuerCA-text-node] :IssuerCA)
                       (mapping [:DowntimeAnnounce :DowntimeAnnounce-text-node] :DowntimeAnnounce)
                       (mapping [:DowntimeStart :DowntimeStart-text-node] :DowntimeStart)
                       (mapping [:DowntimeEnd :DowntimeEnd-text-node] :DowntimeEnd)
                       (mapping [:DowntimeInfo :DowntimeInfo-text-node] :DowntimeInfo)))

(foreach [:glue:Domains :AdminDomain :Services :ComputingService]
         (add-association :ExposesServiceEndpoint
                          (mapping [:ComputingEndpoint] :Endpoint)
                          (mapping [] :Service)))

(foreach [:glue:Domains :AdminDomain :Services :ComputingService :ComputingEndpoint :AccessPolicy]
         (add-instance :AccessPolicy
                       (mapping [:CreationTime-attribute] :CreationTime)
                       (mapping [:Validity-attribute] :Validity)
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:Scheme :Scheme-text-node] :Scheme)
                       (mapping [:Rule-text-node :Rule] :Rule)))

(foreach [:glue:Domains :AdminDomain :Services :ComputingService :ComputingEndpoint]
         (add-association :CanAccessAccessPolicyEndpoint
                          (mapping [:AccessPolicy] :AccessPolicy)
                          (mapping [] :Endpoint)))

(foreach [:glue:Domains :AdminDomain :Services :ComputingService :ComputingShares :ComputingShare]
         (add-instance :Share
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:Name :Name-text-node] :Name)))

(foreach [:glue:Domains :AdminDomain :Services :ComputingService]
         (add-association :OffersServiceShare
                          (mapping [:ComputingShares :ComputingShare] :Share)
                          (mapping [] :Service)))

; !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! cannot map Type attribute only !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
;(foreach [:glue:Domains :AdminDomain :Services :ComputingService :ComputingActivitites :ComputingActivitiy]
;         (add-instance :Activitiy
;                       (mapping [:Type :Type-text-node] :Type)))

(foreach [:glue:Domains :AdminDomain :Services :ComputingService]
         (add-association :RelatesToServiceService
                          (mapping-pk [:Associations :ServiceID :ServiceID-text-node] :Service)
                          (mapping [] :ToService)))

(foreach [:glue:Domains :AdminDomain]
         (add-association :ParticipatesInAdminDomainAdminDomain
                          (mapping-pk [:Associations :AdminDomainID :AdminDomainID-text-node] :AdminDomain)
                          (mapping [] :ToAdminDomain)))

(foreach [:glue:Domains :UserDomain]
         (add-instance :UserDomain
                       (mapping [:ID :ID-text-node] :ID)
                       (mapping [:Name :Name-text-node] :Name)
                       (mapping [:Description :Description-text-node] :Description)
                       (mapping [:WWW :WWW-text-node] :WWW)
                       (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                       (mapping [:Level :Level-text-node] :Level)
                       (mapping [:UserManager :UserManager-text-node] :UserManager)
                       (mapping [:Member :Member-text-node] :Member)))

(foreach [:glue:Domains :UserDomain]
         (add-association :ParticipatesInUserDomainUserDomain
                          (mapping-pk [:Associations :UserDomainID :UserDomainID-text-node] :UserDomain)
                          (mapping [] :ToUserDomain)))
