(table "entity" :Entity
       (column "ID" :ID :VARCHAR_45_ :pk :notnull)
       (column "CreationTime" :CreationTime :DATETIME)
       (column "Validity" :Validity :BIGINT)
       (column "Name" :Name :VARCHAR_45_))

(table "entity_other_info" :EntityOtherInfo
       (column "EntityID" :EntityID :VARCHAR_45_ :notnull)
       (column "OtherInfo" :OtherInfo :VARCHAR_45_ :notnull)
       (fk "EntityOtherInfo"
           (between :EntityID :Entity :ID)))

(relation :EntityOtherInfo
          (edge :Entity :ID :*)
          (edge :EntityOtherInfo :EntityID :1))