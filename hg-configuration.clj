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

(def-type :Contact)

(def-type :Manager
          (with-classifier :Abstract))

(def-type :Resource
          (with-classifier :Abstract))

(def-type :Endpoint)

(def-type :Activity)

(def-type :AccessPolicy)

(def-type :MappingPolicy)

(def-type :Policy
          (with-classifier :Abstract))

(def-type :UserDomain)

;links

(def-named-link :DefinedOn)
                (from :Share)
                (to :Storage)

(def-named-link :Runs
                (from :Activity)
                (to :Resource))

(def-unnamed-link :RelatesTo
                  (from :Service)
                  (to :Service))