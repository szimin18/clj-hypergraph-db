(for-each :GlueSite
  (add-instance :GlueSite
    (mapping :GlueSiteUniqueID :GlueSiteUniqueID)
    (mapping :GlueSiteName :GlueSiteName)
    (mapping :GlueSiteDescription :GlueSiteDescription)
    (mapping :GlueSiteEmailContact :GlueSiteEmailContact)
    (mapping :GlueSiteSysAdminContact :GlueSiteSysAdminContact)
    (mapping :GlueSiteUserSupportContact :GlueSiteUserSupportContact)
    (mapping :GlueSiteSecurityContact :GlueSiteSecurityContact)
    (mapping :GlueSiteLocation :GlueSiteLocation)
    (mapping :GlueSiteLatitude :GlueSiteLatitude)
    (mapping :GlueSiteLongitude :GlueSiteLongitude)
    (mapping :GlueSiteWeb :GlueSiteWeb)
    (mapping :GlueSiteSponsor :GlueSiteSponsor)
    (mapping :GlueSiteOtherInfo :GlueSiteOtherInfo)))


(for-each :GlueService
  (add-instance :GlueService
    (mapping :GlueServiceUniqueID :GlueServiceUniqueID)
    (mapping :GlueServiceName :GlueServiceName)
    (mapping :GlueServiceType :GlueServiceType)
    (mapping :GlueServiceVersion :GlueServiceVersion)
    (mapping :GlueServiceEndpoint :GlueServiceEndpoint)
    (mapping :GlueServiceStatus :GlueServiceStatus)
    (mapping :GlueServiceStatusInfo :GlueServiceStatusInfo)
    (mapping :GlueServiceStartTime :GlueServiceStartTime)
    (mapping :GlueServiceOwner :GlueServiceOwner)
    (mapping :GlueServiceAccessControlRule :GlueServiceAccessControlRule)
    (mapping :GlueService :GlueService)
    (mapping :GlueServiceWSDL :GlueServiceWSDL)
    (mapping :GlueForeignKey :GlueServiceSiteUniqueID)
    ))

#_(for-each :GlueServiceDataKey
  (add-instance :GlueServiceData
    (mapping :GlueServiceDataKey :GlueServiceDataKey)
    (mapping :GlueServiceDataValue :GlueServiceDataValue)
    (mapping :GlueServiceUniqueID :GlueChunkKey )))

(for-each :GlueCluster
  (add-instance :GlueCluster
    (mapping :GlueClusterUniqueID :GlueClusterUniqueID)
    (mapping :GlueClusterName :GlueClusterName)
    (mapping :GlueClusterService :GlueClusterService)
    (mapping :GlueCEUniqueID :GlueCEUniqueID)
    (mapping :GlueClusterTmpDir :GlueClusterTmpDir)
    (mapping :GlueClusterWNTmpDir :GlueClusterWNTmpDir)
    (mapping :GlueInformationServiceURL :GlueInformationServiceURL)
    ))

(for-each :GlueSubCluster
  (add-instance :GlueSubCluster
    (mapping :GlueSubClusterUniqueID :GlueSubClusterUniqueID)
    (mapping :GlueSubClusterName :GlueSubClusterName)
    (mapping :GlueSubClusterWNTmpDir :GlueSubClusterWNTmpDir)
    (mapping :GlueSubClusterTmpDir :GlueSubClusterTmpDir)
    (mapping :GlueSubClusterPhysicalCPUs :GlueSubClusterPhysicalCPUs)
    (mapping :GlueSubClusterLogicalCPUs :GlueSubClusterLogicalCPUs)
    (mapping :GlueHostOperatingSystemName :GlueHostOperatingSystemName)
    (mapping :GlueHostOperatingSystemRelease :GlueHostOperatingSystemRelease)
    (mapping :GlueHostOperatingSystemVersion :GlueHostOperatingSystemVersion)
    (mapping :GlueHostArchitecturePlatformType :GlueHostArchitecturePlatformType)
    (mapping :GlueHostArchitectureSMPSize :GlueHostArchitectureSMPSize)
    (mapping :GlueHostProcessorVendor :GlueHostProcessorVendor)
    (mapping :GlueHostProcessorModel :GlueHostProcessorModel)
    (mapping :GlueHostProcessorVersion :GlueHostProcessorVersion)
    (mapping :GlueHostProcessorClockSpeed :GlueHostProcessorClockSpeed)
    (mapping :GlueHostProcessorInstructionSet :GlueHostProcessorInstructionSet)
    (mapping :GlueHostProcessorOtherDescription :GlueHostProcessorOtherDescription)
    (mapping :GlueHostApplicationSoftwareRunTimeEnvironment :GlueHostApplicationSoftwareRunTimeEnvironment)
    (mapping :GlueHostBenchmarkSI00 :GlueHostBenchmarkSI00)
    (mapping :GlueHostBenchmarkSF00 :GlueHostBenchmarkSF00)
    (mapping :GlueHostNetworkAdapterOutboundIP :GlueHostNetworkAdapterOutboundIP)
    (mapping :GlueHostNetworkAdapterInboundIP :GlueHostNetworkAdapterInboundIP)
    (mapping :GlueHostMainMemoryRAMSize :GlueHostMainMemoryRAMSize)
    (mapping :GlueHostMainMemoryVirtualSize :GlueHostMainMemoryVirtualSize)
    ))

(for-each :GlueCETop
  (add-instance :GlueCE
    (mapping :GlueCEAccessControlBaseRule :GlueCEAccessControlBaseRule)
    (mapping :GlueCEUniqueID :GlueCEUniqueID)
    (mapping :GlueForeignKey :GlueForeignKey)

    (mapping :GlueCEInfoTotalCPUs :GlueCEInfoTotalCPUs)
    (mapping :GlueCEInfoLRMSType :GlueCEInfoLRMSType)
    (mapping :GlueCEInfoLRMSVersion :GlueCEInfoLRMSVersion)
    (mapping :GlueCEInfoGRAMVersion :GlueCEInfoGRAMVersion)
    (mapping :GlueCEInfoHostName :GlueCEInfoHostName)
    (mapping :GlueCEInfoGatekeeperPort :GlueCEInfoGatekeeperPort)
    (mapping :GlueCEInfoContactString :GlueCEInfoContactString)
    (mapping :GlueCEInfoJobManager :GlueCEInfoJobManager)
    (mapping :GlueCEInfoApplicationDir :GlueCEInfoApplicationDir)
    (mapping :GlueCEInfoDataDir :GlueCEInfoDataDir)
    (mapping :GlueCEInfoDefaultSE :GlueCEInfoDefaultSE)

    (mapping :GlueCEPolicyPriority :GlueCEPolicyPriority)
    (mapping :GlueCEPolicyMaxRunningJobs :GlueCEPolicyMaxRunningJobs)
    (mapping :GlueCEPolicyMaxTotalJobs :GlueCEPolicyMaxTotalJobs)
    (mapping :GlueCEPolicyMaxCPUTime :GlueCEPolicyMaxCPUTime)
    (mapping :GlueCEPolicyMaxWallClockTime :GlueCEPolicyMaxWallClockTime)
    (mapping :GlueCEPolicyAssignedJobSlots :GlueCEPolicyAssignedJobSlots)
    (mapping :GlueCEPolicyMaxObtainableWallClockTime :GlueCEPolicyMaxObtainableWallClockTime)
    (mapping :GlueCEPolicyMaxObtainableCPUTime :GlueCEPolicyMaxObtainableCPUTime)
    (mapping :GlueCEPolicyMaxWaitingJobs :GlueCEPolicyMaxWaitingJobs)
    (mapping :GlueCEPolicyMaxSlotsPerJob :GlueCEPolicyMaxSlotsPerJob)
    (mapping :GlueCEPolicyPreemption :GlueCEPolicyPreemption)
    (mapping :GlueCEHostingCluster :GlueCEHostingCluster)
    ))


