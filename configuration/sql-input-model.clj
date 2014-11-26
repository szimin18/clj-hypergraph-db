(database :sql
          (default-credentials "glue_ogf" "user" "password"))

(table "accesspolicy" :accesspolicy
       (column "Id" :Id :pk :notnull)
       (column "endpointId" :endpointId :notnull))

(table "activity" :activity
       (column "Id" :Id :pk :notnull)
       (column "Name" :Name)
       (column "CreationTime" :CreationTime :notnull)
       (column "Validity" :Validity)
       (column "endpointId" :endpointId)
       (column "userDomainId" :userDomainId)
       (column "shareId" :shareId)
       (column "resourceId" :resourceId))

(table "activityactivity" :activityactivity
       (column "activityId2" :activityId2 :pk :notnull)
       (column "activityId1" :activityId1 :pk :notnull))

(table "contact" :contact
       (column "Id" :Id :pk :notnull)
       (column "Name" :Name)
       (column "CreationTime" :CreationTime :notnull)
       (column "Validity" :Validity)
       (column "Detail" :Detail :notnull)
       (column "Type" :Type :notnull))

(table "admindomain" :admindomain
       (column "Id" :Id :pk :notnull)
       (column "Distributed" :Distributed)
       (column "adminDomainId" :adminDomainId))

(table "domain" :domain
       (column "Id" :Id :pk :notnull)
       (column "Name" :Name)
       (column "CreationTime" :CreationTime :notnull)
       (column "Validity" :Validity)
       (column "Description" :Description)
       (column "locationId" :locationId))

(table "domaincontact" :domaincontact
       (column "domainId" :domainId :pk :notnull)
       (column "contactId" :contactId :pk :notnull))


(table "endpoint" :endpoint
       (column "Id" :Id :pk :notnull)
       (column "Name" :Name)
       (column "CreationTime" :CreationTime :notnull)
       (column "Validity" :Validity)
       (column "URL" :URL :notnull)
       (column "Technology" :Technology)
       (column "InterfaceName" :InterfaceName :notnull)
       (column "Implementor" :Implementor)
       (column "ImplementationName" :ImplementationName)
       (column "ImplementationVersion" :ImplementationVersion)
       (column "QualityLevel" :QualityLevel :notnull)
       (column "HealthState" :HealthState :notnull)
       (column "HealthStateInfo" :HealthStateInfo)
       (column "ServingState" :ServingState :notnull)
       (column "StartTime" :StartTime)
       (column "IssuerCA" :IssuerCA)
       (column "DowntimeAnnounce" :DowntimeAnnounce)
       (column "DowntimeStart" :DowntimeStart)
       (column "DowntimeEnd" :DowntimeEnd)
       (column "DowntimeInfo" :DowntimeInfo)
       (column "serviceId" :serviceId :notnull))

(table "extension" :extension
       (column "LocalID" :LocalID :pk :notnull)
       (column "Key" :Key :notnull)
       (column "Value" :Value :notnull)
       (column "locationId" :locationId)
       (column "contactId" :contactId)
       (column "serviceId" :serviceId)
       (column "domainId" :domainId)
       (column "endpointId" :endpointId)
       (column "shareId" :shareId)
       (column "managerId" :managerId)
       (column "resourceId" :resourceId)
       (column "activityId" :activityId)
       (column "policyId" :policyId))

(table "location" :location
       (column "Id" :Id :pk :notnull)
       (column "Name" :Name)
       (column "CreationTime" :CreationTime :notnull)
       (column "Validity" :Validity)
       (column "Address" :Address)
       (column "Place" :Place)
       (column "Country" :Country)
       (column "PostCode" :PostCode)
       (column "Latitude" :Latitude)
       (column "Longitude" :Longitude))

(table "manager" :manager
       (column "Id" :Id :pk :notnull)
       (column "Name" :Name)
       (column "CreationTime" :CreationTime :notnull)
       (column "Validity" :Validity)
       (column "ProductName" :ProductName :notnull)
       (column "ProductVersion" :ProductVersion)
       (column "serviceId" :serviceId :notnull)
       (column "managerId" :managerId :notnull))

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

(table "resource" :resource
       (column "Id" :Id :pk :notnull)
       (column "Name" :Name)
       (column "CreationTime" :CreationTime :notnull)
       (column "Validity" :Validity)
       (column "managerId" :managerId :notnull))

(table "resourceshare" :resourceshare
       (column "resourceId" :resourceId :pk :notnull)
       (column "shareId" :shareId :pk :notnull))

(table "service" :service
       (column "Id" :Id :pk :notnull)
       (column "Name" :Name)
       (column "CreationTime" :CreationTime :notnull)
       (column "Validity" :Validity)
       (column "Type" :Type :notnull)
       (column "QualityLevel" :QualityLevel :notnull)
       (column "Complexity" :Complexity)
       (column "locationId" :locationId)
       (column "adminDomainId" :adminDomainId))

(table "servicecontact" :servicecontact
       (column "serviceId" :serviceId :pk :notnull)
       (column "contactId" :contactId :pk :notnull))

(table "serviceservice" :serviceservice
       (column "serviceId2" :serviceId2 :pk :notnull)
       (column "serviceId1" :serviceId1 :pk :notnull))

(table "share" :share
       (column "Id" :Id :pk :notnull)
       (column "Name" :Name)
       (column "CreationTime" :CreationTime :notnull)
       (column "Validity" :Validity)
       (column "Description" :Description)
       (column "serviceId" :serviceId :notnull))

(table "shareendpoint" :shareendpoint
       (column "shareId" :shareId :pk :notnull)
       (column "endpointId" :endpointId :pk :notnull))

(table "userdomain" :userdomain
       (column "Id" :Id :pk :notnull)
       (column "Level" :Level)
       (column "userDomainId" :userDomainId))

(table "userdomainpolicy" :userdomainpolicy
       (column "userDomainId" :userDomainId :pk :notnull)
       (column "policyId" :policyId :pk :notnull))

(relation "accessPolicy_fk_EndpointId" :accessPolicy_fk_EndpointId
          (between :accesspolicy :endpoint)
          (referring :endpointId :Id))

(relation "accessPolicy_fk_Id" :accessPolicy_fk_Id
          (between :accesspolicy :policy)
          (referring :Id :Id))

(relation "activity_fk_EndpointId" :activity_fk_EndpointId
          (between :activity :endpoint)
          (referring :endpointId :Id))

(relation "activity_fk_ResourceId" :activity_fk_ResourceId
          (between :activity :resource)
          (referring :resourceId :Id))

(relation "activity_fk_ShareId" :activity_fk_ShareId
          (between :activity :share)
          (referring :shareId :Id))

(relation "activity_fk_UserDomainId" :activity_fk_UserDomainId
          (between :activity :userdomain)
          (referring :userDomainId :Id))

(relation "activityActivity_fk_ActivityId1" :activityActivity_fk_ActivityId1
          (between :activityactivity :activity)
          (referring :activityId1 :Id))

(relation "activityActivity_fk_ActivityId2" :activityActivity_fk_ActivityId2
          (between :activityactivity :activity)
          (referring :activityId2 :Id))

(relation "adminDomain_fk_AdminDomainId" :adminDomain_fk_AdminDomainId
          (between :admindomain :admindomain)
          (referring :adminDomainId :Id))

(relation "adminDomain_fk_Id" :adminDomain_fk_Id
          (between :admindomain :domain)
          (referring :Id :Id))

(relation "domain_fk_LocationId" :domain_fk_LocationId
          (between :domain :location)
          (referring :locationId :Id))

(relation "domainContact_fk_ContactId" :domainContact_fk_ContactId
          (between :domaincontact :contact)
          (referring :contactId :Id))

(relation "domainContact_fk_DomainId" :domainContact_fk_DomainId
          (between :domaincontact :domain)
          (referring :domainId :Id))

(relation "endpoint_fk_ServiceId" :endpoint_fk_ServiceId
          (between :endpoint :service)
          (referring :serviceId :Id))

(relation "extension_fk_ActivityId" :extension_fk_ActivityId
          (between :extension :activity)
          (referring :activityId :Id))

(relation "extension_fk_ContactId" :extension_fk_ContactId
          (between :extension :contact)
          (referring :contactId :Id))

(relation "extension_fk_DomainId" :extension_fk_DomainId
          (between :extension :domain)
          (referring :domainId :Id))

(relation "extension_fk_EndpointId" :extension_fk_EndpointId
          (between :extension :endpoint)
          (referring :endpointId :Id))

(relation "extension_fk_LocationId" :extension_fk_LocationId
          (between :extension :location)
          (referring :locationId :Id))

(relation "extension_fk_ManagerId" :extension_fk_ManagerId
          (between :extension :manager)
          (referring :managerId :Id))

(relation "extension_fk_PolicyId" :extension_fk_PolicyId
          (between :extension :policy)
          (referring :policyId :Id))

(relation "extension_fk_ResourceId" :extension_fk_ResourceId
          (between :extension :resource)
          (referring :resourceId :Id))

(relation "extension_fk_ServiceId" :extension_fk_ServiceId
          (between :extension :service)
          (referring :serviceId :Id))

(relation "extension_fk_ShareId" :extension_fk_ShareId
          (between :extension :share)
          (referring :shareId :Id))

(relation "manager_fk_ServiceId" :manager_fk_ServiceId
          (between :manager :service)
          (referring :serviceId :Id))

(relation "mappingPolicy_fk_Id" :mappingPolicy_fk_Id
          (between :mappingpolicy :policy)
          (referring :Id :Id))

(relation "mappingPolicy_fk_ShareId" :mappingPolicy_fk_ShareId
          (between :mappingpolicy :share)
          (referring :shareId :Id))

(relation "resource_fk_ManagerId" :resource_fk_ManagerId
          (between :resource :manager)
          (referring :managerId :Id))

(relation "resourceShare_fk_ResourceId" :resourceShare_fk_ResourceId
          (between :resourceshare :resource)
          (referring :resourceId :Id))

(relation "resourceShare_fk_ShareId" :resourceShare_fk_ShareId
          (between :resourceshare :share)
          (referring :shareId :Id))

(relation "service_fk_AdminDomainId" :service_fk_AdminDomainId
          (between :service :admindomain)
          (referring :adminDomainId :Id))

(relation "service_fk_LocationId" :service_fk_LocationId
          (between :service :location)
          (referring :locationId :Id))

(relation "serviceContact_fk_ContactId" :serviceContact_fk_ContactId
          (between :servicecontact :contact)
          (referring :contactId :Id))

(relation "serviceContact_fk_ServiceId" :serviceContact_fk_ServiceId
          (between :servicecontact :service)
          (referring :serviceId :Id))

(relation "serviceService_fk_ServiceId1" :serviceService_fk_ServiceId1
          (between :serviceservice :service)
          (referring :serviceId1 :Id))

(relation "serviceService_fk_ServiceId2" :serviceService_fk_ServiceId2
          (between :serviceservice :service)
          (referring :serviceId2 :Id))

(relation "share_fk_ServiceId" :share_fk_ServiceId
          (between :share :service)
          (referring :serviceId :Id))

(relation "shareEndpoint_fk_EndpointId" :shareEndpoint_fk_EndpointId
          (between :shareendpoint :endpoint)
          (referring :endpointId :Id))

(relation "shareEndpoint_fk_ShareId" :shareEndpoint_fk_ShareId
          (between :shareendpoint :share)
          (referring :shareId :Id))

(relation "userDomain_fk_Id" :userDomain_fk_Id
          (between :userdomain :domain)
          (referring :Id :Id))

(relation "userDomain_fk_UserDomainId" :userDomain_fk_UserDomainId
          (between :userdomain :userdomain)
          (referring :userDomainId :Id))

(relation "userDomainPolicy_fk_PolicyId" :userDomainPolicy_fk_PolicyId
          (between :userdomainpolicy :policy)
          (referring :policyId :Id))

(relation "userDomainPolicy_fk_UserDomainId" :userDomainPolicy_fk_UserDomainId
          (between :userdomainpolicy :userdomain)
          (referring :userDomainId :Id))
