(ns clj_hypergraph_db.core
  (:import [org.hypergraphdb HGEnvironment])
  (:gen-class :main true))


(def database (atom nil))
(def classes (atom {}))


(defn create-database
  ""
  [path]
  (let [dbinstance (HGEnvironment/get path)]
        (reset! database dbinstance)))


(defn generate-handler
  ""
  [attribs]
  (.add @database attribs)
  )


(defn create-class
  "
  Creates a type

  Arguments:
  name - name of type
  attribs - fields of type
  "
  [name & attribs]
  (reset! classes
          (assoc @classes
            name
            {:handle (generate-handler attribs)
            :create attribs})
  ))


;'(name attribs)

;(apply assoc (cons (apply hash-map attribs) (filter #(contains? attribs %1) (keys key-value-attrib-list))))


(defn -main
  "I don't do a whole lot."
  []
  (do
    (create-database "hgdbtest")))
