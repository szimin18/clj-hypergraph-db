(foreach :GLUE2AdminDomain
         (add-instance :AdminDomain
                       (mapping :GLUE2EntityName :Name)
                       (mapping :GLUE2EntityOtherInfo :OtherInfo)
                       (mapping :GLUE2EntityCreationTime :CreationTime)
                       (mapping :GLUE2EntityValidity :Validity)
                       (mapping :GLUE2DomainID :ID)
                       (mapping :GLUE2DomainDescription :Description)
                       (mapping :GLUE2DomainWWW :WWW)
                       (mapping :GLUE2AdminDomainDistributed :Distributed)
                       (mapping :GLUE2AdminDomainOwner :Owner))
         (add-association :ParticipatesInAdminDomainAdminDomain
                          (mapping-fk :GLUE2AdminDomainAdminDomainForeignKey :ToAdminDomain)
                          (mapping-pk :GLUE2DomainID :AdminDomain)))

(foreach :GLUE2UserDomain
         (add-instance :UserDomain
                       (mapping :GLUE2EntityName :Name)
                       (mapping :GLUE2EntityOtherInfo :OtherInfo)
                       (mapping :GLUE2EntityCreationTime :CreationTime)
                       (mapping :GLUE2EntityValidity :Validity)
                       (mapping :GLUE2DomainID :ID)
                       (mapping :GLUE2DomainDescription :Description)
                       (mapping :GLUE2DomainWWW :WWW)
                       (mapping :GLUE2UserDomainLevel :Level)
                       (mapping :GLUE2UserDomainUserManager :UserManager)
                       (mapping :GLUE2UserDomainMember :Member)))
