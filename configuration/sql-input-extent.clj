(foreach [:accesspolicy]
  (add-instance :AcessPolicy
    (mapping-pk [:Id] :Id)))
;ASSOC ACCESSPOLICY: ENDPOINT  (column "endpointId" :endpointId :notnull))

(foreach [:activity]
  (add-isntance :Activity
    (mapping-pk [:Id] :ID)
    (mapping [:Name] :Name)
    (mapping [:CreationTime] :CreationTime)
    (mapping [:Validity] :Validity)))
;ASSOC ACTIIVITY: ENDPOINT  (column "endpointId" :endpointId)
;ASSOC ACTIIVITY: USERDOMAIN (column "userDomainId" :userDomainId)
;ASSOC ACTIIVITY: RESOURCE (column "resourceId" :resourceId))
;ASSOC ACTIIVITY: SHARE (column "shareId" :shareId)
;ASSOC ACTIIVITY: ACTIVITY (table "activityactivity" :activityactivity  (column "activityId2" :activityId2 :pk :notnull)  (column "activityId1" :activityId1 :pk :notnull))

(foreach [:admindomain]
  (add-instance :AdminDomain
    (mapping-pk [:Id] :ID)
    (mapping [:Distributed] :Distributed)
    (mapping [:domain :Name] :Name)
    (mapping [:domain :CreationTime] :CreationTime)
    (mapping [:domain :Validity] :Validity)
    (mapping [:domain :Description] :Description)))
;ASSOC ADMINDOMAIN: ADMINDOMAIN   (column "adminDomainId" :adminDomainId)
;ASSOC ADMINDOMAIN: LOCATION  (table "domain" :domain (column "locationId" :locationId))
;ASSOC ADMINDOMAIN: CONTACT  (table "domaincontact" :domaincontact   (column "domainId" :domainId :pk :notnull)  (column "contactId" :contactId :pk :notnull))

(foreach [:contact]
  (add-instance :Contact
    (mapping-pk [:Id] :ID)
    (mapping [:Name] :Name)
    (mapping [:CreationTime] :CreationTime)
    (mapping [:Validity] :Validity)
    (mapping [:Detail] :Detail)
    (mapping [:Type] :Type)))

(foreach [:endpoint]
  (add-instance :Endpoint
    (mapping-pk [:Id] :ID)
    (mapping [:Name] :Name)
    (mapping [:CreationTime] :CreationTime)
    (mapping [:URL] :URL)
    (mapping [:Technology] :Technology)
    (mapping [:InterfaceName] :InterfaceName)
    (mapping [:Implementor] :Implementor)
    (mapping [:ImplementationName] :ImplementationName)
    (mapping [:ImplementationVersion] :ImplementationVersion)
    (mapping [:QualityLevel] :QualityLevel)
    (mapping [:HealthState] :HealthState)
    (mapping [:HealthStateInfo] :HealthStateInfo)
    (mapping [:ServingState] :ServingState)
    (mapping [:StartTime] :StartTime)
    (mapping [:IssuerCA] :IssuerCA)
    (mapping [:DowntimeAnnounce] :DowntimeAnnounce)
    (mapping [:DowntimeInfo] :DowntimeInfo)))
;ASSOC ENDPOINT: SERVICE   (column "serviceId" :serviceId :notnull)

(foreach [:location]
  (add-instance :Location
    (mapping-pk [:Id] :ID)
    (mapping [:Name] :Name)
    (mapping [:CreationTime] :CreationTime)
    (mapping [:Address] :Address)
    (mapping [:Validity] :Validity)
    (mapping [:Place] :Place)
    (mapping [:PostCode] :PostCode)
    (mapping [:Latitude] :Latitude)
    (mapping [:Longitude] :Longitude)))

(foreach [:manager]
  (add-instance :Manager
    (mapping-pk [:Id] :ID)
    (mapping [:Name] :Name)
    (mapping [:CreationTime] :CreationTime)
    (mapping [:Validity] :Validity)
    (mapping [:ProductName] :ProductName)
    (mapping [:ProductVersion] :ProductVersion)))
;ASSOC MANAGER: SERVICE   (column "serviceId" :serviceId :notnull)
;ASSOC MANAGER: MANAGER   (column "managerId" :managerId :notnull)

(foreach [:resource]
  (add-instance :Resource
    (mapping-pk [:Id] :ID)
    (mapping [:Name] :Name)
    (mapping [:CreationTime] :CreationTime)
    (mapping [:Validity] :Validity)))
;ASSOC RESOURCE: MANAGER   (column "managerId" :managerId :notnull)
;ASSOC RESOURCE: SHARE (table "resourceshare" :resourceshare   (column "resourceId" :resourceId :pk :notnull) (column "shareId" :shareId :pk :notnull))

(foreach [:service]
  (add-instance :Service
    (mapping-pk [:Id] :ID)
    (mapping [:Name] :Name)
    (mapping [:CreationTime] :CreationTime)
    (mapping [:Validity] :Validity)
    (mapping [:Type] :Type)
    (mapping [:QualityLevel] :QualityLevel)
    (mapping [:Complexity] :Complexity)))
;ASSOC SERVICE: LOCATION   (column "locationId" :locationId)
;ASSOC SERVICE: ADMINDOMAIN   (column "adminDomainId" :adminDomainId)
;ASSOC SERVICE: CONTACT   (table "serviceservice" :serviceservice (column "serviceId2" :serviceId2 :pk :notnull) (column "contactId" :contactId :pk :notnull))
;ASSOC SERVICE: SERVICE (table "serviceservice" :serviceservice (column "serviceId2" :serviceId2 :pk :notnull) (column "serviceId1" :serviceId1 :pk :notnull))

(foreach [:share]
  (add-instance :Share
    (mapping-pk [:Id] :ID)
    (mapping [:Name] :Name)
    (mapping [:CreationTime] :CreationTime)
    (mapping [:Validity] :Validity)
    (mapping [:Description] :Description)))
;ASSOC SHARE: SERVICE   (column "serviceId" :serviceId :notnull)
;ASSOC SHARE: ENDPOINT (table "shareendpoint" :shareendpoint (column "shareId" :shareId :pk :notnull) (column "endpointId" :endpointId :pk :notnull))

(table "domain" :domain
  (column "Id" :Id :pk :notnull)
  (column "Name" :Name)
  (column "CreationTime" :CreationTime :notnull)
  (column "Validity" :Validity)
  (column "Description" :Description)
  (column "locationId" :locationId))

(table "userdomain" :userdomain
  (column "Id" :Id :pk :notnull)
  (column "Level" :Level)
  (column "userDomainId" :userDomainId))

(table "userdomainpolicy" :userdomainpolicy
  (column "userDomainId" :userDomainId :pk :notnull)
  (column "policyId" :policyId :pk :notnull))



;????????????????????????????
(table "mappingpolicy" :mappingpolicy
  (column "Id" :Id :pk :notnull)
  (column "Name" :Name)
  (column "CreationTime" :CreationTime :notnull)
  (column "Validity" :Validity)
  (column "Scheme" :Scheme :notnull)
  (column "shareId" :shareId :notnull))

(table "policy" :policy
  (column "Id" :Id :pk :notnull)
  (column "Name" :Name)
  (column "CreationTime" :CreationTime :notnull)
  (column "Validity" :Validity)
  (column "Scheme" :Scheme :notnull))