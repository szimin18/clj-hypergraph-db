(ns clj_hypergraph_db.xml_parser.XMLLoaderContentHandler
  (:require [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager_neo4j :refer :all]
            [clj_hypergraph_db.persistance.persistance_manager :refer :all]
            [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.common_parser.common_functions :refer :all])
  (:gen-class
    :extends      org.xml.sax.helpers.DefaultHandler
    :state        state
    :init         init
    :constructors {[clojure.lang.PersistentArrayMap] []}))


;
; helpers
;


(defn add-child
  [parent child-name child-body]
  (assoc-in parent [:children child-name] child-body))


(defn remove-child
  [parent child-name]
  (dissoc-in parent [:children child-name]))


(defn finalizeText
  [this]
  (let [{string-builder :string-builder model :model} (.state this)
        string-builder-text (.toString @string-builder)]
    (when (not-every? #{\newline \tab \space} string-builder-text)
      (doseq [{instance-map :instance-map
               attribute-name :attribute-name} (:add-attribute-from-text @model)]
        (swap! instance-map merge-concat {attribute-name [string-builder-text]}))
      (doseq [{instance-handle :instance-handle
               association-name :association-name
               role-name :role-name} (:add-role-from-text-pk @model)]
        (swap! instance-handle add-role-instance-pk association-name role-name string-builder-text)))
    (reset! string-builder (StringBuilder.))))


;
; clj_hypergraph_db.xml_parser.XMLLoaderContentHandler extetds org.xml.sax.helpers.DefaultHandler
;


(defn -init
  [model-root]
  [[] {:stack (atom [])
       :model (atom model-root)
       :string-builder (atom (StringBuilder.))}])


(defn -startElement    ; String uri, String localName, String qName, Attributes attributes
  [this uri localName qName attributes]
  (let [{stack :stack model :model} (.state this)]
    (finalizeText this)
    (swap! stack concat [(remove-child @model qName)])
    (swap! model #((:children %) qName))
    (doseq [{instance-map :instance-map instance-node-handle :instance-node-handle} (:add-instance @model)]
      (reset! instance-map {})
      (reset! instance-node-handle (hg-add-node :shell)))
    (doseq [{instance-handle :instance-handle association-name :association-name} (:add-association @model)]
      (reset! instance-handle (add-association-instance association-name)))
    (doseq [{instance-handle :instance-handle
             association-name :association-name
             role-name :role-name
             target-instance-handle :target-instance-handle} (:add-role @model)]
      (swap! instance-handle add-role-instance association-name role-name @target-instance-handle))
    (let [model-attributes (:attributes @model)]
      (doseq [attribute-index (range (.getLength attributes))]
        (let [{add-attribute :add-attribute add-role-pk :add-role-pk} (get model-attributes (.getQName attributes attribute-index))
              attribute-value (.getValue attributes attribute-index)]
          (doseq [{instance-map :instance-map
                   attribute-name :attribute-name} add-attribute]
            (swap! instance-map merge-concat {attribute-name [attribute-value]}))
          (doseq [{instance-handle :instance-handle
                   association-name :association-name
                   role-name :role-name} add-role-pk]
            (swap! instance-handle add-role-instance-pk association-name role-name attribute-value)))))))


(defn -endElement   ; String uri, String localName, String qName
  [this uri localName qName]
  (let [{stack :stack model :model} (.state this)]
    (finalizeText this)
    (doseq [{instance-map :instance-map
             class-name :class-name
             shell-node-handle :instance-node-handle} (:add-instance @model)]
      (let [instance-map @instance-map
            shell-node-handle @shell-node-handle
            pk-list (get-pk-list-neo4j class-name)
            pk-map (into {} (for [pk pk-list]
                              [pk (-> pk instance-map first)]))
            instance-link-handle (when (not-any? nil? (vals pk-map))
                                   (get-class-instance-by-attributes class-name pk-map))
            instance-map (if instance-link-handle
                           (reduce dissoc instance-map pk-list)
                           instance-map)
            [instance-node-handle instance-link-handle] (if instance-link-handle
                                                          [(-> instance-link-handle hg-get hg-link-first-target) instance-link-handle]
                                                          (add-class-instance-return-with-link class-name))
            current-association-handle (-> shell-node-handle hg-incident hg-find-one atom)]
        (doseq [[attribute-name attribute-values-list] instance-map]
          (doseq [attribute-value attribute-values-list]
            (add-attribute-instance instance-node-handle attribute-name attribute-value)))
        (while @current-association-handle
          (replace-role-target @current-association-handle shell-node-handle instance-link-handle)
          (reset! current-association-handle (hg-find-one (hg-incident shell-node-handle))))
        (try
          (hg-remove shell-node-handle) ;todo remove the remaining :shell nodes
          (catch Exception e))))
    (swap! model #(add-child (last @stack) qName %))
    (swap! stack drop-last)))


(defn -characters   ; char ch[], int start, int length
  [this ch start length]
  (-> this .state :string-builder deref (.append ch start length)))
