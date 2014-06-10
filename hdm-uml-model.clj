(def-class :Share
  (with-attributes
    :Description))

(def-class :Service
  (with-attributes
    :StatusInfo
    :QualityLevel
    :Capability
    :Type
    :Complexity))

(def-class :Location
  (with-attributes
    :Latitude
    :Longitude
    :PostCode
    :Country
    :Place
    :Address))

(def-class :AdminDomain
  (with-attributes
    :Distributed
    :Owner))

(def-class :Domain
  (with-attributes
    :WWW
    :Description))

(def-class :Entity
  (with-attributes
    :ID
    :Validity
    :CreationTime
    :OtherInfo
    :Name))

(def-class :Extension
  (with-attributes
    :LocalID
    :Key
    :Value))

(def-class :Contact
  (with-attributes
    :Type
    :Detail))

(def-class :Manager
  (with-attributes
    :ProductVersion
    :ProductName))

(def-class :Resource)

(def-class :Endpoint
  (with-attributes
    :StartTime
    :HealthStateInfo
    :SupportedProfile
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
    :TrustedCA))

(def-class :Activity)

(def-class :Policy
  (with-attributes
    :Rule
    :Scheme))

(def-class :AccessPolicy)

(def-class :MappingPolicy)


(def-class :UserDomain
  (with-attributes
    :Member
    :UserManager
    :Level))

(association ""
  "role1" :Extension 1 1
  "role2" :Entity 0 *
  )

(association "ParticipatesIn"
  "role1" :UserDomain 1 *)

(association "HasPolicies"
  "role1" :UserDomain 0 *
  "role2" :Policy 0 1)