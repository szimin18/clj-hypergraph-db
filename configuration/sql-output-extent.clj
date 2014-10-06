
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
                     (mapping-single-relation [:PrimaryLocatedAtDomainLocation :Location :ID] [:locationId])))

