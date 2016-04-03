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

(for-each :GlueServiceDataKey
  (add-instance :GlueServiceData
    (mapping :GlueServiceDataKey :GlueServiceDataKey)
    (mapping :GlueServiceDataValue :GlueServiceDataValue)
    (mapping :GlueChunkKey :GlueServiceUniqueID)))

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


