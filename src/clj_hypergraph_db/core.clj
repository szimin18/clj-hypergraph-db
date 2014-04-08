(ns clj_hypergraph_db.core
  (:import [org.hypergraphdb HGEnvironment])
  (:gen-class :main true))

(def db (atom nil))

(defn create-database
  ""
  [path]
  (let [dbinstance (HGEnvironment/get path)]
        (reset! db dbinstance)))
def Service (create-cless args)
(defn create-class
  "
  Creates a type

  Arguments:
  name - name of type
  attribs - fields of type
  "
  [name &attribs]
  (do
    (defn fun
      [&key-value-attrib-list]
      (do
        ; todo adding object to database?
        (apply assoc (cons (apply hash-map attribs) (filter #(contains? attribs %1) key-value-attrib-list)))))
    fun))



(defn -main
  "I don't do a whole lot."
  []
  (do
    (create-database "hgdbtest")))
