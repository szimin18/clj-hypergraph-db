drop table if exists ExecutionEnvironmentApplicationEnvironment ;
drop table if exists DomainContact ;
drop table if exists UserDomainPolicy ;
drop table if exists ServiceContact ;
drop table if exists ServiceService ;
drop table if exists ActivityActivity ;
drop table if exists ShareEndpoint ;
drop table if exists ResourceShare ;
drop table if exists ComputingManager ;
drop table if exists ComputingShare ;
drop table if exists ComputingService ;
drop table if exists ComputingEndpoint ;
drop table if exists Benchmark ;
drop table if exists ApplicationEnvironment ;
drop table if exists ToStorageService ;
drop table if exists ComputingActivity ;
drop table if exists ApplicationHandle ;
drop table if exists ExecutionEnvironment ;
drop table if exists ComputingEndpoint_MVA ;
drop table if exists ComputingManager_MVA ;
drop table if exists ExecutionEnvironment_MVA ;
drop table if exists ApplicationEnvironment_MVA ;
drop table if exists ComputingActivity_MVA ;
drop table if exists ComputingShare_MVA ;
drop table if exists Location ;
drop table if exists Activity ;
drop table if exists AdminDomain ;
drop table if exists Contact ;
drop table if exists Service ;
drop table if exists Service ;
drop table if exists Endpoint ;
drop table if exists AccessPolicy ;
drop table if exists UserDomain ;
drop table if exists MappingPolicy ;
drop table if exists Domain ;
drop table if exists Policy ;
drop table if exists Share ;
drop table if exists Manager ;
drop table if exists Resource ;
drop table if exists Domain_MVA ;
drop table if exists AdminDomain_MVA ;
drop table if exists UserDomain_MVA ;
drop table if exists Service_MVA ;
drop table if exists Endpoint_MVA ;
drop table if exists Policy_MVA ;
drop table if exists Extension ;
drop table if exists StorageService ;
drop table if exists StorageServiceCapacity ;
drop table if exists StorageAccessProtocol ;
drop table if exists StorageEndpoint ;
drop table if exists StorageShare ;
drop table if exists StorageShareCapacity ;
drop table if exists StorageManager ;
drop table if exists DataStore ;
drop table if exists ToComputingService ;
drop table if exists StorageShare_MVA ;
drop table if exists EntityInfo ;



-- EntityOtherInfo
-- Common extension
create table EntityInfo (
  Id                   	    varchar(255) not null,
  OtherInfo                 varchar(255) not null
);

-- DomainContact
-- Association between Domain and Contact
create table DomainContact (
  domainId                  varchar(255) not null,
  contactId                 varchar(255) not null
) ;

-- UserDomainPolicy
-- Association between UserDomain and Policy
create table UserDomainPolicy (
  userDomainId              varchar(255) not null,
  policyId                  varchar(255) not null
) ;

-- ServiceContact
-- Association between Service and Contact
create table ServiceContact (
  serviceId                 varchar(255) not null,
  contactId                 varchar(255) not null
) ;

-- ServiceService
-- Association between Service and Service
create table ServiceService (
  serviceId2                varchar(255) not null,
  serviceId1                varchar(255) not null
) ;

-- ActivityActivity
-- Association between Activity and Activity
create table ActivityActivity (
  activityId2               varchar(255) not null,
  activityId1               varchar(255) not null
) ;

-- ShareEndpoint
-- Association between Share and Endpoint
create table ShareEndpoint (
  shareId                   varchar(255) not null,
  endpointId                varchar(255) not null
) ;

-- ResourceShare
-- Association between Resource and Share
create table ResourceShare (
  resourceId                varchar(255) not null,
  shareId                   varchar(255) not null
) ;

-- Location
create table Location (
  Id                        varchar(255) not null,
  Name                      varchar(255),
  CreationTime              timestamp,
  Validity                  integer,
  Address                   varchar(255),
  Place                     varchar(255),
  Country                   varchar(255),
  PostCode                  varchar(255),
  Latitude                  real,
  Longitude                 real
) ;

-- Activity
create table Activity (
  Id                        varchar(255) not null,
  Name                      varchar(255),
  CreationTime              timestamp,
  Validity                  integer,
  endpointId                varchar(255),
  userDomainId              varchar(255),
  shareId                   varchar(255),
  resourceId                varchar(255)
) ;

-- AdminDomain
create table AdminDomain (
  Id                        varchar(255) not null,
  Distributed               varchar(255),
  Owner 		    varchar(255),
  adminDomainId             varchar(255)
) ;

-- Contact
create table Contact (
  Id                        varchar(255) not null,
  Name                      varchar(255),
  CreationTime              timestamp,
  Validity                  integer,
  Detail                    varchar(255) not null,
  Type                      varchar(255) not null
) ;

-- Service
create table Service (
  Id                        varchar(255) not null,
  Name                      varchar(255),
  CreationTime              timestamp,
  Validity                  integer,
  Type                      varchar(255) not null,
  QualityLevel              varchar(255) not null,
  Complexity                varchar(255),
  locationId                varchar(255),
  adminDomainId             varchar(255)
) ;

-- Endpoint
create table Endpoint (
  Id                        varchar(255) not null,
  Name                      varchar(255),
  CreationTime              timestamp,
  Validity                  integer,
  URL                       varchar(255) not null,
  Technology                varchar(255),
  InterfaceName             varchar(255) not null,
  Implementor               varchar(255),
  ImplementationName        varchar(255),
  ImplementationVersion     varchar(255),
  QualityLevel              varchar(255) not null,
  HealthState               varchar(255) not null,
  HealthStateInfo           varchar(255),
  ServingState              varchar(255) not null,
  StartTime                 varchar(255),
  IssuerCA                  varchar(255),
  DowntimeAnnounce          varchar(255),
  DowntimeStart             varchar(255),
  DowntimeEnd               varchar(255),
  DowntimeInfo              varchar(255),
  serviceId                 varchar(255) not null
) ;

-- AccessPolicy
create table AccessPolicy (
  Id                        varchar(255) not null,
  endpointId                varchar(255) not null
) ;

-- UserDomain
create table UserDomain (
  Id                        varchar(255) not null,
  Level                     integer,
  userDomainId              varchar(255)
) ;

-- MappingPolicy
create table MappingPolicy (
  Id                        varchar(255) not null,
  Name                      varchar(255),
  CreationTime              timestamp,
  Validity                  integer,
  Scheme                    varchar(255) not null,
  shareId                   varchar(255) not null
) ;

-- Domain
create table Domain (
  Id                        varchar(255) not null,
  Name                      varchar(255),
  CreationTime              timestamp,
  Validity                  integer,
  WWW                       varchar(255),
  Description               varchar(255),
  locationId                varchar(255)
) ;

-- Policy
create table Policy (
  Id                        varchar(255) not null,
  Name                      varchar(255),
  CreationTime              timestamp,
  Validity                  integer,
  Scheme                    varchar(255) not null
) ;

-- Share
create table Share (
  Id                        varchar(255) not null,
  Name                      varchar(255),
  CreationTime              timestamp,
  Validity                  integer,
  Description               varchar(255),
  serviceId                 varchar(255) not null
) ;

-- Manager
create table Manager (
  Id                        varchar(255) not null,
  Name                      varchar(255),
  CreationTime              timestamp,
  Validity                  integer,
  ProductName               varchar(255) not null,
  ProductVersion            varchar(255),
  serviceId                 varchar(255) not null,
  managerId                 varchar(255) not null
) ;

-- Resource
create table Resource (
  Id                        varchar(255) not null,
  Name                      varchar(255),
  CreationTime              timestamp,
  Validity                  integer,
  managerId                 varchar(255) not null
) ;


-- Extension
create table Extension (
  LocalID                   varchar(255) not null,
  Key                       varchar(255) not null,
  Value                     varchar(255) not null,
  locationId                varchar(255),
  contactId                 varchar(255),
  serviceId                 varchar(255),
  domainId                  varchar(255),
  endpointId                varchar(255),
  shareId                   varchar(255),
  managerId                 varchar(255),
  resourceId                varchar(255),
  activityId                varchar(255),
  policyId                  varchar(255)
) ;
