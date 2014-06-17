;db declaration

(def-db :hypergraph)

;types

(def-type :Share
          (with-classifier :Abstract))

(def-type :Service)

(def-type :Location)

(def-type :AdminDomain)

(def-type :Domain
          (with-classifier :Abstract))

(def-type :Entity
          (with-classifier :Abstract))

(def-type :Extension)

(def-type :Contact)

(def-type :Manager
          (with-classifier :Abstract))

(def-type :Resource
          (with-classifier :Abstract))

(def-type :Endpoint)

(def-type :Activity)

(def-type :Policy
  (with-classifier :Abstract))

(def-type :AccessPolicy)

(def-type :MappingPolicy)


(def-type :UserDomain)

;links


;; ?? STORAGE ?? -> Resource
(def-named-link :DefinedOn
                (from :Share)
                (to :Resource))

(def-named-link :Runs
                (from :Activity)
                (to :Resource))

(def-unnamed-link :RelatesTo
                (from :Service)
                (to :Service))

(def-unnamed-link :RelatesTo
                (from :Activity)
                (to :Activity))

(def-named-link :Creates
                (from :Activity)
                (to :UserDomain))

(def-named-link :MappedInto
                (from :Activity)
                (to :Share))

(def-named-link :ParticipatesIn
                (from :UserDomain)
                (to :UserDomain))

(def-named-link :HasPolicies
                (from :Policy)
                (to :UserDomai))

(def-named-link :CanAccess
                (from :AccessPolicy)
                (to :Endpoint))

(def-named-link :CanBeMappedInto
                (from :MappingPolicy)
                (to :Share))

(def-named-link :Exposes
                (from :Endpoint)
                (to :Service))

(def-unnamed-link :Offers
                (from :Share)
                (to :Endpoint))

(def-named-link :Manages
                (from :Manager)
                (to :Resource))

(def-named-link :Offers
                (from :Share)
                (to :Service))

(def-named-link :Offers
                (from :Manager)
                (to :Service))

(def-named-link :PrimaryLocatedAt
                (from :Service)
                (to :Location))

(def-named-link :PrimaryLocatedAt
                (from :Domain)
                (to :Location))

(def-unnamed-link :Has
                (from :Service)
                (to :Contact))

(def-unnamed-link :Has
                (from :Domain)
                (to :Contact))

(def-unnamed-link :Manages
                (from :AdminDomain)
                (to :Service))

(def-named-link :ParticipatesIn
                (from :AdminDomain)
                (to :AdminDomain))