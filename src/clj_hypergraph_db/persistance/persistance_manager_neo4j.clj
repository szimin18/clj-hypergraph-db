(ns clj_hypergraph_db.persistance.persistance_manager_neo4j
  (:require [clojurewerkz.neocons.rest :as nr]
            [clojurewerkz.neocons.rest.nodes :as nn]
            [clojurewerkz.neocons.rest.relationships :as nrl]
            [clojurewerkz.neocons.rest.cypher :as cy]))

(def DEFAULT_SERVER "http://localhost:7474/db/data")
(def neo4j (atom nil))


(defn neo4j-create
  []
  (reset! neo4j (nr/connect (do (println DEFAULT_SERVER) DEFAULT_SERVER)))
  (println neo4j))


(defn neo4j-close
  []
  #_(cy/tquery @neo4j "START n=node(*) OPTIONAL MATCH (n)-[r]-() delete n,r;")
  (reset! neo4j (atom nil)))


(defn neo4j-add-node
  [data]
  (nn/create @neo4j data))


(defn neo4j-add-link
  ([target-list]
   #_(.add @neo4j (neo4jPlainLink. (into-array neo4jHandle target-list))))
  ([data target-list]
   #_(.add @neo4j (neo4jValueLink. data (into-array neo4jHandle target-list)))))


;(defn neo4j-remove
;  [handle]
;  (.remove @neo4j handle))
;
;
;(defn neo4j-get
;  [handle]
;  (.get @neo4j handle))
;
;
;(defn neo4j-find-one
;  ([neo4j-condition1]
;   (->> neo4j-condition1 (neo4jQuery$neo4j/findOne @neo4j)))
;  ([neo4j-condition1 neo4j-condition2]
;   (->> (And. neo4j-condition1 neo4j-condition2) (neo4jQuery$neo4j/findOne @neo4j)))
;  ([neo4j-condition1 neo4j-condition2 neo4j-condition3]
;   (->> (And. neo4j-condition1 neo4j-condition2 neo4j-condition3) (neo4jQuery$neo4j/findOne @neo4j))))
;
;
;(defn neo4j-find-all
;  ([neo4j-condition1]
;   (->> neo4j-condition1 (neo4jQuery$neo4j/findAll @neo4j)))
;  ([neo4j-condition1 neo4j-condition2]
;   (->> (And. neo4j-condition1 neo4j-condition2) (neo4jQuery$neo4j/findAll @neo4j)))
;  ([neo4j-condition1 neo4j-condition2 neo4j-condition3]
;   (->> (And. neo4j-condition1 neo4j-condition2 neo4j-condition3) (neo4jQuery$neo4j/findAll @neo4j))))
;
;
;(defn neo4j-eq
;  [value]
;  (neo4jQuery$neo4j/eq value))
;
;
;(defn neo4j-incident
;  [handle]
;  (neo4jQuery$neo4j/incident handle))
;
;
;(defn neo4j-incident-at
;  [handle index]
;  (neo4jQuery$neo4j/incidentAt handle index))
;
;
;(defn neo4j-link-first-target
;  [link]
;  (.getTargetAt link 1))
;
;
;(defn neo4j-link-target-at
;  [link index]
;  (.getTargetAt link index))
;
;
;(defn neo4j-link-arity
;  [link]
;  (.getArity link))
;
;
;(defn neo4j-link-value
;  [link]
;  (.getValue link))
