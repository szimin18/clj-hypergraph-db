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

(table "admindomain" :admindomain
       (column "Id" :Id :pk :notnull)
       (column "Distributed" :Distributed)
       (column "adminDomainId" :adminDomainId))

(table "contact" :contact
       (column "Id" :Id :pk :notnull)
       (column "Name" :Name)
       (column "CreationTime" :CreationTime :notnull)
       (column "Validity" :Validity)
       (column "Detail" :Detail :notnull)
       (column "Type" :Type :notnull))

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

(foreignkey "accessPolicy_fk_EndpointId" :accessPolicy_fk_EndpointId
            (reference :accesspolicy :endpointId :endpoint :Id))

(foreignkey "accessPolicy_fk_Id" :accessPolicy_fk_Id
            (reference :accesspolicy :Id :policy :Id))

(foreignkey "activity_fk_ResourceId" :activity_fk_ResourceId
            (reference :activity :resourceId :resource :Id))

(foreignkey "activity_fk_EndpointId" :activity_fk_EndpointId
            (reference :activity :endpointId :endpoint :Id))

(foreignkey "activity_fk_ShareId" :activity_fk_ShareId
            (reference :activity :shareId :share :Id))

(foreignkey "activity_fk_UserDomainId" :activity_fk_UserDomainId
            (reference :activity :userDomainId :userdomain :Id))

(foreignkey "activityActivity_fk_ActivityId1" :activityActivity_fk_ActivityId1
            (reference :activityactivity :activityId1 :activity :Id))

(foreignkey "activityActivity_fk_ActivityId2" :activityActivity_fk_ActivityId2
            (reference :activityactivity :activityId2 :activity :Id))

(foreignkey "adminDomain_fk_AdminDomainId" :adminDomain_fk_AdminDomainId
            (reference :admindomain :adminDomainId :admindomain :Id))

(foreignkey "adminDomain_fk_Id" :adminDomain_fk_Id
            (reference :admindomain :Id :domain :Id))

(foreignkey "domain_fk_LocationId" :domain_fk_LocationId
            (reference :domain :locationId :location :Id))

(foreignkey "domainContact_fk_ContactId" :domainContact_fk_ContactId
            (reference :domaincontact :contactId :contact :Id))

(foreignkey "domainContact_fk_DomainId" :domainContact_fk_DomainId
            (reference :domaincontact :domainId :domain :Id))

(foreignkey "endpoint_fk_ServiceId" :endpoint_fk_ServiceId
            (reference :endpoint :serviceId :service :Id))

(foreignkey "extension_fk_PolicyId" :extension_fk_PolicyId
            (reference :extension :policyId :policy :Id))

(foreignkey "extension_fk_ActivityId" :extension_fk_ActivityId
            (reference :extension :activityId :activity :Id))

(foreignkey "extension_fk_ContactId" :extension_fk_ContactId
            (reference :extension :contactId :contact :Id))

(foreignkey "extension_fk_DomainId" :extension_fk_DomainId
            (reference :extension :domainId :domain :Id))

(foreignkey "extension_fk_EndpointId" :extension_fk_EndpointId
            (reference :extension :endpointId :endpoint :Id))

(foreignkey "extension_fk_LocationId" :extension_fk_LocationId
            (reference :extension :locationId :location :Id))

(foreignkey "extension_fk_ManagerId" :extension_fk_ManagerId
            (reference :extension :managerId :manager :Id))

(foreignkey "extension_fk_ResourceId" :extension_fk_ResourceId
            (reference :extension :resourceId :resource :Id))

(foreignkey "extension_fk_ServiceId" :extension_fk_ServiceId
            (reference :extension :serviceId :service :Id))

(foreignkey "extension_fk_ShareId" :extension_fk_ShareId
            (reference :extension :shareId :share :Id))

(foreignkey "manager_fk_ServiceId" :manager_fk_ServiceId
            (reference :manager :serviceId :service :Id))

(foreignkey "mappingPolicy_fk_ShareId" :mappingPolicy_fk_ShareId
            (reference :mappingpolicy :shareId :share :Id))

(foreignkey "mappingPolicy_fk_Id" :mappingPolicy_fk_Id
            (reference :mappingpolicy :Id :policy :Id))

(foreignkey "resource_fk_ManagerId" :resource_fk_ManagerId
            (reference :resource :managerId :manager :Id))

(foreignkey "resourceShare_fk_ShareId" :resourceShare_fk_ShareId
            (reference :resourceshare :shareId :share :Id))

(foreignkey "resourceShare_fk_ResourceId" :resourceShare_fk_ResourceId
            (reference :resourceshare :resourceId :resource :Id))

(foreignkey "service_fk_LocationId" :service_fk_LocationId
            (reference :service :locationId :location :Id))

(foreignkey "service_fk_AdminDomainId" :service_fk_AdminDomainId
            (reference :service :adminDomainId :admindomain :Id))

(foreignkey "serviceContact_fk_ContactId" :serviceContact_fk_ContactId
            (reference :servicecontact :contactId :contact :Id))

(foreignkey "serviceContact_fk_ServiceId" :serviceContact_fk_ServiceId
            (reference :servicecontact :serviceId :service :Id))

(foreignkey "serviceService_fk_ServiceId1" :serviceService_fk_ServiceId1
            (reference :serviceservice :serviceId1 :service :Id))

(foreignkey "serviceService_fk_ServiceId2" :serviceService_fk_ServiceId2
            (reference :serviceservice :serviceId2 :service :Id))

(foreignkey "share_fk_ServiceId" :share_fk_ServiceId
            (reference :share :serviceId :service :Id))

(foreignkey "shareEndpoint_fk_EndpointId" :shareEndpoint_fk_EndpointId
            (reference :shareendpoint :endpointId :endpoint :Id))

(foreignkey "shareEndpoint_fk_ShareId" :shareEndpoint_fk_ShareId
            (reference :shareendpoint :shareId :share :Id))

(foreignkey "userDomain_fk_UserDomainId" :userDomain_fk_UserDomainId
            (reference :userdomain :userDomainId :userdomain :Id))

(foreignkey "userDomain_fk_Id" :userDomain_fk_Id
            (reference :userdomain :Id :domain :Id))

(foreignkey "userDomainPolicy_fk_PolicyId" :userDomainPolicy_fk_PolicyId
            (reference :userdomainpolicy :policyId :policy :Id))

(foreignkey "userDomainPolicy_fk_UserDomainId" :userDomainPolicy_fk_UserDomainId
            (reference :userdomainpolicy :userDomainId :userdomain :Id))

