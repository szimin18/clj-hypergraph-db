(ns clj_hypergraph_db.xml_parser.XMLLoaderContentHandler
  (:require [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]
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
  (let [state (.state this)
        model (:model state)
        string-builder (:string-builder state)
        string-builder-text (.toString @string-builder)]
    (when (not-every? #{\newline \tab \space} string-builder-text)
      (doseq [add-attribute-from-text (:add-attribute-from-text @model)]
        (add-attribute-instance
          @(:instance-handle add-attribute-from-text)
          (:class-name add-attribute-from-text)
          (:attribute-name add-attribute-from-text)
          string-builder-text))
      (doseq [add-role-from-text-pk (:add-role-from-text-pk @model)]
        (add-role-instance-pk
          @(:instance-handle add-role-from-text-pk)
          (:association-name add-role-from-text-pk)
          (:role-name add-role-from-text-pk)
          string-builder-text)))
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
  (let [state (.state this)
        stack (:stack state)
        model (:model state)]
    (finalizeText this)
    (swap! stack concat [(remove-child @model qName)])
    (swap! model #((:children %) qName))
    (doseq [add-instance (:add-instance @model)]
      (let [[instance-node-handle instance-link-handle] (add-class-instance-return-with-link (:class-name add-instance))]
        (reset! (:instance-link-handle add-instance) instance-link-handle)
        (reset! (:instance-node-handle add-instance) instance-node-handle)))
    (doseq [add-association (:add-association @model)]
      (reset! (:instance-handle add-association) (add-association-instance (:association-name add-association))))
    (doseq [add-role (:add-role @model)]
      (add-role-instance
        @(:instance-handle add-role)
        (:association-name add-role)
        (:role-name add-role)
        @(:target-instance-handle add-role)))
    (let [model-attributes (:attributes @model)]
      (doseq [attribute-index (range (.getLength attributes))]
        (let [attribute-name (.getQName attributes attribute-index)
              attribute-value (.getValue attributes attribute-index)]
          (doseq [add-attribute (:add-attribute (get model-attributes attribute-name))]
            (add-attribute-instance
              @(:instance-handle add-attribute)
              (:class-name add-attribute)
              (:attribute-name add-attribute)
              attribute-value))
          (doseq [add-role-pk (:add-role-pk (get model-attributes attribute-name))]
            (add-role-instance-pk
              @(:instance-handle add-role-pk)
              (:association-name add-role-pk)
              (:role-name add-role-pk)
              attribute-value)))))))


(defn -endElement   ; String uri, String localName, String qName
  [this uri localName qName]
  (let [state (.state this)
        stack (:stack state)
        model (:model state)]
    (finalizeText this)
    (swap! model #(add-child (last @stack) qName %))
    (swap! stack drop-last)))


(defn -endDocument
  [this]
  (finalizeText this))


(defn -characters   ; char ch[], int start, int length
  [this ch start length]
  (.append @(:string-builder (.state this)) ch start length))
