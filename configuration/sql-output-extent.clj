(foreach :GlueSite (add-entity [:domain]
                     (mapping :GlueSiteUniqueID [:Id])
                     (mapping :GlueSiteName [:Name])
                     (mapping :GlueSiteDescription [:Description])
                     (mapping :GlueSiteLocation [:locationId])
                     (mapping :GlueSiteWeb [:WWW]))

  (add-entity [:admindomain]
    (mapping :GlueSiteUniqueID [:Id])
    (mapping :GlueSiteSponsor [:Owner]))

  (add-entity [:userdomain]
    (mapping :GlueSiteUniqueID [:Id]))

  (add-entity [:otherinfo]
    (mapping :GlueSiteUniqueID [:Id])
    (mapping :GlueSiteOtherInfo [:OtherInfo]))

  ;missing - contact type, multiple contacts
  (add-entity [:contact]
    (mapping :GlueSiteUniqueID [:Id])
    (mapping :GlueSiteEmailContact [:Name])
    (mapping :GlueSiteEmailContact [:Detail]))

  (add-entity [:location]
    (mapping :GlueSiteUniqueID [:Id])
    (mapping :GlueSiteLocation [:Name])
    (mapping :GlueSiteLongitude [:Longitude])
    (mapping :GlueSiteLatitude [:Latitude]))

  (add-entity [:domaincontact]
    (mapping :GlueSiteUniqueID [:domainId])
    (mapping :GlueSiteUniqueID [:contactId])
    ))

;missing - quality level
(foreach :GlueService (add-entity [:service]
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

(for-each :GlueServiceData
  (add-entity [:extension]
    (mapping :GlueServiceDataKey [:Key])
    (mapping :GlueServiceDataValue [:Value])
    (mapping :GlueServiceUniqueID [:serviceId])))

(for-each :GlueCluster
  (add-entity [:computingmanager]
    (mapping :GlueClusterUniqueID [:Id])
    (mapping :GlueClusterTmpDir [:TmpDir])
    (mapping :GlueClusterWNTmpDir [:ScratchDir])
    ))