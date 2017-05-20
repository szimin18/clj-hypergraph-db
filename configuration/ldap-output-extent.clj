(for-each :AdminDomain
         (add-instance :GLUE2AdminDomain
                       (mapping :Name :GLUE2EntityName)
                       (mapping :OtherInfo :GLUE2EntityOtherInfo)
                       (mapping :CreationTime :GLUE2EntityCreationTime)
                       (mapping :Validity :GLUE2EntityValidity)
                       (mapping :ID :GLUE2DomainID)
                       (mapping :Description :GLUE2DomainDescription)
                       (mapping :WWW :GLUE2DomainWWW)
                       (mapping :Distributed :GLUE2AdminDomainDistributed)
                       (mapping :Owner :GLUE2AdminDomainOwner)
                       (mapping-fk :AdminDomain
                                   :ParticipatesInAdminDomainAdminDomain
                                   :ToAdminDomain
                                   :AdminDomain
                                   :ID
                                   :GLUE2AdminDomainAdminDomainForeignKey)))