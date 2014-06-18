;TODO Extensions, Ew poprawienie mapowań blokowych - w tym przypadku można np. wywalić mapowania kontenerów - nie są raczej w ogóle potrzebne,
;zrobić ich mapowanie poprzez ścieżko docelowe

(for [:glue:Domains :AdminDomain]
  (add-instance :AdminDomain
                (mapping [:Distributed :Distributed-text-node] :Distributed)
                (mapping [:Owner :Owner-text-node] :Owner)
                (mapping [:WWW :WWW-text-node] :WWW)
                (mapping [:Description :Description-text-node] :Description)
                (mapping [:ID :ID-text-node :ID] :ID)
                (mapping [:Name :Name-text-node] :Name)
                (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                (mapping [:CreationTime-attribute] :CreationTime)
                (mapping [:Validity-attribute] :Validity)))


(for [:glue:Domains :AdminDomain :Location]
  (add-instance :Location
                (mapping [:Distributed :Distributed-text-node] :Distributed)
                (mapping [:Owner :Owner-text-node] :Owner)
                (mapping [:WWW :WWW-text-node] :WWW)
                (mapping [:Description :Description-text-node] :Description)
                (mapping [:LocalID :LocalID-text-node :ID] :ID)
                (mapping [:Name :Name-text-node] :Name)
                (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                (mapping [:Latitude-attribute] :Latitude)
                (mapping [:Longitude-attribute] :Longitude)
                (mapping [:PostCode-attribute] :PostCode)
                (mapping [:Country-attribute] :Country)
                (mapping [:Place-attribute] :Place)
                (mapping [:Address-attribute] :Address)))

(for [:glue:Domains :AdminDomain :Contact]
  (add-instance :Contact
                (mapping [:Distributed :Distributed-text-node] :Distributed)
                (mapping [:Owner :Owner-text-node] :Owner)
                (mapping [:WWW :WWW-text-node] :WWW)
                (mapping [:Description :Description-text-node] :Description)
                (mapping [:LocalID :LocalID-text-node :ID] :ID)
                (mapping [:Name :Name-text-node] :Name)
                (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                (mapping [:Latitude-attribute] :Latitude)
                (mapping [:Longitude-attribute] :Longitude)
                (mapping [:PostCode-attribute] :PostCode)
                (mapping [:Country-attribute] :Country)
                (mapping [:CreationTime-attribute] :CreationTime)
                (mapping [:Validity-attribute] :Validity)))

(for [:glue:Domains :UserDomain]
  (add-instance :UserDomain (mapping [:Distributed :Distributed-text-node] :Distributed)
                (mapping [:Owner :Owner-text-node] :Owner)
                (mapping [:WWW :WWW-text-node] :WWW)
                (mapping [:Description :Description-text-node] :Description)
                (mapping [:ID :ID-text-node :ID] :ID)
                (mapping [:Name :Name-text-node] :Name)
                (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
                (mapping [:Level-text-node] :Level)
                (mapping [:UserManager-text-node] :UserManager)
                (mapping [:Member-text-node] :Member)))

;????????????????
(for [:glue:Domains :AdminDomain :Services]
  (add-instance :Services
    (mapping [:Service :Service-text-node] :Service)
    (mapping [:ComputingService :ComputingService-text-node] :ComputingService)))

(for [:glue:Domains :AdminDomain :Services :Service]
  (add-instance :Service
    (mapping [:ID :ID-text-node] :ID)
    (mapping [:Name :Name-text-node] :Name)
    (mapping [:Capability :Capability-text-node] :Capability)
    (mapping [:Type :Type-text-node] :Type)
    (mapping [:QualityLevel :QualityLevel-text-node] :QualityLevel)
    (mapping [:Complexity :Complexity-text-node] :Complexity)
    (mapping [:StatusPage] :StatusPage)))

(for [:glue:Domains :AdminDomain :Services :Service :Location]
  (add-instance :Location
    (mapping [:Distributed :Distributed-text-node] :Distributed)
    (mapping [:Owner :Owner-text-node] :Owner)
    (mapping [:WWW :WWW-text-node] :WWW)
    (mapping [:Description :Description-text-node] :Description)
    (mapping [:LocalID :LocalID-text-node :ID] :ID)
    (mapping [:Name :Name-text-node] :Name)
    (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
    (mapping [:Latitude-attribute] :Latitude)
    (mapping [:Longitude-attribute] :Longitude)
    (mapping [:PostCode-attribute] :PostCode)
    (mapping [:Country-attribute] :Country)
    (mapping [:Place-attribute] :Place)
    (mapping [:Address-attribute] :Address)))

(for [:glue:Domains :AdminDomain :Services :Service :Contact]
  (add-instance :Contact
    (mapping [:Distributed :Distributed-text-node] :Distributed)
    (mapping [:Owner :Owner-text-node] :Owner)
    (mapping [:WWW :WWW-text-node] :WWW)
    (mapping [:Description :Description-text-node] :Description)
    (mapping [:LocalID :LocalID-text-node :ID] :ID)
    (mapping [:Name :Name-text-node] :Name)
    (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
    (mapping [:Latitude-attribute] :Latitude)
    (mapping [:Longitude-attribute] :Longitude)
    (mapping [:PostCode-attribute] :PostCode)
    (mapping [:Country-attribute] :Country)
    (mapping [:CreationTime-attribute] :CreationTime)
    (mapping [:Validity-attribute] :Validity)))

(for [:glue:Domains :AdminDomain :Services :Service :Endpoint]
  (add-instance :Endpoint
    (mapping [:ID :ID-text-node] :ID)
    (mapping [:URL :URL-text-node] :URL)
    (mapping [:Capability :Capability-text-node] :Capability)
    (mapping [:Technology :Technology-text-node] :Technology)
    (mapping [:Interface :Interface-text-node] :Interface)
    (mapping [:WSDL :WSDL-text-node] :WSDL)
    (mapping [:SupportedProfile :SupportedProfile-text-node] :SupportedProfile)
    (mapping [:Semantics :Semantics-text-node] :Semantics)
    (mapping [:Implementor :Implementor-text-node] :Implementor)
    (mapping [:ImplementationName :ImplementationName-text-node] :ImplementationName)
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

(for [:glue:Domains :AdminDomain :Services :Service :Endpoint :AccessPolicy]
  (add-instance :AccessPolicy
    (mapping [:CreationTime-attribute] :CreationTime)
    (mapping [:Validity-attribute] :Validity)
    (mapping [:LocalID :LocalID-text-node] :LocalID)
    (mapping [:Scheme :Scheme-text-node] :Scheme)
    (mapping [:Rule-text-node :Rule] :Rule)))

(for [:glue:Domains :AdminDomain :Services :Service :Activities]
  (add-instance :Activities
    (mapping [:Activity] :Activity)))

(for [:glue:Domains :AdminDomain :Services :Service :Activities :Activity]
  (add-instance :Activity
    (mapping [:ID :ID-text-node] :ID)))

(for [:glue:Domains :AdminDomain :Services :ComputingService]
  (add-instance :ComputingService
    (mapping [:ID :ID-text-node] :ID)
    (mapping [:Name :Name-text-node] :Name)
    (mapping [:Capability :Capability-text-node] :Capability)
    (mapping [:Type :Type-text-node] :Type)
    (mapping [:QualityLevel :QualityLevel-text-node] :QualityLevel)
    (mapping [:Complexity :Complexity-text-node] :Complexity)
    (mapping [:StatusPage] :StatusPage)))

(for [:glue:Domains :AdminDomain :Services :ComputingService :Location]
  (add-instance :Location
    (mapping [:Distributed :Distributed-text-node] :Distributed)
    (mapping [:Owner :Owner-text-node] :Owner)
    (mapping [:WWW :WWW-text-node] :WWW)
    (mapping [:Description :Description-text-node] :Description)
    (mapping [:LocalID :LocalID-text-node :ID] :ID)
    (mapping [:Name :Name-text-node] :Name)
    (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
    (mapping [:Latitude-attribute] :Latitude)
    (mapping [:Longitude-attribute] :Longitude)
    (mapping [:PostCode-attribute] :PostCode)
    (mapping [:Country-attribute] :Country)
    (mapping [:Place-attribute] :Place)
    (mapping [:Address-attribute] :Address)))

(for [:glue:Domains :AdminDomain :Services :ComputingService :Contact]
  (add-instance :Contact
    (mapping [:Distributed :Distributed-text-node] :Distributed)
    (mapping [:Owner :Owner-text-node] :Owner)
    (mapping [:WWW :WWW-text-node] :WWW)
    (mapping [:Description :Description-text-node] :Description)
    (mapping [:LocalID :LocalID-text-node :ID] :ID)
    (mapping [:Name :Name-text-node] :Name)
    (mapping [:OtherInfo :OtherInfo-text-node] :OtherInfo)
    (mapping [:Latitude-attribute] :Latitude)
    (mapping [:Longitude-attribute] :Longitude)
    (mapping [:PostCode-attribute] :PostCode)
    (mapping [:Country-attribute] :Country)
    (mapping [:CreationTime-attribute] :CreationTime)
    (mapping [:Validity-attribute] :Validity)))

(for [:glue:Domains :AdminDomain :Services :ComputingService :ComputingEndpoint]
  (add-instance :ComputingEndpoint
    (mapping [:ID :ID-text-node] :ID)
    (mapping [:URL :URL-text-node] :URL)
    (mapping [:Capability :Capability-text-node] :Capability)
    (mapping [:Technology :Technology-text-node] :Technology)
    (mapping [:Interface :Interface-text-node] :Interface)
    (mapping [:WSDL :WSDL-text-node] :WSDL)
    (mapping [:SupportedProfile :SupportedProfile-text-node] :SupportedProfile)
    (mapping [:Semantics :Semantics-text-node] :Semantics)
    (mapping [:Implementor :Implementor-text-node] :Implementor)
    (mapping [:ImplementationName :ImplementationName-text-node] :ImplementationName)
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

(for [:glue:Domains :AdminDomain :Services :ComputingService :ComputingEndpoint :AccessPolicy]
  (add-instance :AccessPolicy
    (mapping [:CreationTime-attribute] :CreationTime)
    (mapping [:Validity-attribute] :Validity)
    (mapping [:LocalID :LocalID-text-node] :LocalID)
    (mapping [:Scheme :Scheme-text-node] :Scheme)
    (mapping [:Rule-text-node :Rule] :Rule)))

(for [:glue:Domains :AdminDomain :Services :ComputingService :ComputingActivitites]
  (add-instance :ComputingActivitites
    (mapping [:ComputingActivitiy] :ComputingActivitiy)))

(for [:glue:Domains :AdminDomain :Services :ComputingService :ComputingActivitites :ComputingActivitiy]
  (add-instance :ComputingActivitiy
    (mapping [:ComputingActivityID :ComputingActivityID-text-node] :ComputingActivityID)))

(for [:glue:Domains :AdminDomain :Services :ComputingService :ComputingShares]
  (add-instance :ComputingShares
    (mapping [:ComputingShares] :ComputingShares)))


(for [:glue:Domains :AdminDomain :Services :ComputingService :ComputingShares :ComputingShare]
  (add-instance :ComputingShare
    (mapping [:LocalID :LocalID-text-node] :LocalID)
    (mapping [:Description] :Description)
    (mapping [:MappingQueue :MappingQueue-text-node] :MappingQueue)
    (mapping [:MaxWallTime :MaxWallTime-text-node] :MaxWallTime)
    (mapping [:MaxTotalWallTime :MaxTotalWallTime-text-node] :MaxTotalWallTime-text-node)
    (maping [:ServingState :ServingState-text-node] :ServingState)))
