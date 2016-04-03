(foreach :GlueSite
  (add-entity [:domain]
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