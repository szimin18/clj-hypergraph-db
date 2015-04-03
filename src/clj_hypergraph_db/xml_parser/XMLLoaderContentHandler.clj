(ns clj_hypergraph_db.xml_parser.XMLLoaderContentHandler
  (:require [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]
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


(defn finalize-text
  [this]
  (let [{string-builder :string-builder model :model} (.state this)
        string-builder-text (.toString @string-builder)]
    (reset! string-builder (StringBuilder.))
    (when (not-every? #{\newline \tab \space} string-builder-text)
      ;iterate over add-attribute-from-text
      (doseq [{instance-map :instance-map
               attribute-name :attribute-name} (:add-attribute-from-text @model)]
        (swap! instance-map merge-concat {attribute-name [string-builder-text]}))
      ;iterate over add-role-from-text-pk
      (doseq [{association-name :association-name
               role-name :role-name
               association-instance-number :instance-number
               related-roles :related-roles
               target-class-name :target-class-name
               :as add-role-from-text-pk} (:add-role-from-text-pk @model)]
        (swap! related-roles conj (assoc add-role-from-text-pk
                                    :association-instance-number @association-instance-number)
                                  {:association-name association-name
                                   :association-instance-number @association-instance-number
                                   :role-name role-name
                                   :target-class-name target-class-name})))))


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
  (let [{stack :stack
         model :model} (.state this)]
    ;finalize text
    (finalize-text this)
    ;swap model to next item
    (swap! stack concat [(remove-child @model qName)])
    (swap! model #((:children %) qName))
    ;iterate over add-association
    #_(doseq [{instance-handle :instance-handle association-name :association-name} (:add-association @model)]
      (reset! instance-handle (add-association-instance association-name)))
    ;iterate over add-role
    #_(doseq [{instance-handle :instance-handle
             association-name :association-name
             role-name :role-name
             target-instance-handle :target-instance-handle} (:add-role @model)]
      (swap! instance-handle add-role-instance association-name role-name @target-instance-handle))
    ;iterate over current attributes
    #_(let [model-attributes (:attributes @model)]
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
  (let [{stack :stack
         model :model} (.state this)]
    (finalize-text this)
    (doseq [{instance-map :instance-map
             class-name :class-name
             related-roles :related-roles} (:add-instance @model)]
      (let [instance-map-value @instance-map
            pk-list (get-pk-list class-name)
            pk-map (into {} (for [pk pk-list]
                              [pk (-> pk instance-map-value first)]))
            instance-index -1 #_(when (not-any? nil? (vals pk-map))
                                   (get-class-instance-by-attributes class-name pk-map))
            instance-found (>= instance-index 0)
            instance-map-value (if instance-found
                                 (reduce dissoc instance-map-value pk-list)
                                 instance-map-value)
            instance-index (if instance-found
                             instance-index
                             (add-class-instance class-name))
            ;[instance-node-handle instance-link-handle] (if instance-found
            ;                                              [(-> instance-index hg-get hg-link-first-target) instance-index]
            ;                                              (add-class-instance-return-with-link class-name))
            ;current-association-handle (-> shell-node-handle hg-incident hg-find-one atom)
            ]
        (reset! instance-map {})
        (doseq [[attribute-name attribute-values-list] instance-map-value]
          (doseq [attribute-value attribute-values-list]
            (add-attribute-instance instance-index class-name attribute-name attribute-value)))
        (doseq [{association-name :association-name
                 association-instance-number :association-instance-number
                 role-name :role-name} @related-roles]
          (add-role-instance))))
    (swap! model #(add-child (last @stack) qName %))
    (swap! stack drop-last)))


(defn -characters   ; char ch[], int start, int length
  [this ch start length]
  (-> this .state :string-builder deref (.append ch start length)))
