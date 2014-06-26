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

;;; classes

(class :Extension
       (attribute :LocalID :LocalID_t :1 :1)
       (attribute :Key :String :1 :1)
       (attribute :Value :String :1 :1))

(class :Entity
       (key-attribute :ID :URI :1 :1)
       (attribute :Validity :UInt64 :0 :1)
       (attribute :CreationTime :DateTime_t :0 :1)
       (attribute :OtherInfo :String :0 :*)
       (attribute :Name :String :0 :1))

(class :Domain
       (attribute :WWW :URL :0 :*)
       (attribute :Description :String :0 :1)
       (extends :Entity))

(class :Share
       (attribute :Description :String :0 :1)
       (extends :Entity))

(class :Service
       (attribute :StatusInfo :URL :1 :1)
       (attribute :QualityLevel :QualityLevel_t :1 :1)
       (attribute :Capability :Capability_t :0 :*)
       (attribute :Type :ServiceType_t :1 :1)
       (attribute :Complexity :String :0 :1)
       (extends :Entity))

(class :Location
       (attribute :Latitude :Real32 :0 :1)
       (attribute :Longitude :Real32 :0 :1)
       (attribute :PostCode :String :0 :1)
       (attribute :Country :String :0 :1)
       (attribute :Place :String :0 :1)
       (attribute :Address :String :0 :1)
       (extends :Entity))

(class :AdminDomain
       (attribute :Distributed :ExtendedBoolean_t :0 :1)
       (attribute :Owner :String :0 :*)
       (extends :Domain))

(class :UserDomain
       (attribute :Member :String :0 :*)
       (attribute :UserManager :URI :0 :*)
       (attribute :Level :UInt32 :0 :1)
       (extends :Domain))

(class :Contact
       (attribute :Type :ContactType_t :1 :1)
       (attribute :Detail :URI :1 :1)
       (extends :Entity))

(class :Manager
       (attribute :ProductVersion :String :0 :1)
       (attribute :ProductName :String :1 :1)
       (extends :Entity))

(class :Resource
       (extends :Entity))

(class :Endpoint
       (attribute :StartTime :DateTime_t :0 :1)
       (attribute :HealthStateInfo :String :0 :1)
       (attribute :SupportedProfile :URI :0 :*)
       (attribute :InterfaceName :InterfaceName_t :1 :1)
       (attribute :InterfaceVersion :String :0 :*)
       (attribute :InterfaceExtension :URI :0 :*)
       (attribute :HealthState :EndpointHealthState_t :1 :1)
       (attribute :URL :URL :1 :1)
       (attribute :DowntimeAnnounce :DateTime_t :0 :1)
       (attribute :QualityLevel :QualityLevel_t :1 :1)
       (attribute :IssuerCA :DN_t :0 :1)
       (attribute :DowntimeStart :DateTime_t :0 :1)
       (attribute :DowntimeInfo :String :0 :1)
       (attribute :WSDL :URL :0 :*)
       (attribute :ServingState :ServingState_t :1 :1)
       (attribute :Implementor :String :0 :1)
       (attribute :Semantics :URI :0 :*)
       (attribute :Technology :EndpointTechnology_t :0 :1)
       (attribute :Capability :Capability_t :0 :*)
       (attribute :ImplementationName :String :0 :1)
       (attribute :ImplementationVersion :String :0 :1)
       (attribute :DowntimeEnd :DateTime_t :0 :1)
       (attribute :TrustedCA :DN_t :0 :*)
       (extends :Entity))

(class :Activity
       (extends :Entity))

(class :Policy
       (attribute :Rule :String :1 :*)
       (attribute :Scheme :PolicyScheme_t :1 :1)
       (extends :Entity))

(class :AccessPolicy
       (extends :Policy))

(class :MappingPolicy
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
