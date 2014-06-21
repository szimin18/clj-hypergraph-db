;;; type representations

(representation :String String)
(representation :LocalID_t String)
(representation :URI String)
(representation :URL String)
(representation :UInt32 Integer)
(representation :UInt64 Long)
(representation :Real32 Double)
(representation :DateTime_t )               ;????????????????????????????????????????????????????????????????
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
  (role :Extension :1 :1)
  (role :Entity :0 :*))

(association
  :ParticipatesInUserDomainToUserDomain
  "ParticipatesIn"
  (role :UserDomain :0 :1)
  (role "to" :UserDomain :0 :*))

(association
  :CreatesActivityUserDomain
  "Creates"
  (role :Activity :1 :*)
  (role :UserDomain :0 :*))

(association
  :HasPoliciesUserDomainPolicy
  "HasPolicies"
  (role :UserDomain :0 :*)
  (role :Policy :0 :1))

(association
  :HasContactDomain
  "Has"
  (role :Contact :0 :*)
  (role :Domain :0 :*))

(association
  :HasContactServices
  "Has"
  (role :Contact :0 :*)
  (role :Service :0 :*))

(association
  :PrimaryLocatedAtDomainLocation
  "PrimarilyLocatedAt"
  (role :Domain :1 :*)
  (role :Location :0 :*))

(association
  :PrimaryLocatedAtServiceLocation
  "PrimarilyLocatedAt"
  (role :Service :1 :*)
  (role :Location :0 :*))

(association
  :RelatesToServiceToService
  "RelatesTo"
  (role :Service :0 :*)
  (role "to" :Service :0 :*))

(association
  :ParticipatesInAdminDomainToAdminDomain
  "ParticipatesIn"
  (role :AdminDomain :1 :*)
  (role "to" :AdminDomain :1 :*))

(association
  :ManagesAdminDomainService
  "Manages"
  (role :AdminDomain :0 :*)
  (role :Service :0 :*))

(association
  :ManagesManagerResource
  "Manages"
  (role :Manager :0 :1)
  (role :Resource :0 :*))

(association
  :OffersServiceManager
  "Offers"
  (role :Service :0 :*)
  (role :Manager :1 :1))

(association
  :OffersServiceShare
  "Offers"
  (role :Service :0 :*)
  (role :Share :1 :1))

(association
  :ExposesServiceEndpoint
  "Exposes"
  (role :Service :0 :*)
  (role :Endpoint :1 :1))

(association
  :CanBeMappedIntoMappingPolicyShare
  "CanBeMappedInto"
  (role :MappingPolicy :1 :1)
  (role :Share :0 :*))

(association
  :CanAccessAccessPolicyEndpoint
  "CanAccess"
  (role :AccessPolicy :1 :1)
  (role :Endpoint :0 :*))

(association
  :DefinedOnShareResource
  "DefinedOn"
  (role :Share :0 :*)
  (role :Resource :0 :*))

(association
  :OffersEndpointShare
  "Offers"
  (role :Endpoint :0 :*)
  (role :Share :0 :*))

(association
  :SubmittedByEndpointActivity
  "SubmittedBy"
  (role :Endpoint :0 :*)
  (role :Activity :1 :*))

(association
  :MappedIntoActivityResource
  "MappedInto"
  (role :Activity :1 :*)
  (role :Resource :0 :*))

(association
  :RunsActivityShare
  "Runs"
  (role :Activity :1 :*)
  (role :Share :0 :*))

(association
  :RelatesToActivityToActivity
  "RelatesTo"
  (role :Activity :0 :*)
  (role "to" :Activity :0 :*))
