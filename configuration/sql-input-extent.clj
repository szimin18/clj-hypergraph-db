(foreach-sql [:activity]
  (add-sql-instance :Activity
    (mapping-sql-pk [:Id] :ID)
    (mapping-sql [:Name] :Name)
    (mapping-sql [:CreationTime] :CreationTime)
    (mapping-sql [:Validity] :Validity))
  (add-sql-association :activity_fk_EndpointId)
  (add-sql-association :activity_fk_ResourceId)
  (add-sql-association :activity_fk_ShareId)
  (add-sql-association :activity_fk_UserDomainId))
;TODO ASSOC ACTIIVITY: ACTIVITY (table "activityactivity" :activityactivity  (column "activityId2" :activityId2 :pk :notnull)  (column "activityId1" :activityId1 :pk :notnull))

(foreach-sql [:contact]
  (add-sql-instance :Contact
    (mapping-sql-pk [:Id] :ID)
    (mapping-sql [:Name] :Name)
    (mapping-sql [:CreationTime] :CreationTime)
    (mapping-sql [:Validity] :Validity)
    (mapping-sql [:Detail] :Detail)
    (mapping-sql [:Type] :Type)))

(foreach-sql [:endpoint]
  (add-sql-instance :Endpoint
    (mapping-sql-pk [:Id] :ID)
    (mapping-sql [:Name] :Name)
    (mapping-sql [:CreationTime] :CreationTime)
    (mapping-sql [:URL] :URL)
    (mapping-sql [:Technology] :Technology)
    (mapping-sql [:InterfaceName] :InterfaceName)
    (mapping-sql [:Implementor] :Implementor)
    (mapping-sql [:ImplementationName] :ImplementationName)
    (mapping-sql [:ImplementationVersion] :ImplementationVersion)
    (mapping-sql [:QualityLevel] :QualityLevel)
    (mapping-sql [:HealthState] :HealthState)
    (mapping-sql [:HealthStateInfo] :HealthStateInfo)
    (mapping-sql [:ServingState] :ServingState)
    (mapping-sql [:StartTime] :StartTime)
    (mapping-sql [:IssuerCA] :IssuerCA)
    (mapping-sql [:DowntimeAnnounce] :DowntimeAnnounce)
    (mapping-sql [:DowntimeInfo] :DowntimeInfo))
  (add-sql-association :endpoint_fk_ServiceId))


(foreach-sql [:location]
  (add-sql-instance :Location
    (mapping-sql-pk [:Id] :ID)
    (mapping-sql [:Name] :Name)
    (mapping-sql [:CreationTime] :CreationTime)
    (mapping-sql [:Address] :Address)
    (mapping-sql [:Validity] :Validity)
    (mapping-sql [:Place] :Place)
    (mapping-sql [:PostCode] :PostCode)
    (mapping-sql [:Latitude] :Latitude)
    (mapping-sql [:Longitude] :Longitude)))

(foreach-sql [:manager]
  (add-sql-instance :Manager
    (mapping-sql-pk [:Id] :ID)
    (mapping-sql [:Name] :Name)
    (mapping-sql [:CreationTime] :CreationTime)
    (mapping-sql [:Validity] :Validity)
    (mapping-sql [:ProductName] :ProductName)
    (mapping-sql [:ProductVersion] :ProductVersion))
  (add-sql-association [:managerId :Id]); ?????
  (add-sql-association :manager_fk_ServiceId))
;TODO ASSOC MANAGER: MANAGER   (column "managerId" :managerId :notnull) // IS IT NOT RELATION!?



(foreach-sql [:resource]
  (add-sql-instance :Resource
    (mapping-sql-pk [:Id] :ID)
    (mapping-sql [:Name] :Name)
    (mapping-sql [:CreationTime] :CreationTime)
    (mapping-sql [:Validity] :Validity))
  (add-sql-association :resource_fk_ManagerId))
;TODO ASSOC RESOURCE: SHARE (table "resourceshare" :resourceshare   (column "resourceId" :resourceId :pk :notnull) (column "shareId" :shareId :pk :notnull))

(foreach-sql [:service]
  (add-sql-instance :Service
    (mapping-sql-pk [:Id] :ID)
    (mapping-sql [:Name] :Name)
    (mapping-sql [:CreationTime] :CreationTime)
    (mapping-sql [:Validity] :Validity)
    (mapping-sql [:Type] :Type)
    (mapping-sql [:QualityLevel] :QualityLevel)
    (mapping-sql [:Complexity] :Complexity))
  (add-sql-association :service_fk_LocationId)
  (add-sql-association :service_fk_AdminDomainId))
;TODO ASSOC SERVICE: CONTACT   (table "serviceservice" :serviceservice (column "serviceId2" :serviceId2 :pk :notnull) (column "contactId" :contactId :pk :notnull))
;TODO ASSOC SERVICE: SERVICE (table "serviceservice" :serviceservice (column "serviceId2" :serviceId2 :pk :notnull) (column "serviceId1" :serviceId1 :pk :notnull))


(foreach-sql [:share]
  (add-sql-instance :Share
    (mapping-sql-pk [:Id] :ID)
    (mapping-sql [:Name] :Name)
    (mapping-sql [:CreationTime] :CreationTime)
    (mapping-sql [:Validity] :Validity)
    (mapping-sql [:Description] :Description))
  (add-sql-association :share_fk_ServiceId))
;TODO ASSOC SHARE: ENDPOINT (table "shareendpoint" :shareendpoint (column "shareId" :shareId :pk :notnull) (column "endpointId" :endpointId :pk :notnull))

(foreach-sql [:admindomain]
  (add-sql-instance :AdminDomain (mapping-sql-pk [:Id] :ID)
    (mapping-sql [:domain :Description] :Description))
  (add-sql-association :adminDomain_fk_AdminDomainId))
;TODO ASSOC ADMINDOMAIN: ADMINDOMAIN   (column "adminDomainId" :adminDomainId)


(foreach-sql [:domain]
  (add-sql-instance :Domain
    (mapping-sql-pk [:Id] :ID)
    (mapping-sql [:Name] :Name)
    (mapping-sql [:CreationTime] :CreationTime)
    (mapping-sql [:Validity] :Validity)
    (mapping-sql [:Description] :Description))
  (add-sql-association :domain_fk_LocationId))
;TODO ASSOC DOMAIN: CONTACT  (table "domaincontact" :domaincontact   (column "domainId" :domainId :pk :notnull)  (column "contactId" :contactId :pk :notnull))

(foreach-sql [:userdomain]
  (add-sql-instance :UserDomain
    (mapping-sql-pk [:Id] :ID)
    (mapping-sql [:Level] :Level))
  (add-sql-association :userDomain_fk_UserDomainId))
;TODO ASSOC USERDOMAIN: POLICY (table "userdomainpolicy" :userdomainpolicy (column "userDomainId" :userDomainId :pk :notnull) (column "policyId" :policyId :pk :notnull))

(foreach-sql [:policy]
  (add-sql-instance :Policy
    (mapping-sql-pk [:Id] :ID)
    (mapping-sql [:Name] :Name)
    (mapping-sql [:CreationTime] :CreationTime)
    (mapping-sql [:Validity] :Validity)
    (mapping-sql [:Scheme] :Scheme)))


(foreach-sql [:mapping-sqlpolicy]
  (add-sql-instance :mapping-sqlPolicy
    (mapping-sql-pk [:Id] :ID)
    (mapping-sql [:Name] :Name)
    (mapping-sql [:CreationTime] :CreationTime)
    (mapping-sql [:Validity] :Validity)
    (mapping-sql [:Scheme] :Scheme))
  (add-sql-association :mapping-sqlPolicy_fk_ShareId))

(foreach-sql [:accesspolicy]
  (add-sql-instance :AcessPolicy
    (mapping-sql-pk [:Id] :Id))
  (add-sql-association :accessPolicy_fk_EndpointId))
