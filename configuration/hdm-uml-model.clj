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

(uml-class :Extension
           (uml-attribute :LocalID :LocalID_t :1 :1)
           (uml-attribute :Key :String :1 :1)
           (uml-attribute :Value :String :1 :1))

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
