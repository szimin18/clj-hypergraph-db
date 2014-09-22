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


(defn finalizeText
  [this]
  (let [state (.state this)
        model (:model state)
        string-builder (:string-builder state)
        string-builder-text (.toString @string-builder)]
    (when (not-every? #{\newline \tab \space} string-builder-text)
      (doseq [add-attribute-from-text (:add-attribute-from-text @model)]
        (swap! (:instance-map add-attribute-from-text)
               #(merge-with concat % (hash-map (:attribute-name add-attribute-from-text) (list string-builder-text)))))
      (doseq [add-role-from-text-pk (:add-role-from-text-pk @model)]
        (swap!
          (:instance-handle add-role-from-text-pk)
          add-role-instance-pk
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
      (reset! (:instance-map add-instance) {})
      (reset! (:instance-node-handle add-instance) (hg-add-node :shell)))
    (doseq [add-association (:add-association @model)]
      (reset! (:instance-handle add-association) (add-association-instance (:association-name add-association))))
    (doseq [add-role (:add-role @model)]
      (swap!
        (:instance-handle add-role)
        add-role-instance
        (:association-name add-role)
        (:role-name add-role)
        @(:target-instance-handle add-role)))
    (let [model-attributes (:attributes @model)]
      (doseq [attribute-index (range (.getLength attributes))]
        (let [attribute-name (.getQName attributes attribute-index)
              attribute-value (.getValue attributes attribute-index)]
          (doseq [add-attribute (:add-attribute (get model-attributes attribute-name))]
            (swap! (:instance-map add-attribute)
                   #(merge-with concat % (hash-map (:attribute-name add-attribute) (list attribute-value)))))
          (doseq [add-role-pk (:add-role-pk (get model-attributes attribute-name))]
            (swap!
              (:instance-handle add-role-pk)
              add-role-instance-pk
              (:association-name add-role-pk)
              (:role-name add-role-pk)
              attribute-value)))))))


(defn -endElement   ; String uri, String localName, String qName
  [this uri localName qName]
  (let [state (.state this)
        stack (:stack state)
        model (:model state)]
    (finalizeText this)
    (doseq [add-instance (:add-instance @model)]
      (let [instance-map @(:instance-map add-instance)
            class-name (:class-name add-instance)
            pk-list (get-pk-list class-name)
            pk-map (reduce
                     #(assoc %1 %2 (first (instance-map %2)))
                     {}
                     (get-pk-list class-name))
            instance-link-handle (get-class-instance-by-attributes class-name pk-map)
            instance-map (if (and instance-link-handle (not-any? nil? (vals pk-map)))
                           (reduce dissoc instance-map pk-list)
                           instance-map)
            [instance-node-handle instance-link-handle] (if (and instance-link-handle (not-any? nil? (vals pk-map)))
                                                          [(-> instance-link-handle hg-get hg-link-first-target) instance-link-handle]
                                                          (add-class-instance-return-with-link class-name))
            shell-node-handle @(:instance-node-handle add-instance)
            current-association-handle (atom (hg-find-one (hg-incident shell-node-handle)))]
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
