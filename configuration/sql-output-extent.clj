(foreach :GlueSite
  (add-entity [:domain]
    (mapping :GlueSiteUniqueID [:Id])
    (mapping :GlueSiteName [:Name])
    (mapping :GlueSiteDescription [:Description])
    (mapping :GlueSiteUniqueID [:locationId "Location"])
    (mapping :GlueSiteWeb [:WWW]))

  (add-entity [:location]
    (mapping :GlueSiteUniqueID [:Id "Location"])
    (mapping :GlueSiteLocation [:Name])
    (mapping :GlueSiteLongitude [:Longitude])
    (mapping :GlueSiteLatitude [:Latitude]))

  (add-entity [:admindomain]
    (mapping :GlueSiteUniqueID [:Id "Admin"])
    (mapping :GlueSiteSponsor [:Owner]))

  (add-entity [:userdomain]
    (mapping :GlueSiteUniqueID [:Id "User"]))

  (foreach-attribute :GlueSiteEmailContact
    (add-entity [:contact]
      (mapping :GlueSiteUniqueID [:Id "Email"])
      (mapping :GlueSiteEmailContact [:Name])
      (mapping :GlueSiteEmailContact [:Detail]))
    (add-entity [:domaincontact]
      (mapping :GlueSiteUniqueID [:domainId])
      (mapping :GlueSiteEmailContact [:contactId "Email"])))

  (foreach-attribute :GlueSiteSysAdminContact
    (add-entity [:contact]
      (mapping :GlueSiteUniqueID [:Id "SysAdmin"])
      (mapping :GlueSiteSysAdminContact [:Name])
      (mapping :GlueSiteSysAdminContact [:Detail]))
    (add-entity [:domaincontact]
      (mapping :GlueSiteUniqueID [:domainId "Admin"])
      (mapping :GlueSiteSysAdminContact [:contactId "SysAdmin"])))

  (foreach-attribute :GlueSiteUserSupportContact
    (add-entity [:contact]
      (mapping :GlueSiteUniqueID [:Id "UserSupport"])
      (mapping :GlueSiteUserSupportContact [:Name])
      (mapping :GlueSiteUserSupportContact [:Detail]))
    (add-entity [:domaincontact]
      (mapping :GlueSiteUniqueID [:domainId "User"])
      (mapping :GlueSiteUserSupportContact [:contactId "UserSupport"])))

  (foreach-attribute :GlueSiteSecurityContact
    (add-entity [:contact]
      (mapping :GlueSiteUniqueID [:Id "Security"])
      (mapping :GlueSiteSecurityContact [:Name])
      (mapping :GlueSiteSecurityContact [:Detail]))
    (add-entity [:domaincontact]
      (mapping :GlueSiteUniqueID [:domainId "Admin"])
      (mapping :GlueSiteSecurityContact [:contactId "Security"])))
  )

;missing - quality level

(foreach :GlueService
  (add-entity [:service]
    (mapping :GlueServiceUniqueID [:Id])
    (mapping :GlueServiceName [:Name])
    (mapping :GlueServiceType [:Type])
    (mapping :GlueServiceStatusInfo [:StatusInfo])
    (mapping :GlueServiceSiteUniqueID [:adminDomainId])
    (mapping :GlueServiceSiteUniqueID [:locationId]))

  ;missing - interface name, dynamic attributes (HealthState,ServiceState)
  ;ignored - dynamic StartTime
  (add-entity [:endpoint]
    (mapping :GlueServiceUniqueID [:Id])
    (mapping :GlueServiceEndpoint [:URL])
    (mapping :GlueServiceName [:Name])
    (mapping :GlueServiceVersion [:ImplementationVersion])
    (mapping :GlueServiceWSDL [:WSDL])
    (mapping :GlueServiceSemantics [:Semantics])
    (mapping :GlueServiceUniqueID [:serviceId])
    )

  (add-entity [:policy]
    (mapping :GlueServiceAccessControlRule [:Id])
    (mapping :GlueServiceAccessControlRule [:Name]))

  (add-entity [:accesspolicy]
    (mapping :GlueServiceAccessControlRule [:Id])
    (mapping :GlueServiceUniqueID [:endpointId]))
  )

(foreach :GlueServiceData
  (add-entity [:extension]
    (mapping :GlueServiceDataKey [:Key])
    (mapping :GlueServiceDataValue [:Value])
    (mapping :GlueChunkKey [:serviceId])))

(foreach :GlueCluster
  (add-entity [:computingmanager]
    (mapping :GlueClusterUniqueID [:Id])
    (mapping :GlueClusterTmpDir [:TmpDir])
    (mapping :GlueClusterWNTmpDir [:ScratchDir])
    )

  ;  (foreach-attribute :GlueClusterService
  ;    (add-entity [:share]
  ;      (mapping :GlueClusterService [:Id])
  ;      (mapping :GlueSiteUniqueID)))

  ;Requires ldap split on attributes:
  ;  GlueForeignKey: GlueSiteUniqueID=WT2
  ;  GlueForeignKey: GlueCEUniqueID=osgserv06.slac.stanford.edu:2119/jobmanager-lsf-atlasq
  ;  (comment (foreach-attribute :GlueCEUniqueID
  ;    (add-entity [:share]
  ;      (mapping :GlueCEUniqueID)
  ;      (mapping :GlueSiteUniqueID))))
  ;  )
  )

(foreach :GlueSubCluster
  (add-entity [:executionenvironment]
    (mapping :GlueSubClusterUniqueID [:Id])
    (mapping :GlueSubClusterName [:Name])

;ldap split - GlueChunkKey: GlueClusterUniqueID=slac
    (mapping :GlueChunkKey [:managerId])
    (mapping :GlueHostArchitecturePlatformType [:Platform])
    ;(mapping [:TotalInstances]) no data
    (mapping :GlueSubClusterPhysicalCPUs [:PhysicalCPUs])
    (mapping :GlueSubClusterLogicalCPUs [:LogicalCPUs])
    (mapping :GlueHostProcessorVendor [:CPUVendor])
    (mapping :GlueHostProcessorModel [:CPUModel])
    (mapping :GlueHostProcessorVersion [:CPUVersion])
    (mapping :GlueHostProcessorClockSpeed [:CPUClockSpeed])
    (mapping :GlueHostMainMemoryRAMSize [:CPUMainMemorySize])
    ;(mapping :GlueHostOperatingSystemRelease [:OSFamily]) to verify validity
    (mapping :GlueHostOperatingSystemName [:OSName])
    (mapping :GlueHostOperatingSystemVersion [:OSVersion])
    ))

(foreach :GlueCE
  (add-entity [:share]
    (mapping :GlueCEUniqueID [:Id])
    (mapping :GlueCEUniqueID [:Name])
;serviceId - has to be taken from GlueCluster
    )

  (add-entity [:computingshare]
    (mapping :GlueCEUniqueID [:Id])
    (mapping :GlueCEInfoJobManager [:MappingQueue])
    (mapping :GlueCEPolicyMaxObtainableWallClockTime [:MaxWallTime])
    ;(mapping [:MaxMultiSlotWallTime]) no data
    ;(mapping [:MinWallTime]) no data
    (mapping :GlueCEPolicyMaxWallClockTime [:DefaultWallTime])
    (mapping :GlueCEPolicyMaxObtainableCPUTime [:MaxCPUTime])
    ;(mapping [:MaxTotalCPUTime]) no data
    ;(mapping [:MinCPUTime]) no data
    (mapping :GlueCEPolicyMaxSlotsPerJob [:MaxSlotsPerJob])
    (mapping :GlueCEPolicyMaxCPUTime [:DefaultCPUTime])
    (mapping :GlueCEPolicyMaxTotalJobs [:MaxTotalJobs])
    (mapping :GlueCEPolicyMaxRunningJobs [:MaxRunningJobs])
    (mapping :GlueCEPolicyMaxWaitingJobs [:MaxWaitingJobs])

    ;No More data(mapping [:MaxPreLRMSWaitingJobs])
;    (mapping [:MaxUserRunningJobs])
;    (mapping [:MaxStageInStreams])
;    (mapping [:MaxStageOutStreams])
;    (mapping [:SchedulingPolicy])
;    (mapping [:MaxMainMemory])
;    (mapping [:GuaranteedMainMemory])
;    (mapping [:MaxVirtualMemory])
;    (mapping [:GuaranteedVirtualMemory])
;    (mapping [:MaxDiskSpace])
;    (mapping [:DefaultStorageService])

    )
  )
;Direclty linked to ExecutionEnvironment via matching Cluster


(foreach :GlueSE

  ;has exactly the same 'DataStore' entity...
  (add-entity [:storageservicecapacity]
    (mapping :GlueSEUniqueID [:Id])
    (mapping :GlueSEName [:Name])
    (mapping :GlueSEType [:Type])
    (mapping :GlueSESizeTotal [:TotalSize]) ;deprecated values
    ;(mapping :GlueSESizeFree [:FreeSize]) - dynamic

    ;requires split - GlueForeignKey: GlueSiteUniqueID=PIC
    (mapping :GlueForeignKey [:adminDomainId]))

  (add-entity [:manager]
    (mapping :GlueSEUniqueID [:Id])
    (mapping :GlueSEName [:Name])
    ;(mapping :GlueSEImplementationName [:GlueSEImplementationName])
    ;(mapping :GlueSEImplementationVersion [:GlueSEImplementationVersion])

    ;replaced serviceId with adminDomainId ...
    (mapping :GlueForeignKey [:serviceId])))

