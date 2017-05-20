
(for-each [:glue:Domains :AdminDomain]
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

(for-each [:glue:Domains :AdminDomain :Extensions :Extension]
         (add-instance :Extension
                       (mapping [:Key-attribute] :Key)
                       (mapping [:Extension-text-node] :Value))
         (add-association :HasExtentsionEntity
                          (mapping [] :Extension)
                          (mapping [:.. :..] :Entity)))

(for-each [:glue:Domains :AdminDomain :Location]
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
                       (mapping [:Longitude :Longitude-text-node] :Longitude))
         (add-association :PrimaryLocatedAtDomainLocation
                          (mapping [] :Location)
                          (mapping [:..] :Domain)))

(for-each [:glue:Domains :AdminDomain :Location :Extensions :Extension]
         (add-instance :Extension
                       (mapping [:Key-attribute] :Key)
                       (mapping [:Extension-text-node] :Value))
         (add-association :HasExtentsionEntity
                          (mapping [] :Extension)
                          (mapping [:.. :..] :Entity)))

(for-each [:glue:Domains :AdminDomain :Contact]
         (add-instance :Contact
                       (mapping [:CreationTime-attribute] :CreationTime)
                       (mapping [:Validity-attribute] :Validity)
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                       (mapping [:Type :Type-text-node] :Type)
                       (mapping [:URL :URL-text-node] :Detail))
         (add-association :HasContactDomain
                          (mapping [] :Contact)
                          (mapping [:..] :Domain)))

(for-each [:glue:Domains :AdminDomain :Contact :Extensions :Extension]
         (add-instance :Extension
                       (mapping [:Key-attribute] :Key)
                       (mapping [:Extension-text-node] :Value))
         (add-association :HasExtentsionEntity
                          (mapping [] :Extension)
                          (mapping [:.. :..] :Entity)))

(for-each [:glue:Domains :AdminDomain :Services :Service]
         (add-instance :Service
                       (mapping [:ID :ID-text-node] :ID)
                       (mapping [:Name :Name-text-node] :Name)
                       (mapping [:Capability :Capability-text-node] :Capability)
                       (mapping [:Type :Type-text-node] :Type)
                       (mapping [:QualityLevel :QualityLevel-text-node] :QualityLevel)
                       (mapping [:Complexity :Complexity-text-node] :Complexity))
         (add-association :ManagesAdminDomainService
                          (mapping [] :Service)
                          (mapping [:.. :..] :AdminDomain)))

(for-each [:glue:Domains :AdminDomain :Services :Service :Extensions :Extension]
         (add-instance :Extension
                       (mapping [:Key-attribute] :Key)
                       (mapping [:Extension-text-node] :Value))
         (add-association :HasExtentsionEntity
                          (mapping [] :Extension)
                          (mapping [:.. :..] :Entity)))

(for-each [:glue:Domains :AdminDomain :Services :Service :Location]
         (add-instance :Location
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:Name :Name-text-node] :Name)
                       (mapping [:Address :Address-text-node] :Address)
                       (mapping [:Place :Place-text-node] :Place)
                       (mapping [:Country :Country-text-node] :Country)
                       (mapping [:PostCode :PostCode-text-node] :PostCode)
                       (mapping [:Latitude :Latitude-text-node] :Latitude)
                       (mapping [:Longitude :Longitude-text-node] :Longitude))
         (add-association :PrimaryLocatedAtServiceLocation
                          (mapping [] :Location)
                          (mapping [:..] :Service)))

(for-each [:glue:Domains :AdminDomain :Services :Service :Location :Extensions :Extension]
         (add-instance :Extension
                       (mapping [:Key-attribute] :Key)
                       (mapping [:Extension-text-node] :Value))
         (add-association :HasExtentsionEntity
                          (mapping [] :Extension)
                          (mapping [:.. :..] :Entity)))

(for-each [:glue:Domains :AdminDomain :Services :Service :Contact]
         (add-instance :Contact
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                       (mapping [:Type :Type-text-node] :Type)
                       (mapping [:URL :URL-text-node] :Detail))
         (add-association :HasContactServices
                          (mapping [] :Contact)
                          (mapping [:..] :Service)))

(for-each [:glue:Domains :AdminDomain :Services :Service :Contact :Extensions :Extension]
         (add-instance :Extension
                       (mapping [:Key-attribute] :Key)
                       (mapping [:Extension-text-node] :Value))
         (add-association :HasExtentsionEntity
                          (mapping [] :Extension)
                          (mapping [:.. :..] :Entity)))

(for-each [:glue:Domains :AdminDomain :Services :Service :Endpoint]
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
                       (mapping [:DowntimeInfo :DowntimeInfo-text-node] :DowntimeInfo))
         (add-association :ExposesServiceEndpoint
                          (mapping [] :Endpoint)
                          (mapping [:..] :Service)))

(for-each [:glue:Domains :AdminDomain :Services :Service :Endpoint :Extensions :Extension]
         (add-instance :Extension
                       (mapping [:Key-attribute] :Key)
                       (mapping [:Extension-text-node] :Value))
         (add-association :HasExtentsionEntity
                          (mapping [] :Extension)
                          (mapping [:.. :..] :Entity)))

(for-each [:glue:Domains :AdminDomain :Services :Service :Endpoint :AccessPolicy]
         (add-instance :AccessPolicy
                       (mapping [:CreationTime-attribute] :CreationTime)
                       (mapping [:Validity-attribute] :Validity)
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:Scheme :Scheme-text-node] :Scheme)
                       (mapping [:Rule :Rule-text-node] :Rule))
         (add-association :CanAccessAccessPolicyEndpoint
                          (mapping [] :AccessPolicy)
                          (mapping [:..] :Endpoint)))

(for-each [:glue:Domains :AdminDomain :Services :Service :Endpoint :Associations :ActivityID]
         (add-association :SubmittedByEndpointActivity
                          (mapping-pk [:ActivityID-text-node] :Activity)
                          (mapping [:.. :..] :Endpoint)))

(for-each [:glue:Domains :AdminDomain :Services :Service :Activities :Activity]
         (add-instance :Activity
                       (mapping [:ID :ID-text-node] :ID)))

(for-each [:glue:Domains :AdminDomain :Services :Service :Activities :Activity :Associations :EndpointID]
         (add-association :SubmittedByEndpointActivity
                          (mapping-pk [:EndpointID-text-node] :Endpoint)
                          (mapping [:.. :..] :Activity)))

(for-each [:glue:Domains :AdminDomain :Services :Service :Activities :Activity :Associations :UserDomainID]
         (add-association :CreatesActivityUserDomain
                          (mapping-pk [:UserDomainID-text-node] :UserDomain)
                          (mapping [:.. :..] :Activity)))

(for-each [:glue:Domains :AdminDomain :Services :Service :Activities :Activity :Associations :ActivityID]
         (add-association :RelatesToActivityActivity
                          (mapping-pk [:ActivityID-text-node] :Activity)
                          (mapping [:.. :..] :ToActivity)))

(for-each [:glue:Domains :AdminDomain :Services :Service :Associations :ServiceID]
         (add-association :RelatesToServiceService
                          (mapping-pk [:ServiceID-text-node] :Service)
                          (mapping [:.. :..] :ToService)))

(for-each [:glue:Domains :AdminDomain :Services :ComputingService]
         (add-instance :Service
                       (mapping [:CreationTime-attribute] :CreationTime)
                       (mapping [:Validity-attribute] :Validity)
                       (mapping [:ID :ID-text-node] :ID)
                       (mapping [:Name :Name-text-node] :Name)
                       (mapping [:Capability :Capability-text-node] :Capability)
                       (mapping [:Type :Type-text-node] :Type)
                       (mapping [:QualityLevel :QualityLevel-text-node] :QualityLevel)
                       (mapping [:StatusPage :StatusPage-text-node] :StatusInfo)
                       (mapping [:Complexity :Complexity-text-node] :Complexity))
         (add-association :ManagesAdminDomainService
                          (mapping [] :Service)
                          (mapping [:.. :..] :AdminDomain)))

(for-each [:glue:Domains :AdminDomain :Services :ComputingService :Extensions :Extension]
         (add-instance :Extension
                       (mapping [:Key-attribute] :Key)
                       (mapping [:Extension-text-node] :Value))
         (add-association :HasExtentsionEntity
                          (mapping [] :Extension)
                          (mapping [:.. :..] :Entity)))

(for-each [:glue:Domains :AdminDomain :Services :ComputingService :Location]
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
                       (mapping [:Longitude :Longitude-text-node] :Longitude))
         (add-association :PrimaryLocatedAtServiceLocation
                          (mapping [] :Location)
                          (mapping [:..] :Service)))

(for-each [:glue:Domains :AdminDomain :Services :ComputingService :Contact]
         (add-instance :Contact
                       (mapping [:CreationTime-attribute] :CreationTime)
                       (mapping [:Validity-attribute] :Validity)
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                       (mapping [:Type :Type-text-node] :Type)
                       (mapping [:URL :URL-text-node] :Detail))
         (add-association :HasContactServices
                          (mapping [] :Contact)
                          (mapping [:..] :Service)))

(for-each [:glue:Domains :AdminDomain :Services :ComputingService :ComputingEndpoint]
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
                       (mapping [:DowntimeInfo :DowntimeInfo-text-node] :DowntimeInfo))
         (add-association :ExposesServiceEndpoint
                          (mapping [] :Endpoint)
                          (mapping [:..] :Service)))

(for-each [:glue:Domains :AdminDomain :Services :ComputingService :ComputingEndpoint :Extensions :Extension]
         (add-instance :Extension
                       (mapping [:Key-attribute] :Key)
                       (mapping [:Extension-text-node] :Value))
         (add-association :HasExtentsionEntity
                          (mapping [] :Extension)
                          (mapping [:.. :..] :Entity)))

(for-each [:glue:Domains :AdminDomain :Services :ComputingService :ComputingEndpoint :AccessPolicy]
         (add-instance :AccessPolicy
                       (mapping [:CreationTime-attribute] :CreationTime)
                       (mapping [:Validity-attribute] :Validity)
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:Scheme :Scheme-text-node] :Scheme)
                       (mapping [:Rule :Rule-text-node] :Rule))
         (add-association :CanAccessAccessPolicyEndpoint
                          (mapping [] :AccessPolicy)
                          (mapping [:..] :Endpoint)))

(for-each [:glue:Domains :AdminDomain :Services :ComputingService :ComputingShares :ComputingShare]
         (add-instance :Share
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:Name :Name-text-node] :Name))
         (add-association :OffersServiceShare
                          (mapping [] :Share)
                          (mapping [:.. :..] :Service)))

(for-each [:glue:Domains :AdminDomain :Services :ComputingService :ComputingShares :ComputingShare :Extensions :Extension]
         (add-instance :Extension
                       (mapping [:Key-attribute] :Key)
                       (mapping [:Extension-text-node] :Value))
         (add-association :HasExtentsionEntity
                          (mapping [] :Extension)
                          (mapping [:.. :..] :Entity)))

; !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! cannot map Type attribute only !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
;(for-each [:glue:Domains :AdminDomain :Services :ComputingService :ComputingActivitites :ComputingActivitiy]
;         (add-instance :Activitiy
;                       (mapping [:Type :Type-text-node] :Type)))

(for-each [:glue:Domains :AdminDomain :Services :ComputingService :Associations :ServiceID]
         (add-association :RelatesToServiceService
                          (mapping-pk [:ServiceID-text-node] :Service)
                          (mapping [:.. :..] :ToService)))

(for-each [:glue:Domains :AdminDomain :Associations :AdminDomainID]
         (add-association :ParticipatesInAdminDomainAdminDomain
                          (mapping-pk [:AdminDomainID-text-node] :AdminDomain)
                          (mapping [:.. :..] :ToAdminDomain)))

(for-each [:glue:Domains :UserDomain]
         (add-instance :UserDomain
                       (mapping [:ID :ID-text-node] :ID)
                       (mapping [:Name :Name-text-node] :Name)
                       (mapping [:Description :Description-text-node] :Description)
                       (mapping [:WWW :WWW-text-node] :WWW)
                       (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                       (mapping [:Level :Level-text-node] :Level)
                       (mapping [:UserManager :UserManager-text-node] :UserManager)
                       (mapping [:Member :Member-text-node] :Member)))

(for-each [:glue:Domains :UserDomain :Associations :UserDomainID]
         (add-association :ParticipatesInUserDomainUserDomain
                          (mapping-pk [:UserDomainID-text-node] :UserDomain)
                          (mapping [:.. :..] :ToUserDomain)))

(for-each [:glue:Domains :UserDomain :Extensions :Extension]
         (add-instance :Extension
                       (mapping [:Key-attribute] :Key)
                       (mapping [:Extension-text-node] :Value))
         (add-association :HasExtentsionEntity
                          (mapping [] :Extension)
                          (mapping [:.. :..] :Entity)))