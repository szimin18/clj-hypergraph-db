;types

(def-attribute :Abstract); "<<abstract>>")

(def-type :Share
          (with-attribute :Abstract))

(def-type :Service)

(def-type :Location)

(def-type :AdminDomain)

(def-type :Domain
          (with-attribute :Abstract))

(def-type :Contact)

(def-type :Manager
          (with-attribute :Abstract))

(def-type :Resource
          (with-attribute :Abstract))

(def-type :Endpoint)

(def-type :Activity)

(def-type :AccessPolicy)

(def-type :MappingPolicy)

(def-type :Policy
          (with-attribute :Abstract))

(def-type :UserDomain)

;links

(def-named-link :DefinedOn
                (from :Share)
                (to :Resource))

(def-named-link :Runs
                (from :Activity)
                (to :Resource))

(def-unnamed-link :RelatesTo
                  (from-to :Service))