(for-each :GlueSite
  (add-token [:glue:Domains :AdminDomain]
    (mapping :GlueSiteSponsor [:Owner :Owner-text-node])
    (mapping :GlueSiteWeb [:WWW :WWW-text-node])
    (mapping :GlueSiteDescription [:Description :Description-text-node])
    (mapping :GlueSiteUniqueID [:ID :ID-text-node])
    (mapping :GlueSiteName [:Name :Name-text-node])
    (mapping :GlueSiteOtherInfo [:OtherInfo :OtherInfo-text-node])))
(for-each :GlueSite
  (add-token [:glue:Domains :UserDomain]
    (mapping :GlueSiteWeb [:WWW :WWW-text-node])
    (mapping :GlueSiteDescription [:Description :Description-text-node])
    (mapping :GlueSiteUniqueID [:ID :ID-text-node])
    (mapping :GlueSiteName [:Name :Name-text-node])
    (mapping :GlueSiteOtherInfo [:OtherInfo :OtherInfo-text-node])))

(for-each :AdminDomain
         (add-token [:glue:Domains :AdminDomain]
                    (mapping :CreationTime [:CreationTime-attribute])
                    (mapping :Validity [:Validity-attribute])
                    (mapping :Distributed [:Distributed :Distributed-text-node])
                    (mapping :Owner [:Owner :Owner-text-node])
                    (mapping :WWW [:WWW :WWW-text-node])
                    (mapping :Description [:Description :Description-text-node])
                    (mapping :ID [:ID :ID-text-node])
                    (mapping :Name [:Name :Name-text-node])
                    (mapping :OtherInfo [:OtherInfo :OtherInfo-text-node])))

