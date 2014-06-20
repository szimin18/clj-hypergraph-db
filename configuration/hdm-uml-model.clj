;TODO add attribute cardinalities, classes | add formula indicating "key" attribute

(class :Extension
  (attributes
    :LocalID
    :Key
    :Value))

(class :Entity
  (attributes
    :ID
    :Validity
    :CreationTime
    :OtherInfo
    :Name))

(class :Domain
  (attributes
    :WWW
    :Description)
  (extends :Entity))

(class :Share
  (attributes
    :Description)
  (extends :Entity))

(class :Service
  (attributes
    :StatusInfo
    :QualityLevel
    :Capability
    :Type
    :Complexity)
  (extends :Entity))

(class :Location
  (attributes
    :Latitude
    :Longitude
    :PostCode
    :Country
    :Place
    :Address)
  (extends :Entity))

(class :AdminDomain
  (attributes
    :Distributed
    :Owner)
  (extends :Domain))

(class :UserDomain
  (attributes
    :Member
    :UserManager
    :Level)
  (extends :Domain))

(class :Contact
  (attributes
    :Type
    :Detail)
  (extends :Entity))

(class :Manager
  (attributes
    :ProductVersion
    :ProductName)
  (extends :Entity))

(class :Resource
  (extends :Entity))

(class :Endpoint
  (attributes
    :StartTime
    :HealthStateInfo
    :SupportedProfile
    :InterfaceName
    :InterfaceVersion
    :InterfaceExtension
    :HealthState
    :URL
    :DowntimeAnnounce
    :QualityLevel
    :IssuerCA
    :DowntimeStart
    :DowntimeInfo
    :WSDL
    :ServingState
    :Implementor
    :Semantics
    :Technology
    :Capability
    :ImplementationName
    :ImplementationVersion
    :DowntimeEnd
    :TrustedCA)
  (extends :Entity))

(class :Activity
  (extends :Entity))

(class :Policy
  (attributes
    :Rule
    :Scheme)
  (extends :Entity))

(class :AccessPolicy
  (extends :Policy))

(class :MappingPolicy
  (extends :Policy))


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
