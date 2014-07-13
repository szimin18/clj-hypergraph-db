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

(foreach (as :Location [:glue:Domains :AdminDomain :Location])
         (in (as :AdminDomain [:glue:Domains :AdminDomain])
             (add-association :PrimaryLocatedAtDomainLocation
                              (mapping :Location :Location)
                              (mapping :AdminDomain :Domain))))

(foreach [:glue:Domains :AdminDomain :Contact]
         (add-instance :Contact
                       (mapping [:CreationTime-attribute] :CreationTime)
                       (mapping [:Validity-attribute] :Validity)
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                       (mapping [:Type :Type-text-node] :Type)
                       (mapping [:URL :URL-text-node] :Detail)))

(foreach (as :Contact [:glue:Domains :AdminDomain :Contact])
         (in (as :AdminDomain [:glue:Domains :AdminDomain])
             (add-association :HasContactDomain
                              (mapping :Contact :Contact)
                              (mapping :AdminDomain :Domain))))

(foreach [:glue:Domains :AdminDomain :Services :Service]
         (add-instance :Service
                       (mapping [:ID :ID-text-node] :ID)
                       (mapping [:Name :Name-text-node] :Name)
                       (mapping [:Capability :Capability-text-node] :Capability)
                       (mapping [:Type :Type-text-node] :Type)
                       (mapping [:QualityLevel :QualityLevel-text-node] :QualityLevel)
                       (mapping [:Complexity :Complexity-text-node] :Complexity)))

(foreach (as :Service [:glue:Domains :AdminDomain :Services :Service])
         (in (as :AdminDomain [:glue:Domains :AdminDomain])
             (add-association :ManagesAdminDomainService
                              (mapping :Service :Service)
                              (mapping :AdminDomain :Domain))))

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

(foreach (as :Location [:glue:Domains :AdminDomain :Services :Service :Location])
         (in (as :Service [:glue:Domains :AdminDomain :Services :Service])
             (add-association :PrimaryLocatedAtServiceLocation
                              (mapping :Location :Location)
                              (mapping :Service :Service))))

(foreach [:glue:Domains :AdminDomain :Services :Service :Contact]
         (add-instance :Contact
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                       (mapping [:Type :Type-text-node] :Type)
                       (mapping [:URL :URL-text-node] :Detail)))

(foreach (as :Contact [:glue:Domains :AdminDomain :Services :Service :Contact])
         (in (as :Service [:glue:Domains :AdminDomain :Services :Service])
             (add-association :HasContactServices
                              (mapping :Contact :Contact)
                              (mapping :Service :Service))))

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

(foreach (as :Endpoint [:glue:Domains :AdminDomain :Services :Service :Endpoint])
         (in (as :Service [:glue:Domains :AdminDomain :Services :Service])
             (add-association :ExposesServiceEndpoint
                              (mapping :Endpoint :Endpoint)
                              (mapping :Service :Service))))

(foreach [:glue:Domains :AdminDomain :Services :Service :Endpoint :AccessPolicy]
         (add-instance :AccessPolicy
                       (mapping [:CreationTime-attribute] :CreationTime)
                       (mapping [:Validity-attribute] :Validity)
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:Scheme :Scheme-text-node] :Scheme)
                       (mapping [:Rule :Rule-text-node] :Rule)))

(foreach (as :AccessPolicy [:glue:Domains :AdminDomain :Services :Service :Endpoint :AccessPolicy])
         (in (as :Endpoint [:glue:Domains :AdminDomain :Services :Service :Endpoint])
             (add-association :CanAccessAccessPolicyEndpoint
                              (mapping :AccessPolicy :AccessPolicy)
                              (mapping :Endpoint :Endpoint))))

(foreach (as :Association [:glue:Domains :AdminDomain :Services :Service :Endpoint :Associations :ActivityID :ActivityID-text-node])
         (in (as :Endpoint [:glue:Domains :AdminDomain :Services :Service :Endpoint])
             (add-association :SubmittedByEndpointActivity
                              (mapping-pk :Association :Activity)
                              (mapping :Endpoint :Endpoint))))

(foreach [:glue:Domains :AdminDomain :Services :Service :Activities :Activity]
         (add-instance :Activity
                       (mapping [:ID :ID-text-node] :ID)))

(foreach (as :Association [:glue:Domains :AdminDomain :Services :Service :Activities :Activity :Associations :EndpointID :EndpointID-text-node])
         (in (as :Activity [:glue:Domains :AdminDomain :Services :Service :Activities :Activity])
             (add-association :SubmittedByEndpointActivity
                              (mapping-pk :Association :Endpoint)
                              (mapping :Activity :Activity))))

(foreach (as :Association [:glue:Domains :AdminDomain :Services :Service :Activities :Activity :Associations :UserDomainID :UserDomainID-text-node])
         (in (as :Activity [:glue:Domains :AdminDomain :Services :Service :Activities :Activity])
             (add-association :CreatesActivityUserDomain
                              (mapping-pk :Association :UserDomain)
                              (mapping :Activity :Activity))))

(foreach (as :Association [:glue:Domains :AdminDomain :Services :Service :Activities :Activity :Associations :ActivityID :ActivityID-text-node])
         (in (as :Activity [:glue:Domains :AdminDomain :Services :Service :Activities :Activity])
             (add-association :RelatesToActivityActivity
                              (mapping-pk :Association :Activity)
                              (mapping :Activity :ToActivity))))

(foreach (as :Association [:glue:Domains :AdminDomain :Services :Service :Associations :ServiceID :ServiceID-text-node])
         (in (as :Service [:glue:Domains :AdminDomain :Services :Service])
             (add-association :RelatesToServiceService
                              (mapping-pk :Association :Service)
                              (mapping :Service :ToService))))

(foreach [:glue:Domains :AdminDomain :Services :ComputingService]
         (add-instance :Service
                       (mapping [:CreationTime-attribute] :CreationTime)
                       (mapping [:Validity-attribute] :Validity)
                       (mapping [:ID :ID-text-node] :ID)
                       (mapping [:Name :Name-text-node] :Name)
                       (mapping [:Capability :Capability-text-node] :Capability)
                       (mapping [:Type :Type-text-node] :Type)
                       (mapping [:QualityLevel :QualityLevel-text-node] :QualityLevel)
                       (mapping [:StatusPage] :StatusInfo)
                       (mapping [:Complexity :Complexity-text-node] :Complexity)))

(foreach (as :ComputingService [:glue:Domains :AdminDomain :Services :ComputingService])
         (in (as :AdminDomain [:glue:Domains :AdminDomain])
             (add-association :ManagesAdminDomainService
                              (mapping :ComputingService :Service)
                              (mapping :AdminDomain :AdminDomain))))

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

(foreach (as :Location [:glue:Domains :AdminDomain :Services :ComputingService :Location])
         (in (as :ComputingService [:glue:Domains :AdminDomain :Services :ComputingService])
             (add-association :PrimaryLocatedAtServiceLocation
                              (mapping :Location :Location)
                              (mapping :ComputingService :Service))))

(foreach [:glue:Domains :AdminDomain :Services :ComputingService :Contact]
         (add-instance :Contact
                       (mapping [:CreationTime-attribute] :CreationTime)
                       (mapping [:Validity-attribute] :Validity)
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                       (mapping [:Type :Type-text-node] :Type)
                       (mapping [:URL :URL-text-node] :Detail)))

(foreach (as :Contact [:glue:Domains :AdminDomain :Services :ComputingService :Contact])
         (in (as :ComputingService [:glue:Domains :AdminDomain :Services :ComputingService])
             (add-association :HasContactServices
                              (mapping :Contact :Contact)
                              (mapping :ComputingService :Service))))

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

(foreach (as :Endpoint [:glue:Domains :AdminDomain :Services :ComputingService :ComputingEndpoint])
         (in (as :ComputingService [:glue:Domains :AdminDomain :Services :ComputingService])
             (add-association :ExposesServiceEndpoint
                              (mapping :Endpoint :Endpoint)
                              (mapping :ComputingService :Service))))

(foreach [:glue:Domains :AdminDomain :Services :ComputingService :ComputingEndpoint :AccessPolicy]
         (add-instance :AccessPolicy
                       (mapping [:CreationTime-attribute] :CreationTime)
                       (mapping [:Validity-attribute] :Validity)
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:Scheme :Scheme-text-node] :Scheme)
                       (mapping [:Rule-text-node :Rule] :Rule)))

(foreach (as :AccessPolicy [:glue:Domains :AdminDomain :Services :ComputingService :ComputingEndpoint :AccessPolicy])
         (in (as :ComputingEndpoint [:glue:Domains :AdminDomain :Services :ComputingService :ComputingEndpoint])
             (add-association :CanAccessAccessPolicyEndpoint
                              (mapping :AccessPolicy :AccessPolicy)
                              (mapping :ComputingEndpoint :Endpoint))))

(foreach [:glue:Domains :AdminDomain :Services :ComputingService :ComputingShares :ComputingShare]
         (add-instance :Share
                       (mapping [:LocalID :LocalID-text-node] :ID)
                       (mapping [:Name :Name-text-node] :Name)))

(foreach (as :ComputingShare [:glue:Domains :AdminDomain :Services :ComputingService :ComputingShares :ComputingShare])
         (in (as :ComputingService [:glue:Domains :AdminDomain :Services :ComputingService])
             (add-association :OffersServiceShare
                              (mapping :ComputingShare :Share)
                              (mapping :ComputingService :Service))))

; !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! cannot map Type attribute only !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
;(foreach [:glue:Domains :AdminDomain :Services :ComputingService :ComputingActivitites :ComputingActivitiy]
;         (add-instance :Activitiy
;                       (mapping [:Type :Type-text-node] :Type)))

(foreach (as :Association [:glue:Domains :AdminDomain :Services :ComputingService :Associations :ServiceID :ServiceID-text-node])
         (in (as :ComputingService [:glue:Domains :AdminDomain :Services :ComputingService])
             (add-association :RelatesToServiceService
                              (mapping-pk :Association :Service)
                              (mapping :ComputingService :ToService))))

(foreach (as :Association [:glue:Domains :AdminDomain :Associations :AdminDomainID :AdminDomainID-text-node])
         (in (as :AdminDomain [:glue:Domains :AdminDomain])
             (add-association :ParticipatesInAdminDomainAdminDomain
                              (mapping-pk :Association :AdminDomain)
                              (mapping :AdminDomain :ToAdminDomain))))

(foreach [:glue:Domains :UserDomain]
         (add-instance :UserDomain
                       (mapping [:ID :ID-text-node :ID] :ID)
                       (mapping [:Name :Name-text-node] :Name)
                       (mapping [:Description :Description-text-node] :Description)
                       (mapping [:WWW :WWW-text-node] :WWW)
                       (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                       (mapping [:Level :Level-text-node] :Level)
                       (mapping [:UserManager :UserManager-text-node] :UserManager)
                       (mapping [:Member :Member-text-node] :Member)))

(foreach (as :Association [:glue:Domains :UserDomain :Associations :UserDomainID :UserDomainID-text-node])
         (in (as :UserDomain [:glue:Domains :UserDomain])
             (add-association :ParticipatesInUserDomainUserDomain
                              (mapping-pk :Association :UserDomain)
                              (mapping :UserDomain :ToUserDomain))))
