(foreach [:activity]
         (add-instance :Activity
                       (mapping-pk [:Id] :ID)
                       (mapping [:Name] :Name)
                       (mapping [:CreationTime] :CreationTime)
                       (mapping [:Validity] :Validity))
         (add-association :activity_fk_EndpointId)
         (add-association :activity_fk_ResourceId)
         (add-association :activity_fk_ShareId)
         (add-association :activity_fk_UserDomainId))
;TODO ASSOC ACTIIVITY: ACTIVITY

(foreach [:activityactivity]
         (add-association :RelatesToActivityActivity
                          (mapping [:Activity] :activityId1)
                          (mapping [:ToActivity] :activityId2))

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
                       (mapping [:DowntimeInfo] :DowntimeInfo))
         (add-association :endpoint_fk_ServiceId))


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
                       (mapping [:ProductVersion] :ProductVersion))
         (add-association [:managerId :Id]); ?????
         (add-association :manager_fk_ServiceId))
;TODO ASSOC MANAGER: MANAGER   (column "managerId" :managerId :notnull) // IS IT NOT RELATION!?



(foreach [:resource]
         (add-instance :Resource
                       (mapping-pk [:Id] :ID)
                       (mapping [:Name] :Name)
                       (mapping [:CreationTime] :CreationTime)
                       (mapping [:Validity] :Validity))
         (add-association :resource_fk_ManagerId))
;TODO ASSOC RESOURCE: SHARE (table "resourceshare" :resourceshare   (column "resourceId" :resourceId :pk :notnull) (column "shareId" :shareId :pk :notnull))

(foreach [:service]
         (add-instance :Service
                       (mapping-pk [:Id] :ID)
                       (mapping [:Name] :Name)
                       (mapping [:CreationTime] :CreationTime)
                       (mapping [:Validity] :Validity)
                       (mapping [:Type] :Type)
                       (mapping [:QualityLevel] :QualityLevel)
                       (mapping [:Complexity] :Complexity))
         (add-association :service_fk_LocationId)
         (add-association :service_fk_AdminDomainId))
;TODO ASSOC SERVICE: CONTACT   (table "serviceservice" :serviceservice (column "serviceId2" :serviceId2 :pk :notnull) (column "contactId" :contactId :pk :notnull))
;TODO ASSOC SERVICE: SERVICE (table "serviceservice" :serviceservice (column "serviceId2" :serviceId2 :pk :notnull) (column "serviceId1" :serviceId1 :pk :notnull))


(foreach [:share]
         (add-instance :Share
                       (mapping-pk [:Id] :ID)
                       (mapping [:Name] :Name)
                       (mapping [:CreationTime] :CreationTime)
                       (mapping [:Validity] :Validity)
                       (mapping [:Description] :Description))
         (add-association :share_fk_ServiceId))
;TODO ASSOC SHARE: ENDPOINT (table "shareendpoint" :shareendpoint (column "shareId" :shareId :pk :notnull) (column "endpointId" :endpointId :pk :notnull))

(foreach [:admindomain]
         (add-instance :AdminDomain (mapping-pk [:Id] :ID)
                       (mapping [:domain :Description] :Description))
         (add-association :adminDomain_fk_AdminDomainId))
;TODO ASSOC ADMINDOMAIN: ADMINDOMAIN   (column "adminDomainId" :adminDomainId)


(foreach [:domain]
         (add-instance :Domain
                       (mapping-pk [:Id] :ID)
                       (mapping [:Name] :Name)
                       (mapping [:CreationTime] :CreationTime)
                       (mapping [:Validity] :Validity)
                       (mapping [:Description] :Description))
         (add-association :domain_fk_LocationId))
;TODO ASSOC DOMAIN: CONTACT  (table "domaincontact" :domaincontact   (column "domainId" :domainId :pk :notnull)  (column "contactId" :contactId :pk :notnull))

(foreach [:userdomain]
         (add-instance :UserDomain
                       (mapping-pk [:Id] :ID)
                       (mapping [:Level] :Level))
         (add-association :userDomain_fk_UserDomainId))
;TODO ASSOC USERDOMAIN: POLICY (table "userdomainpolicy" :userdomainpolicy (column "userDomainId" :userDomainId :pk :notnull) (column "policyId" :policyId :pk :notnull))

(foreach [:policy]
         (add-instance :Policy
                       (mapping-pk [:Id] :ID)
                       (mapping [:Name] :Name)
                       (mapping [:CreationTime] :CreationTime)
                       (mapping [:Validity] :Validity)
                       (mapping [:Scheme] :Scheme)))


(foreach [:mappingpolicy]
         (add-instance :mappingPolicy
                       (mapping-pk [:Id] :ID)
                       (mapping [:Name] :Name)
                       (mapping [:CreationTime] :CreationTime)
                       (mapping [:Validity] :Validity)
                       (mapping [:Scheme] :Scheme))
         (add-association :mappingPolicy_fk_ShareId))

(foreach [:accesspolicy]
         (add-instance :AcessPolicy
                       (mapping-pk [:Id] :Id))
         (add-association :accessPolicy_fk_EndpointId))
