;todo Primary-key sequences (could be done via database itself...)

(function
  :modify
  [string]
  (str string "-modified"))

(function
  :concat
  [& seqs]
  (for [s seqs
        e s]
    e))

(bind "VARIABLE" :GLOBAL)

(foreach :AdminDomain
         (bind :ID :VAR)
         (bind (call :modify :GLOBAL) :VAR2)

         (add-entity [:admindomain]
                     (mapping :VAR [:Id])
                     (mapping :Distributed [:Distributed])
                     (mapping :VAR2 [:adminDomainId]))
         (add-entity [:domain]
                     (mapping :ID [:Id])
                     (mapping :Name [:Name])
                     (mapping (call :concat :CreationTime :VAR) [:CreationTime])
                     (mapping :Validity [:Validity])
                     (mapping :Description [:Description])
                     (mapping-relation [:Location :ID] [:locationId] :PrimaryLocatedAtDomainLocation)))

(foreach :Contact
         (add-entity [:contact]
                     (mapping :ID [:Id])
                     (mapping :Name [:Name])
                     (mapping :CreationTime [:CreationTime])
                     (mapping :Validity [:Validity])
                     (mapping :GLOBAL [:Detail])
                     (mapping :Type [:Type])))

(for-each-association :HasContactDomain
                      (add-entity [:domaincontact]
                                  (mapping-relation [:Domain :ID] [:domainId])
                                  (mapping-relation [:Contact :ID] [:contactId])))

