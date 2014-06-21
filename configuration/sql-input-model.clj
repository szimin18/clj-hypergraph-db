(table "Entity" :Entity
       (column "ID" :ID :String :notnull))

(table "EntityOtherInfo" :EntityOtherInfo
       (column "ID" :EntityID :String :notnull)
       (column "OtherInfo" :OtherInfo :String :notnull))

(relation :EntityOtherInfo
          (edge :Entity :ID :*)
          (edge :EntityOtherInfo :EntityID :1))