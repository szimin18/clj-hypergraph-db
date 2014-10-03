(foreach :AdminDomain
         (add-token [:glue:Domains :AdminDomain]
                    (mapping :CreationTime [:CreationTime-attribute])
                    (mapping :Validity [:Validity-attribute])
                    (mapping :Distributed [:Distributed :Distributed-text-node])
                    (mapping :Owner [:Owner :Owner-text-node])
                    (mapping :WWW [:WWW :WWW-text-node])
                    (mapping :Description [:Description :Description-text-node])
                    (mapping :ID [:ID :ID-text-node])
                    (mapping :Name [:Name :Name-text-node])
                    (mapping :OtherInfo [:OtherInfo :OtherInfo-text-node])))

;(foreach [:glue:Domains :AdminDomain]
;         (add-instance :AdminDomain
;                       (mapping [:CreationTime-attribute] :CreationTime)
;                       (mapping [:Validity-attribute] :Validity)
;                       (mapping [:Distributed :Distributed-text-node] :Distributed)
;                       (mapping [:Owner :Owner-text-node] :Owner)
;                       (mapping [:WWW :WWW-text-node] :WWW)
;                       (mapping [:Description :Description-text-node] :Description)
;                       (mapping [:ID :ID-text-node] :ID)
;                       (mapping [:Name :Name-text-node] :Name)
;                       (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)))

(foreach :Extension
         (associated-with [:glue:Domains :AdminDomain] :Entity :HasExtentsionEntity :Extension
                          (add-token [:Extensions :Extension]
                                     (mapping :Key [:Key-attribute])
                                     (mapping :Value [:Extension-text-node]))))

;(foreach [:glue:Domains :AdminDomain :Extensions :Extension]
;         (add-instance :Extension
;                       (mapping [:Key-attribute] :Key)
;                       (mapping [:Extension-text-node] :Value))
;         (add-association :HasExtentsionEntity
;                          (mapping [] :Extension)
;                          (mapping [:.. :..] :Entity)))

(foreach :Location
         (associated-with [:glue:Domains :AdminDomain] :Domain :PrimaryLocatedAtDomainLocation :Location
                          (add-token [:Location]
                                     (mapping :CreationTime [:CreationTime-attribute])
                                     (mapping :Validity [:Validity-attribute])
                                     (mapping :ID [:LocalID :LocalID-text-node])
                                     (mapping :Name [:Name :Name-text-node])
                                     (mapping :Address [:Address :Address-text-node])
                                     (mapping :Place [:Place :Place-text-node])
                                     (mapping :Country [:Country :Country-text-node])
                                     (mapping :PostCode [:PostCode :PostCode-text-node])
                                     (mapping :Latitude [:Latitude :Latitude-text-node])
                                     (mapping :Longitude [:Longitude :Longitude-text-node]))))

;(foreach [:glue:Domains :AdminDomain :Location]
;         (add-instance :Location
;                       (mapping [:CreationTime-attribute] :CreationTime)
;                       (mapping [:Validity-attribute] :Validity)
;                       (mapping [:LocalID :LocalID-text-node] :ID)
;                       (mapping [:Name :Name-text-node] :Name)
;                       (mapping [:Address :Address-text-node] :Address)
;                       (mapping [:Place :Place-text-node] :Place)
;                       (mapping [:Country :Country-text-node] :Country)
;                       (mapping [:PostCode :PostCode-text-node] :PostCode)
;                       (mapping [:Latitude :Latitude-text-node] :Latitude)
;                       (mapping [:Longitude :Longitude-text-node] :Longitude)))
;
;(foreach [:glue:Domains :AdminDomain]
;         (add-association :PrimaryLocatedAtDomainLocation
;                          (mapping [:Location] :Location)
;                          (mapping [] :Domain)))

(foreach :Extension
         (associated-with [:glue:Domains :AdminDomain :Location] :Entity :HasExtentsionEntity :Extension
                          (add-token [:Extensions :Extension]
                                     (mapping :Key [:Key-attribute])
                                     (mapping :Value [:Extension-text-node]))))

;(foreach [:glue:Domains :AdminDomain :Location :Extensions :Extension]
;         (add-instance :Extension
;                       (mapping [:Key-attribute] :Key)
;                       (mapping [:Extension-text-node] :Value))
;         (add-association :HasExtentsionEntity
;                          (mapping [] :Extension)
;                          (mapping [:.. :..] :Entity)))

(foreach :Contact
         (associated-with [:glue:Domains :AdminDomain] :Domain :HasContactDomain :Contact
                          (add-token [:Contact]
                                     (mapping :CreationTime [:CreationTime-attribute])
                                     (mapping :Validity [:Validity-attribute])
                                     (mapping :ID [:LocalID :LocalID-text-node])
                                     (mapping :OtherInfo [:OtherInfo :OtherInfo-text-node])
                                     (mapping :Type [:Type :Type-text-node])
                                     (mapping :Detail [:URL :URL-text-node]))))

;(foreach [:glue:Domains :AdminDomain :Contact]
;         (add-instance :Contact
;                       (mapping [:CreationTime-attribute] :CreationTime)
;                       (mapping [:Validity-attribute] :Validity)
;                       (mapping [:LocalID :LocalID-text-node] :ID)
;                       (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
;                       (mapping [:Type :Type-text-node] :Type)
;                       (mapping [:URL :URL-text-node] :Detail)))
;
;(foreach [:glue:Domains :AdminDomain]
;         (add-association :HasContactDomain
;                          (mapping [:Contact] :Contact)
;                          (mapping [] :Domain)))

(foreach :Extension
         (associated-with [:glue:Domains :AdminDomain :Contact] :Entity :HasExtentsionEntity :Extension
                          (add-token [:Extensions :Extension]
                                     (mapping :Key [:Key-attribute])
                                     (mapping :Value [:Extension-text-node]))))

;(foreach [:glue:Domains :AdminDomain :Contact :Extensions :Extension]
;         (add-instance :Extension
;                       (mapping [:Key-attribute] :Key)
;                       (mapping [:Extension-text-node] :Value))
;         (add-association :HasExtentsionEntity
;                          (mapping [] :Extension)
;                          (mapping [:.. :..] :Entity)))

;;;;;; no need to add it we have more extended :ComputingServices
;(foreach :Service
;         (associated-with [:glue:Domains :AdminDomain] :AdminDomain :ManagesAdminDomainService :Service
;                        (add-token [:Services :Service]
;                                   (mapping :ID [:ID :ID-text-node])
;                                   (mapping :Name [:Name :Name-text-node])
;                                   (mapping :Capability [:Capability :Capability-text-node])
;                                   (mapping :Type [:Type :Type-text-node])
;                                   (mapping :QualityLevel [:QualityLevel :QualityLevel-text-node])
;                                   (mapping :Complexity [:Complexity :Complexity-text-node]))))
;
;;(foreach [:glue:Domains :AdminDomain :Services :Service]
;;         (add-instance :Service
;;                       (mapping [:ID :ID-text-node] :ID)
;;                       (mapping [:Name :Name-text-node] :Name)
;;                       (mapping [:Capability :Capability-text-node] :Capability)
;;                       (mapping [:Type :Type-text-node] :Type)
;;                       (mapping [:QualityLevel :QualityLevel-text-node] :QualityLevel)
;;                       (mapping [:Complexity :Complexity-text-node] :Complexity)))
;;
;;(foreach [:glue:Domains :AdminDomain]
;;         (add-association :ManagesAdminDomainService
;;                          (mapping [:Services :Service] :Service)
;;                          (mapping [] :AdminDomain)))
;
;(foreach :Location
;         (associated-with [:glue:Domains :AdminDomain :Services :Service] :Service :PrimaryLocatedAtServiceLocation :Location
;                        (add-token [:Location]
;                                   (mapping :ID [:LocalID :LocalID-text-node])
;                                   (mapping :Name [:Name :Name-text-node])
;                                   (mapping :Address [:Address :Address-text-node])
;                                   (mapping :Place [:Place :Place-text-node])
;                                   (mapping :Country [:Country :Country-text-node])
;                                   (mapping :PostCode [:PostCode :PostCode-text-node])
;                                   (mapping :Latitude [:Latitude :Latitude-text-node])
;                                   (mapping :Longitude [:Longitude :Longitude-text-node]))))
;
;;(foreach [:glue:Domains :AdminDomain :Services :Service :Location]
;;         (add-instance :Location
;;                       (mapping [:LocalID :LocalID-text-node] :ID)
;;                       (mapping [:Name :Name-text-node] :Name)
;;                       (mapping [:Address :Address-text-node] :Address)
;;                       (mapping [:Place :Place-text-node] :Place)
;;                       (mapping [:Country :Country-text-node] :Country)
;;                       (mapping [:PostCode :PostCode-text-node] :PostCode)
;;                       (mapping [:Latitude :Latitude-text-node] :Latitude)
;;                       (mapping [:Longitude :Longitude-text-node] :Longitude)))
;;
;;(foreach [:glue:Domains :AdminDomain :Services :Service]
;;         (add-association :PrimaryLocatedAtServiceLocation
;;                          (mapping [:Location] :Location)
;;                          (mapping [] :Service)))
;
;(foreach :Contact
;         (associated-with [:glue:Domains :AdminDomain :Services :Service] :Service :HasContactServices :Contact
;                        (add-token [:Contact]
;                                   (mapping :ID [:LocalID :LocalID-text-node])
;                                   (mapping :OtherInfo [:OtherInfo :OtherInfo-text-node])
;                                   (mapping :Type [:Type :Type-text-node])
;                                   (mapping :Detail [:URL :URL-text-node]))))
;
;;(foreach [:glue:Domains :AdminDomain :Services :Service :Contact]
;;         (add-instance :Contact
;;                       (mapping [:LocalID :LocalID-text-node] :ID)
;;                       (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
;;                       (mapping [:Type :Type-text-node] :Type)
;;                       (mapping [:URL :URL-text-node] :Detail)))
;;
;;(foreach [:glue:Domains :AdminDomain :Services :Service]
;;         (add-association :HasContactServices
;;                          (mapping [:Contact] :Contact)
;;                          (mapping [] :Service)))
;
;(foreach :Endpoint
;         (associated-with [:glue:Domains :AdminDomain :Services :Service] :Service :ExposesServiceEndpoint :Endpoint
;                        (add-token [:Endpoint]
;                                   (mapping :ID [:ID :ID-text-node])
;                                   (mapping :Name [:Name :Name-text-node])
;                                   (mapping :URL [:URL :URL-text-node])
;                                   (mapping :Capability [:Capability :Capability-text-node])
;                                   (mapping :Technology [:Technology :Technology-text-node])
;                                   (mapping :InterfaceName [:Interface :Interface-text-node])
;                                   (mapping :InterfaceExtension [:InterfaceExtension :InterfaceExtension-text-node])
;                                   (mapping :WSDL [:WSDL :WSDL-text-node])
;                                   (mapping :SupportedProfile [:SupportedProfile :SupportedProfile-text-node])
;                                   (mapping :Semantics [:Semantics :Semantics-text-node])
;                                   (mapping :Implementor [:Implementor :Implementor-text-node])
;                                   (mapping :ImplementationName [:ImplementationName :ImplementationName-text-node])
;                                   (mapping :ImplementationVersion [:ImplementationVersion :ImplementationVersion-text-node])
;                                   (mapping :QualityLevel [:QualityLevel :QualityLevel-text-node])
;                                   (mapping :HealthState [:HealthState :HealthState-text-node])
;                                   (mapping :HealthStateInfo [:HealthStateInfo :HealthStateInfo-text-node])
;                                   (mapping :ServingState [:ServingState :ServingState-text-node])
;                                   (mapping :StartTime [:StartTime :StartTime-text-node])
;                                   (mapping :IssuerCA [:IssuerCA :IssuerCA-text-node])
;                                   (mapping :TrustedCA [:TrustedCA :TrustedCA-text-node])
;                                   (mapping :DowntimeAnnounce [:DowntimeAnnounce :DowntimeAnnounce-text-node])
;                                   (mapping :DowntimeStart [:DowntimeStart :DowntimeStart-text-node])
;                                   (mapping :DowntimeEnd [:DowntimeEnd :DowntimeEnd-text-node])
;                                   (mapping :DowntimeInfo [:DowntimeInfo :DowntimeInfo-text-node]))))
;
;;(foreach [:glue:Domains :AdminDomain :Services :Service :Endpoint]
;;         (add-instance :Endpoint
;;                       (mapping [:ID :ID-text-node] :ID)
;;                       (mapping [:Name :Name-text-node] :Name)
;;                       (mapping [:URL :URL-text-node] :URL)
;;                       (mapping [:Capability :Capability-text-node] :Capability)
;;                       (mapping [:Technology :Technology-text-node] :Technology)
;;                       (mapping [:Interface :Interface-text-node] :InterfaceName)
;;                       (mapping [:InterfaceExtension :InterfaceExtension-text-node] :InterfaceExtension)
;;                       (mapping [:WSDL :WSDL-text-node] :WSDL)
;;                       (mapping [:SupportedProfile :SupportedProfile-text-node] :SupportedProfile)
;;                       (mapping [:Semantics :Semantics-text-node] :Semantics)
;;                       (mapping [:Implementor :Implementor-text-node] :Implementor)
;;                       (mapping [:ImplementationName :ImplementationName-text-node] :ImplementationName)
;;                       (mapping [:ImplementationVersion :ImplementationVersion-text-node] :ImplementationVersion)
;;                       (mapping [:QualityLevel :QualityLevel-text-node] :QualityLevel)
;;                       (mapping [:HealthState :HealthState-text-node] :HealthState)
;;                       (mapping [:HealthStateInfo :HealthStateInfo-text-node] :HealthStateInfo)
;;                       (mapping [:ServingState :ServingState-text-node] :ServingState)
;;                       (mapping [:StartTime :StartTime-text-node] :StartTime)
;;                       (mapping [:IssuerCA :IssuerCA-text-node] :IssuerCA)
;;                       (mapping [:TrustedCA :TrustedCA-text-node] :TrustedCA)
;;                       (mapping [:DowntimeAnnounce :DowntimeAnnounce-text-node] :DowntimeAnnounce)
;;                       (mapping [:DowntimeStart :DowntimeStart-text-node] :DowntimeStart)
;;                       (mapping [:DowntimeEnd :DowntimeEnd-text-node] :DowntimeEnd)
;;                       (mapping [:DowntimeInfo :DowntimeInfo-text-node] :DowntimeInfo)))
;;
;;(foreach [:glue:Domains :AdminDomain :Services :Service]
;;         (add-association :ExposesServiceEndpoint
;;                          (mapping [:Endpoint] :Endpoint)
;;                          (mapping [] :Service)))
;
;(foreach :AccessPolicy
;         (associated-with [:glue:Domains :AdminDomain :Services :Service :Endpoint] :Endpoint :CanAccessAccessPolicyEndpoint :AccessPolicy
;                          (add-token [:AccessPolicy]
;                                     (mapping :CreationTime [:CreationTime-attribute])
;                                     (mapping :Validity [:Validity-attribute])
;                                     (mapping :ID [:LocalID :LocalID-text-node])
;                                     (mapping :Scheme [:Scheme :Scheme-text-node])
;                                     (mapping :Rule [:Rule :Rule-text-node]))))
;
;;(foreach [:glue:Domains :AdminDomain :Services :Service :Endpoint :AccessPolicy]
;;         (add-instance :AccessPolicy
;;                       (mapping [:CreationTime-attribute] :CreationTime)
;;                       (mapping [:Validity-attribute] :Validity)
;;                       (mapping [:LocalID :LocalID-text-node] :ID)
;;                       (mapping [:Scheme :Scheme-text-node] :Scheme)
;;                       (mapping [:Rule :Rule-text-node] :Rule)))
;;
;;(foreach [:glue:Domains :AdminDomain :Services :Service :Endpoint]
;;         (add-association :CanAccessAccessPolicyEndpoint
;;                          (mapping [:AccessPolicy] :AccessPolicy)
;;                          (mapping [] :Endpoint)))
;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;(foreach [:glue:Domains :AdminDomain :Services :Service :Endpoint]
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;         (add-association :SubmittedByEndpointActivity
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;                          (mapping-pk [:Associations :ActivityID :ActivityID-text-node] :Activity)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;                          (mapping [] :Endpoint)))
;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;(foreach [:glue:Domains :AdminDomain :Services :Service :Activities :Activity]
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;         (add-instance :Activity
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;                       (mapping [:ID :ID-text-node] :ID)))
;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;(foreach [:glue:Domains :AdminDomain :Services :Service :Activities :Activity]
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;         (add-association :SubmittedByEndpointActivity
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;                          (mapping-pk [:Associations :EndpointID :EndpointID-text-node] :Endpoint)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;                          (mapping [] :Activity)))
;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;(foreach [:glue:Domains :AdminDomain :Services :Service :Activities :Activity]
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;         (add-association :CreatesActivityUserDomain
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;                          (mapping-pk [:Associations :UserDomainID :UserDomainID-text-node] :UserDomain)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;                          (mapping [] :Activity)))
;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;(foreach [:glue:Domains :AdminDomain :Services :Service :Activities :Activity]
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;         (add-association :RelatesToActivityActivity
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;                          (mapping-pk [:Associations :ActivityID :ActivityID-text-node] :Activity)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;                          (mapping [] :ToActivity)))
;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;(foreach [:glue:Domains :AdminDomain :Services :Service]
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;         (add-association :RelatesToServiceService
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;                          (mapping-pk [:Associations :ServiceID :ServiceID-text-node] :Service)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;                          (mapping [] :ToService)))

(foreach :Service
         (associated-with [:glue:Domains :AdminDomain] :AdminDomain :ManagesAdminDomainService :Service
                          (add-token [:Services :ComputingService]
                                     (mapping :CreationTime [:CreationTime-attribute])
                                     (mapping :Validity [:Validity-attribute])
                                     (mapping :ID [:ID :ID-text-node])
                                     (mapping :Name [:Name :Name-text-node])
                                     (mapping :Capability [:Capability :Capability-text-node])
                                     (mapping :Type [:Type :Type-text-node])
                                     (mapping :QualityLevel [:QualityLevel :QualityLevel-text-node])
                                     (mapping :StatusInfo [:StatusPage :StatusPage-text-node])
                                     (mapping :Complexity [:Complexity :Complexity-text-node]))))

;(foreach [:glue:Domains :AdminDomain :Services :ComputingService]
;         (add-instance :Service
;                       (mapping [:CreationTime-attribute] :CreationTime)
;                       (mapping [:Validity-attribute] :Validity)
;                       (mapping [:ID :ID-text-node] :ID)
;                       (mapping [:Name :Name-text-node] :Name)
;                       (mapping [:Capability :Capability-text-node] :Capability)
;                       (mapping [:Type :Type-text-node] :Type)
;                       (mapping [:QualityLevel :QualityLevel-text-node] :QualityLevel)
;                       (mapping [:StatusPage] :StatusInfo)
;                       (mapping [:Complexity :Complexity-text-node] :Complexity)))
;
;(foreach [:glue:Domains :AdminDomain]
;         (add-association :ManagesAdminDomainService
;                          (mapping [:Services :ComputingService] :Service)
;                          (mapping [] :AdminDomain)))

(foreach :Extension
         (associated-with [:glue:Domains :AdminDomain :Services :ComputingService] :Entity :HasExtentsionEntity :Extension
                          (add-token [:Extensions :Extension]
                                     (mapping :Key [:Key-attribute])
                                     (mapping :Value [:Extension-text-node]))))

;(foreach [:glue:Domains :AdminDomain :Services :ComputingService :Extensions :Extension]
;         (add-instance :Extension
;                       (mapping [:Key-attribute] :Key)
;                       (mapping [:Extension-text-node] :Value))
;         (add-association :HasExtentsionEntity
;                          (mapping [] :Extension)
;                          (mapping [:.. :..] :Entity)))

(foreach :Location
         (associated-with [:glue:Domains :AdminDomain :Services :ComputingService] :Service :PrimaryLocatedAtServiceLocation :Location
                          (add-token [:Location]
                                     (mapping :CreationTime [:CreationTime-attribute])
                                     (mapping :Validity [:Validity-attribute])
                                     (mapping :ID [:LocalID :LocalID-text-node])
                                     (mapping :Name [:Name :Name-text-node])
                                     (mapping :Address [:Address :Address-text-node])
                                     (mapping :Place [:Place :Place-text-node])
                                     (mapping :Country [:Country :Country-text-node])
                                     (mapping :PostCode [:PostCode :PostCode-text-node])
                                     (mapping :Latitude [:Latitude :Latitude-text-node])
                                     (mapping :Longitude [:Longitude :Longitude-text-node]))))

;(foreach [:glue:Domains :AdminDomain :Services :ComputingService :Location]
;         (add-instance :Location
;                       (mapping [:CreationTime-attribute] :CreationTime)
;                       (mapping [:Validity-attribute] :Validity)
;                       (mapping [:LocalID :LocalID-text-node] :ID)
;                       (mapping [:Name :Name-text-node] :Name)
;                       (mapping [:Address :Address-text-node] :Address)
;                       (mapping [:Place :Place-text-node] :Place)
;                       (mapping [:Country :Country-text-node] :Country)
;                       (mapping [:PostCode :PostCode-text-node] :PostCode)
;                       (mapping [:Latitude :Latitude-text-node] :Latitude)
;                       (mapping [:Longitude :Longitude-text-node] :Longitude)))
;
;(foreach [:glue:Domains :AdminDomain :Services :ComputingService]
;         (add-association :PrimaryLocatedAtServiceLocation
;                          (mapping [:Location] :Location)
;                          (mapping [] :Service)))

(foreach :Contact
         (associated-with [:glue:Domains :AdminDomain :Services :ComputingService] :Service :HasContactServices :Contact
                          (add-token [:Contact]
                                     (mapping :CreationTime [:CreationTime-attribute])
                                     (mapping :Validity [:Validity-attribute])
                                     (mapping :ID [:LocalID :LocalID-text-node])
                                     (mapping :OtherInfo [:OtherInfo :OtherInfo-text-node])
                                     (mapping :Type [:Type :Type-text-node])
                                     (mapping :Detail [:URL :URL-text-node]))))

;(foreach [:glue:Domains :AdminDomain :Services :ComputingService :Contact]
;         (add-instance :Contact
;                       (mapping [:CreationTime-attribute] :CreationTime)
;                       (mapping [:Validity-attribute] :Validity)
;                       (mapping [:LocalID :LocalID-text-node] :ID)
;                       (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
;                       (mapping [:Type :Type-text-node] :Type)
;                       (mapping [:URL :URL-text-node] :Detail)))
;
;(foreach [:glue:Domains :AdminDomain :Services :ComputingService]
;         (add-association :HasContactServices
;                          (mapping [:Contact] :Contact)
;                          (mapping [] :Service)))

(foreach :Endpoint
         (associated-with [:glue:Domains :AdminDomain :Services :ComputingService] :Service :ExposesServiceEndpoint :Endpoint
                          (add-token [:ComputingEndpoint]
                                     (mapping :CreationTime [:CreationTime-attribute])
                                     (mapping :Validity [:Validity-attribute])
                                     (mapping :ID [:ID :ID-text-node])
                                     (mapping :Name [:Name :Name-text-node])
                                     (mapping :URL [:URL :URL-text-node])
                                     (mapping :Technology [:Technology :Technology-text-node])
                                     (mapping :InterfaceName [:Interface :Interface-text-node])
                                     (mapping :InterfaceExtension [:InterfaceExtension :InterfaceExtension-text-node])
                                     (mapping :WSDL [:WSDL :WSDL-text-node])
                                     (mapping :SupportedProfile [:SupportedProfile :SupportedProfile-text-node])
                                     (mapping :Semantics [:Semantics :Semantics-text-node])
                                     (mapping :Implementor [:Implementor :Implementor-text-node])
                                     (mapping :ImplementationName [:ImplementationName :ImplementationName-text-node])
                                     (mapping :ImplementationVersion [:ImplementationVersion :ImplementationVersion-text-node])
                                     (mapping :QualityLevel [:QualityLevel :QualityLevel-text-node])
                                     (mapping :HealthState [:HealthState :HealthState-text-node])
                                     (mapping :HealthStateInfo [:HealthStateInfo :HealthStateInfo-text-node])
                                     (mapping :ServingState [:ServingState :ServingState-text-node])
                                     (mapping :StartTime [:StartTime :StartTime-text-node])
                                     (mapping :IssuerCA [:IssuerCA :IssuerCA-text-node])
                                     (mapping :DowntimeAnnounce [:DowntimeAnnounce :DowntimeAnnounce-text-node])
                                     (mapping :DowntimeStart [:DowntimeStart :DowntimeStart-text-node])
                                     (mapping :DowntimeEnd [:DowntimeEnd :DowntimeEnd-text-node])
                                     (mapping :DowntimeInfo [:DowntimeInfo :DowntimeInfo-text-node]))))

;(foreach [:glue:Domains :AdminDomain :Services :ComputingService :ComputingEndpoint]
;         (add-instance :Endpoint
;                       (mapping [:CreationTime-attribute] :CreationTime)
;                       (mapping [:Validity-attribute] :Validity)
;                       (mapping [:ID :ID-text-node] :ID)
;                       (mapping [:Name :Name-text-node] :Name)
;                       (mapping [:URL :URL-text-node] :URL)
;                       (mapping [:Technology :Technology-text-node] :Technology)
;                       (mapping [:Interface :Interface-text-node] :InterfaceName)
;                       (mapping [:InterfaceExtension :InterfaceExtension-text-node] :InterfaceExtension)
;                       (mapping [:WSDL :WSDL-text-node] :WSDL)
;                       (mapping [:SupportedProfile :SupportedProfile-text-node] :SupportedProfile)
;                       (mapping [:Semantics :Semantics-text-node] :Semantics)
;                       (mapping [:Implementor :Implementor-text-node] :Implementor)
;                       (mapping [:ImplementationName :ImplementationName-text-node] :ImplementationName)
;                       (mapping [:ImplementationVersion :ImplementationVersion-text-node] :ImplementationVersion)
;                       (mapping [:QualityLevel :QualityLevel-text-node] :QualityLevel)
;                       (mapping [:HealthState :HealthState-text-node] :HealthState)
;                       (mapping [:HealthStateInfo :HealthStateInfo-text-node] :HealthStateInfo)
;                       (mapping [:ServingState :ServingState-text-node] :ServingState)
;                       (mapping [:StartTime :StartTime-text-node] :StartTime)
;                       (mapping [:IssuerCA :IssuerCA-text-node] :IssuerCA)
;                       (mapping [:DowntimeAnnounce :DowntimeAnnounce-text-node] :DowntimeAnnounce)
;                       (mapping [:DowntimeStart :DowntimeStart-text-node] :DowntimeStart)
;                       (mapping [:DowntimeEnd :DowntimeEnd-text-node] :DowntimeEnd)
;                       (mapping [:DowntimeInfo :DowntimeInfo-text-node] :DowntimeInfo)))
;
;(foreach [:glue:Domains :AdminDomain :Services :ComputingService]
;         (add-association :ExposesServiceEndpoint
;                          (mapping [:ComputingEndpoint] :Endpoint)
;                          (mapping [] :Service)))

(foreach :Extension
         (associated-with [:glue:Domains :AdminDomain :Services :ComputingService :ComputingEndpoint] :Entity :HasExtentsionEntity :Extension
                          (add-token [:Extensions :Extension]
                                     (mapping :Key [:Key-attribute])
                                     (mapping :Value [:Extension-text-node]))))

;(foreach [:glue:Domains :AdminDomain :Services :ComputingService :ComputingEndpoint :Extensions :Extension]
;         (add-instance :Extension
;                       (mapping [:Key-attribute] :Key)
;                       (mapping [:Extension-text-node] :Value))
;         (add-association :HasExtentsionEntity
;                          (mapping [] :Extension)
;                          (mapping [:.. :..] :Entity)))

(foreach :AccessPolicy
         (associated-with [:glue:Domains :AdminDomain :Services :ComputingService :ComputingEndpoint] :Endpoint :CanAccessAccessPolicyEndpoint :AccessPolicy
                          (add-token [:AccessPolicy]
                                     (mapping :CreationTime [:CreationTime-attribute])
                                     (mapping :Validity [:Validity-attribute])
                                     (mapping :ID [:LocalID :LocalID-text-node])
                                     (mapping :Scheme [:Scheme :Scheme-text-node])
                                     (mapping :Rule [:Rule :Rule-text-node]))))

;(foreach [:glue:Domains :AdminDomain :Services :ComputingService :ComputingEndpoint :AccessPolicy]
;         (add-instance :AccessPolicy
;                       (mapping [:CreationTime-attribute] :CreationTime)
;                       (mapping [:Validity-attribute] :Validity)
;                       (mapping [:LocalID :LocalID-text-node] :ID)
;                       (mapping [:Scheme :Scheme-text-node] :Scheme)
;                       (mapping [:Rule-text-node :Rule] :Rule)))
;
;(foreach [:glue:Domains :AdminDomain :Services :ComputingService :ComputingEndpoint]
;         (add-association :CanAccessAccessPolicyEndpoint
;                          (mapping [:AccessPolicy] :AccessPolicy)
;                          (mapping [] :Endpoint)))

(foreach :Share
         (associated-with [:glue:Domains :AdminDomain :Services :ComputingService] :Service :OffersServiceShare :Share
                          (add-token [:ComputingShares :ComputingShare]
                                     (mapping :ID [:LocalID :LocalID-text-node])
                                     (mapping :Name [:Name :Name-text-node]))))

;(foreach [:glue:Domains :AdminDomain :Services :ComputingService :ComputingShares :ComputingShare]
;         (add-instance :Share
;                       (mapping [:LocalID :LocalID-text-node] :ID)
;                       (mapping [:Name :Name-text-node] :Name)))
;
;(foreach [:glue:Domains :AdminDomain :Services :ComputingService]
;         (add-association :OffersServiceShare
;                          (mapping [:ComputingShares :ComputingShare] :Share)
;                          (mapping [] :Service)))

(foreach :Extension
         (associated-with [:glue:Domains :AdminDomain :Services :ComputingService :ComputingShares :ComputingShare] :Entity :HasExtentsionEntity :Extension
                          (add-token [:Extensions :Extension]
                                     (mapping :Key [:Key-attribute])
                                     (mapping :Value [:Extension-text-node]))))

;(foreach [:glue:Domains :AdminDomain :Services :ComputingService :ComputingShares :ComputingShare :Extensions :Extension]
;         (add-instance :Extension
;                       (mapping [:Key-attribute] :Key)
;                       (mapping [:Extension-text-node] :Value))
;         (add-association :HasExtentsionEntity
;                          (mapping [] :Extension)
;                          (mapping [:.. :..] :Entity)))

; !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! cannot map Type attribute only !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
;(foreach [:glue:Domains :AdminDomain :Services :ComputingService :ComputingActivitites :ComputingActivitiy]
;         (add-instance :Activitiy
;                       (mapping [:Type :Type-text-node] :Type)))

(foreach :Service
         (associated-with [:glue:Domains :AdminDomain :Services :ComputingService] :ToService :RelatesToServiceService :Service
                          (add-token [:Associations :ServiceID]
                                     (mapping :ID [:ServiceID-text-node]))))

;(foreach [:glue:Domains :AdminDomain :Services :ComputingService]
;         (add-association :RelatesToServiceService
;                          (mapping-pk [:Associations :ServiceID :ServiceID-text-node] :Service)
;                          (mapping [] :ToService)))

(foreach :AdminDomain
         (associated-with [:glue:Domains :AdminDomain] :ToAdminDomain :ParticipatesInAdminDomainAdminDomain :AdminDomain
                          (add-token [:Associations :AdminDomainID]
                                     (mapping :ID [:AdminDomainID-text-node]))))

;(foreach [:glue:Domains :AdminDomain]
;         (add-association :ParticipatesInAdminDomainAdminDomain
;                          (mapping-pk [:Associations :AdminDomainID :AdminDomainID-text-node] :AdminDomain)
;                          (mapping [] :ToAdminDomain)))

(foreach :UserDomain
         (add-token [:glue:Domains :UserDomain]
                    (mapping :CreationTime [:CreationTime-attribute])
                    (mapping :Validity [:Validity-attribute])
                    (mapping :ID [:ID :ID-text-node])
                    (mapping :Name [:Name :Name-text-node])
                    (mapping :Description [:Description :Description-text-node])
                    (mapping :WWW [:WWW :WWW-text-node])
                    (mapping :OtherInfo [:OtherInfo :OtherInfo-text-node])
                    (mapping :Level [:Level :Level-text-node])
                    (mapping :UserManager [:UserManager :UserManager-text-node])
                    (mapping :Member [:Member :Member-text-node])))

;(foreach [:glue:Domains :UserDomain]
;         (add-instance :UserDomain
;                       (mapping [:ID :ID-text-node] :ID)
;                       (mapping [:Name :Name-text-node] :Name)
;                       (mapping [:Description :Description-text-node] :Description)
;                       (mapping [:WWW :WWW-text-node] :WWW)
;                       (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
;                       (mapping [:Level :Level-text-node] :Level)
;                       (mapping [:UserManager :UserManager-text-node] :UserManager)
;                       (mapping [:Member :Member-text-node] :Member)))

(foreach :Extension
         (associated-with [:glue:Domains :UserDomain] :Entity :HasExtentsionEntity :Extension
                          (add-token [:Extensions :Extension]
                                     (mapping :Key [:Key-attribute])
                                     (mapping :Value [:Extension-text-node]))))

;(foreach [:glue:Domains :UserDomain :Extensions :Extension]
;         (add-instance :Extension
;                       (mapping [:Key-attribute] :Key)
;                       (mapping [:Extension-text-node] :Value))
;         (add-association :HasExtentsionEntity
;                          (mapping [] :Extension)
;                          (mapping [:.. :..] :Entity)))

(foreach :UserDomain
         (associated-with [:glue:Domains :UserDomain] :ToUserDomain :ParticipatesInUserDomainUserDomain :UserDomain
                          (add-token [:Associations :UserDomainID]
                                     (mapping :ID [:UserDomainID-text-node]))))

;(foreach [:glue:Domains :UserDomain]
;         (add-association :ParticipatesInUserDomainUserDomain
;                          (mapping-pk [:Associations :UserDomainID :UserDomainID-text-node] :UserDomain)
;                          (mapping [] :ToUserDomain)))
