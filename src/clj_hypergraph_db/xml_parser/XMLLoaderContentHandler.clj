(ns clj_hypergraph_db.xml_parser.XMLLoaderContentHandler
  (:require [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all]
            [clj_hypergraph_db.common_parser.common_model_parser :refer :all])
  (:gen-class
    :extends      org.xml.sax.helpers.DefaultHandler
    :state        state
    :init         init
    :constructors {[clojure.lang.PersistentArrayMap] []}))


;
; helpers
;


(defn prnr
  [model sp]
  (do
    (println (apply str (concat (repeat sp "   ") [(:name model)])))
    (println (apply str (concat (repeat sp "   ") [(:attributes model)])))
    (doseq [c (vals (:children model))]
      (prnr c (inc sp)))))


(defn add-child
  [parent child-name child-body]
  (assoc parent :children (assoc (:children parent) child-name child-body)))


(defn remove-child
  [parent child-name]
  (assoc parent :children (dissoc (:children parent) child-name)))


(defn finalizeText
  [this]
  (let [state (.state this)
        string-builder (:string-builder state)
        string-builder-text (.toString @string-builder)]
    (if (count (filter #(not (contains? #{\newline \tab \space} %)) string-builder-text))
      (doseq [add-attribute-from-text (:add-attribute-from-text @(:model state))]
        (comment add-attribute-instance
          @(:instance-handle add-attribute-from-text)
          (:class-name add-attribute-from-text)
          (:attribute-name add-attribute-from-text)
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
      (reset! (:instance-handle add-instance) (add-class-instance (:class-name add-instance))))
    (let [model-attributes (:attributes @model)]
      (doseq [attribute-index (range (.getLength attributes))]
        (let [attribute-value (.getValue attributes attribute-index)]
          (doseq [add-attribute (:add-attribute (model-attributes (.getQName attributes attribute-index)))]
            (comment add-attribute-instance
              @(:instance-handle add-attribute)
              (:class-name add-attribute)
              (:attribute-name add-attribute)
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
