;;; model

(model-type :uml)

;;; type representations

(representation :String String)
(representation :LocalID_t String)
(representation :URI String)
(representation :URL String)
(representation :UInt32 Integer)
(representation :UInt64 Long)
(representation :Real32 Double)
(representation :DateTime_t String)               ;????????????????????????????????????????????????????????????????
(representation :QualityLevel_t  String)
(representation :Capability_t String)
(representation :ServiceType_t String)
(representation :ExtendedBoolean_t Boolean)
(representation :ContactType_t String)
(representation :InterfaceName_t String)
(representation :EndpointHealthState_t String)
(representation :DN_t String)
(representation :ServingState_t String)
(representation :EndpointTechnology_t String)
(representation :PolicyScheme_t String)

;;; uml-classes


(uml-class :GlueSite
  (key-uml-attribute :GlueSiteUniqueID :URI :1 :1)
  (uml-attribute :GlueSiteName :String :1 :1)
  (uml-attribute :GlueSiteDescription :String :1 :1)
  (uml-attribute :GlueSiteEmailContact :String :1 :1)
  (uml-attribute :GlueSiteSysAdminContact :String :1 :1)
  (uml-attribute :GlueSiteUserSupportContact :String :1 :1)
  (uml-attribute :GlueSiteSecurityContact :String :1 :1)
  (uml-attribute :GlueSiteLocation :String :1 :1)
  (uml-attribute :GlueSiteLatitude :String :1 :1)
  (uml-attribute :GlueSiteLongitude :String :1 :1)
  (uml-attribute :GlueSiteWeb :String :1 :1)
  (uml-attribute :GlueSiteSponsor :String :1 :1)
  (uml-attribute :GlueSiteOtherInfo :String :1 :1))

(uml-class :GlueService
  (key-uml-attribute :GlueServiceUniqueID :URI :1 :1)
  (uml-attribute :GlueServiceName :String :1 :1)
  (uml-attribute :GlueServiceType :String :1 :1)
  (uml-attribute :GlueServiceVersion :String :1 :1)
  (uml-attribute :GlueServiceEndpoint :String :1 :1)
  (uml-attribute :GlueServiceStatus :String :1 :1)
  (uml-attribute :GlueServiceStatusInfo :String :1 :1)
  (uml-attribute :GlueServiceStartTime :String :1 :1)
  (uml-attribute :GlueServiceOwner :String :1 :1)
  (uml-attribute :GlueServiceAccessControlBaseRule :String :1 :1)
  (uml-attribute :GlueServiceAccessControlRule :String :1 :1)
  (uml-attribute :GlueService :String :1 :1)
  (uml-attribute :GlueServiceWSDL :String :1 :1)
  (uml-attribute :GlueServiceSiteUniqueID :String :1 :1))

(uml-class :GlueServiceData
  (key-uml-attribute 	:GlueServiceDataKey	:String :1 :1	)
  (key-uml-attribute 	:GlueServiceDataValue	:String :1 :1	)
  (uml-attribute 	:GlueChunkKey	:String :1 :1	))

(uml-class :GlueCluster
  (uml-attribute 	:GlueClusterUniqueID	:String :1 :1)
  (uml-attribute 	:GlueClusterName	:String :1 :1)
  (uml-attribute 	:GlueClusterService	:String :1 :1)
  (uml-attribute 	:GlueCEUniqueID	:String :1 :1)
  (uml-attribute 	:GlueClusterTmpDir	:String :1 :1)
  (uml-attribute 	:GlueClusterWNTmpDir	:String :1 :1)
  (uml-attribute 	:GlueInformationServiceURL	:String :1 :1))

(uml-class :GlueSubCluster
  (uml-attribute 	:GlueSubClusterUniqueID	:String :1 :1)
  (uml-attribute 	:GlueSubClusterName	:String :1 :1)
  (uml-attribute 	:GlueSubClusterWNTmpDir	:String :1 :1)
  (uml-attribute 	:GlueSubClusterTmpDir	:String :1 :1)
  (uml-attribute 	:GlueSubClusterPhysicalCPUs	:String :1 :1)
  (uml-attribute 	:GlueSubClusterLogicalCPUs	:String :1 :1)
  (uml-attribute 	:GlueHostOperatingSystemName	:String :1 :1)
  (uml-attribute 	:GlueHostOperatingSystemRelease	:String :1 :1)
  (uml-attribute 	:GlueHostOperatingSystemVersion	:String :1 :1)
  (uml-attribute 	:GlueHostArchitecturePlatformType	:String :1 :1)
  (uml-attribute 	:GlueHostArchitectureSMPSize	:String :1 :1)
  (uml-attribute 	:GlueHostProcessorVendor	:String :1 :1)
  (uml-attribute 	:GlueHostProcessorModel	:String :1 :1)
  (uml-attribute 	:GlueHostProcessorVersion	:String :1 :1)
  (uml-attribute 	:GlueHostProcessorClockSpeed	:String :1 :1)
  (uml-attribute 	:GlueHostProcessorInstructionSet	:String :1 :1)
  (uml-attribute 	:GlueHostProcessorOtherDescription	:String :1 :1)
  (uml-attribute 	:GlueHostApplicationSoftwareRunTimeEnvironment	:String :1 :1)
  (uml-attribute 	:GlueHostBenchmarkSI00	:String :1 :1)
  (uml-attribute 	:GlueHostBenchmarkSF00	:String :1 :1)
  (uml-attribute 	:GlueHostNetworkAdapterOutboundIP	:String :1 :1)
  (uml-attribute 	:GlueHostNetworkAdapterInboundIP	:String :1 :1)
  (uml-attribute 	:GlueHostMainMemoryRAMSize	:String :1 :1)
  (uml-attribute 	:GlueHostMainMemoryVirtualSize	:String :1 :1))

(uml-class :GlueCE
  (uml-attribute 	:GlueCEAccessControlBaseRule	:String :1 :1)
  (uml-attribute 	:GlueCEUniqueID	:String :1 :1)
  (uml-attribute 	:GlueForeignKey	:String :1 :1)
  (uml-attribute :GlueCEHostingCluster :String :1 :1)

  (uml-attribute 	:GlueCEInfoTotalCPUs	:String :1 :1)
  (uml-attribute 	:GlueCEInfoLRMSType	:String :1 :1)
  (uml-attribute 	:GlueCEInfoLRMSVersion	:String :1 :1)
  (uml-attribute 	:GlueCEInfoGRAMVersion	:String :1 :1)
  (uml-attribute 	:GlueCEInfoHostName	:String :1 :1)
  (uml-attribute 	:GlueCEInfoGatekeeperPort	:String :1 :1)
  (uml-attribute 	:GlueCEInfoContactString	:String :1 :1)
  (uml-attribute 	:GlueCEInfoJobManager	:String :1 :1)
  (uml-attribute 	:GlueCEInfoApplicationDir	:String :1 :1)
  (uml-attribute 	:GlueCEInfoDataDir	:String :1 :1)
  (uml-attribute 	:GlueCEInfoDefaultSE	:String :1 :1)

  (uml-attribute 	:GlueCEPolicyPriority	:String :1 :1)
  (uml-attribute 	:GlueCEPolicyMaxRunningJobs	:String :1 :1)
  (uml-attribute 	:GlueCEPolicyMaxTotalJobs	:String :1 :1)
  (uml-attribute 	:GlueCEPolicyMaxCPUTime	:String :1 :1)
  (uml-attribute 	:GlueCEPolicyMaxWallClockTime	:String :1 :1)
  (uml-attribute 	:GlueCEPolicyAssignedJobSlots	:String :1 :1)
  (uml-attribute 	:GlueCEPolicyMaxObtainableWallClockTime	:String :1 :1)
  (uml-attribute 	:GlueCEPolicyMaxObtainableCPUTime	:String :1 :1)
  (uml-attribute 	:GlueCEPolicyMaxWaitingJobs	:String :1 :1)
  (uml-attribute 	:GlueCEPolicyMaxSlotsPerJob	:String :1 :1)
  (uml-attribute 	:GlueCEPolicyPreemption	:String :1 :1))

(uml-class :Extension
           (uml-attribute :LocalID :LocalID_t :1 :1)
           (key-uml-attribute :Key :String :1 :1)
           (key-uml-attribute :Value :String :1 :1))

(uml-class :Entity
           (key-uml-attribute :ID :URI :1 :1)
           (uml-attribute :Validity :UInt64 :0 :1)
           (uml-attribute :CreationTime :DateTime_t :0 :1)
           (uml-attribute :OtherInfo :String :0 :*)
           (uml-attribute :Name :String :0 :1))

(uml-class :Domain
           (uml-attribute :WWW :URL :0 :*)
           (uml-attribute :Description :String :0 :1)
           (extends :Entity))

(uml-class :Share
           (uml-attribute :Description :String :0 :1)
           (extends :Entity))

(uml-class :Service
           (uml-attribute :StatusInfo :URL :1 :1)
           (uml-attribute :QualityLevel :QualityLevel_t :1 :1)
           (uml-attribute :Capability :Capability_t :0 :*)
           (uml-attribute :Type :ServiceType_t :1 :1)
           (uml-attribute :Complexity :String :0 :1)
           (extends :Entity))

(uml-class :Location
           (uml-attribute :Latitude :Real32 :0 :1)
           (uml-attribute :Longitude :Real32 :0 :1)
           (uml-attribute :PostCode :String :0 :1)
           (uml-attribute :Country :String :0 :1)
           (uml-attribute :Place :String :0 :1)
           (uml-attribute :Address :String :0 :1)
           (extends :Entity))

(uml-class :AdminDomain
           (uml-attribute :Distributed :ExtendedBoolean_t :0 :1)
           (uml-attribute :Owner :String :0 :*)
           (extends :Domain))

(uml-class :UserDomain
           (uml-attribute :Member :String :0 :*)
           (uml-attribute :UserManager :URI :0 :*)
           (uml-attribute :Level :UInt32 :0 :1)
           (extends :Domain))

(uml-class :Contact
           (uml-attribute :Type :ContactType_t :1 :1)
           (uml-attribute :Detail :URI :1 :1)
           (extends :Entity))

(uml-class :Manager
           (uml-attribute :ProductVersion :String :0 :1)
           (uml-attribute :ProductName :String :1 :1)
           (extends :Entity))

(uml-class :Resource
           (extends :Entity))

(uml-class :Endpoint
           (uml-attribute :StartTime :DateTime_t :0 :1)
           (uml-attribute :HealthStateInfo :String :0 :1)
           (uml-attribute :SupportedProfile :URI :0 :*)
           (uml-attribute :InterfaceName :InterfaceName_t :1 :1)
           (uml-attribute :InterfaceVersion :String :0 :*)
           (uml-attribute :InterfaceExtension :URI :0 :*)
           (uml-attribute :HealthState :EndpointHealthState_t :1 :1)
           (uml-attribute :URL :URL :1 :1)
           (uml-attribute :DowntimeAnnounce :DateTime_t :0 :1)
           (uml-attribute :QualityLevel :QualityLevel_t :1 :1)
           (uml-attribute :IssuerCA :DN_t :0 :1)
           (uml-attribute :DowntimeStart :DateTime_t :0 :1)
           (uml-attribute :DowntimeInfo :String :0 :1)
           (uml-attribute :WSDL :URL :0 :*)
           (uml-attribute :ServingState :ServingState_t :1 :1)
           (uml-attribute :Implementor :String :0 :1)
           (uml-attribute :Semantics :URI :0 :*)
           (uml-attribute :Technology :EndpointTechnology_t :0 :1)
           (uml-attribute :Capability :Capability_t :0 :*)
           (uml-attribute :ImplementationName :String :0 :1)
           (uml-attribute :ImplementationVersion :String :0 :1)
           (uml-attribute :DowntimeEnd :DateTime_t :0 :1)
           (uml-attribute :TrustedCA :DN_t :0 :*)
           (extends :Entity))

(uml-class :Activity
           (extends :Entity))

(uml-class :Policy
           (uml-attribute :Rule :String :1 :*)
           (uml-attribute :Scheme :PolicyScheme_t :1 :1)
           (extends :Entity))

(uml-class :AccessPolicy
           (extends :Policy))

(uml-class :MappingPolicy
           (extends :Policy))

;;; associations

(association
  :HasExtentsionEntity
  "Has"
  (role :Extension :Extension :1 :1)
  (role :Entity :Entity :0 :*))

(association
  :ParticipatesInUserDomainUserDomain
  "ParticipatesIn"
  (role :UserDomain :UserDomain :0 :1)
  (role :ToUserDomain :UserDomain :0 :*))

(association
  :CreatesActivityUserDomain
  "Creates"
  (role :Activity :Activity :1 :*)
  (role :UserDomain :UserDomain :0 :*))

(association
  :HasPoliciesUserDomainPolicy
  "HasPolicies"
  (role :UserDomain :UserDomain :0 :*)
  (role :Policy :Policy :0 :1))

(association
  :HasContactDomain
  "Has"
  (role :Contact :Contact :0 :*)
  (role :Domain :Domain :0 :*))

(association
  :HasContactServices
  "Has"
  (role :Contact :Contact :0 :*)
  (role :Service :Service :0 :*))

(association
  :PrimaryLocatedAtDomainLocation
  "PrimarilyLocatedAt"
  (role :Domain :Domain :1 :*)
  (role :Location :Location :0 :*))

(association
  :PrimaryLocatedAtServiceLocation
  "PrimarilyLocatedAt"
  (role :Service :Service :1 :*)
  (role :Location :Location :0 :*))

(association
  :RelatesToServiceService
  "RelatesTo"
  (role :Service :Service :0 :*)
  (role :ToService :Service :0 :*))

(association
  :ParticipatesInAdminDomainAdminDomain
  "ParticipatesIn"
  (role :AdminDomain :AdminDomain :1 :*)
  (role :ToAdminDomain :AdminDomain :1 :*))

(association
  :ManagesAdminDomainService
  "Manages"
  (role :AdminDomain :AdminDomain :0 :*)
  (role :Service :Service :0 :*))

(association
  :ManagesManagerResource
  "Manages"
  (role :Manager :Manager :0 :1)
  (role :Resource :Resource :0 :*))

(association
  :OffersServiceManager
  "Offers"
  (role :Service :Service :0 :*)
  (role :Manager :Manager :1 :1))

(association
  :OffersServiceShare
  "Offers"
  (role :Service :Service :0 :*)
  (role :Share :Share :1 :1))

(association
  :ExposesServiceEndpoint
  "Exposes"
  (role :Service :Service :0 :*)
  (role :Endpoint :Endpoint :1 :1))

(association
  :CanBeMappedIntoMappingPolicyShare
  "CanBeMappedInto"
  (role :MappingPolicy :MappingPolicy :1 :1)
  (role :Share :Share :0 :*))

(association
  :CanAccessAccessPolicyEndpoint
  "CanAccess"
  (role :AccessPolicy :AccessPolicy :1 :1)
  (role :Endpoint :Endpoint :0 :*))

(association
  :DefinedOnShareResource
  "DefinedOn"
  (role :Share :Share :0 :*)
  (role :Resource :Resource :0 :*))

(association
  :OffersEndpointShare
  "Offers"
  (role :Endpoint :Endpoint :0 :*)
  (role :Share :Share :0 :*))

(association
  :SubmittedByEndpointActivity
  "SubmittedBy"
  (role :Endpoint :Endpoint :0 :*)
  (role :Activity :Activity :1 :*))

(association
  :MappedIntoActivityResource
  "MappedInto"
  (role :Activity :Activity :1 :*)
  (role :Resource :Resource :0 :*))

(association
  :RunsActivityShare
  "Runs"
  (role :Activity :Activity :1 :*)
  (role :Share :Share :0 :*))

(association
  :RelatesToActivityActivity
  "RelatesTo"
  (role :Activity :Activity :0 :*)
  (role :ToActivity :Activity :0 :*))
