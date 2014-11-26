
;todo Primary-key sequences (could be done via database itself...)

(foreach :AdminDomain
         (add-entity [:admindomain]
                     (mapping :ID [:Id])
                     (mapping :Distributed [:Distributed])
                     (mapping :ID [:adminDomainId]))
         (add-entity [:domain]
                     (mapping :ID [:Id])
                     (mapping :Name [:Name])
                     (mapping :CreationTime [:CreationTime])
                     (mapping :Validity [:Validity])
                     (mapping :Description [:Description])
                     (mapping-relation [:Location :ID] [:locationId] :PrimaryLocatedAtDomainLocation)))

(foreach :Contact
         (add-entity [:contact]
                     (mapping :ID [:Id])
                     (mapping :Name [:Name])
                     (mapping :CreationTime [:CreationTime])
                     (mapping :Validity [:Validity])
                     (mapping :Detail [:Detail])
                     (mapping :Type [:Type])))

(for-each-association :HasContactDomain
                      (add-entity [:domaincontact]
                                  (mapping-relation [:Domain :ID] [:domainId])
                                  (mapping-relation [:Contact :ID] [:contactId])))

