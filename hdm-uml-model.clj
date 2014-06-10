
(def-class :Extension
  (with-attributes
    :LocalID
    :Key
    :Value))

(def-class :Entity
  (with-attributes
    :ID
    :Validity
    :CreationTime
    :OtherInfo
    :Name))

(def-class :Domain
  (with-attributes
    :WWW
    :Description)
  (extends :Entity))

(def-class :Share
  (with-attributes
    :Description)
  (extends :Entity))

(def-class :Service
  (with-attributes
    :StatusInfo
    :QualityLevel
    :Capability
    :Type
    :Complexity)
  (extends :Entity))

(def-class :Location
  (with-attributes
    :Latitude
    :Longitude
    :PostCode
    :Country
    :Place
    :Address)
  (extends :Entity))

(def-class :AdminDomain
  (with-attributes
    :Distributed
    :Owner)
  (extends :Domain)) ;Pytanie, czy robimy podwójne dziedziczenie, czy wystarczy nam, że już :Domain dziedziczy po :Entity

(def-class :UserDomain
  (with-attributes
    :Member
    :UserManager
    :Level)
  (extends :Domain))

(def-class :Contact
  (with-attributes
    :Type
    :Detail)
  (extends :Entity))

(def-class :Manager
  (with-attributes
    :ProductVersion
    :ProductName)
  (extends :Entity))

(def-class :Resource
  (extends :Entity))

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
    :TrustedCA)
  (extends :Entity))

(def-class :Activity
  (extends :Entity))

(def-class :Policy
  (with-attributes
    :Rule
    :Scheme)
  (extends :Entity))

(def-class :AccessPolicy
  (extends :Policy))

(def-class :MappingPolicy
  (extends :Policy))


(association ""
  "role1" :Extension 1 1
  "role2" :Entity 0 *
  )

(association "ParticipatesIn"
  "role1" :UserDomain 1 *)

(association "Creates"
  "role1" :Activity 1 *
  "role2" :UserDomain 0 *)

(association "HasPolicies"
  "role1" :UserDomain 0 *
  "role2" :Policy 0 1)

(association "Has"
  "role1" :Contact 0 *
  "role2" :Domain 0 *)

(association "Has"
  "role1" :Contact 0 *
  "role2" :Service 0 *)

(association "PrimarilyLocatedAt"
  "role1" :Domain 1 *
  "role2" :Location 0 *)

(association "PrimarilyLocatedAt"
  "role1" :Service 1 *
  "role2" :Location 0 *)

(association "RelatesTo"
  "role1" :Service 0 *)

(association "ParticipatesIn"
  "role1" :AdminDomain 1 *)

(association "Manages"
  "role1" :AdminDomain 0 *
  "role2" :Service 0 *)

(association "Manages"
  "role2" :Manager 0 1
  "role1" :Resource 0 *)

(association "Offers"
  "role1" :Service 0 *
  "role2" :Manager 1 1)

(association "Offers"
  "role1" :Service 0 *
  "role2" :Share 1 1)

(association "Exposes"
  "role1" :Service 0 *
  "role2" :Endpoint 1 1)

(association "CanBeMappedInto"
  "role1" :MappingPolicy 1 1
  "role2" :Share)

(association "CanAccess"
  "role1" :AccessPolicy 1 1
  "role2" :Endpoint 0 *)

(association "DefinedOn"
  "role1" :Share 0 *
  "role2" :Resource 0 *)

(association "Offers"
  "role1" :Endpoint 0 *
  "role2" :Share)

(association "SubmittedBy"
  "role1" :Endpoint 0 *
  "role2" :Activity 1 *)

(association "MappedInto"
  "role1" :Activity 1 *
  "role2" :Resource 0 *)

(association "Runs"
  "role1" :Activity 1 *
  "role2" :Share 0 *)

(association "RelatesTo"
  "role1" :Activity 0 *)


;---------------------------------------------------------------------------------------------------------------
;temporary, to be resolved solution

(generalization
  (into :Policy)
  (from :MappingPolicy :AccessPolicy))

(generalization
  (into :Domain)
  (from :AdminDomain :UserDomain))

(genralization
  (into :Entity)
  (from :Domain :Policy :Endpoint :Contact :Location :Service :Share :Resource :Activity :Manager))