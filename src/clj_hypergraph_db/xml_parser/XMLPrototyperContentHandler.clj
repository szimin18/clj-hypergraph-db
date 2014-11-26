(ns clj_hypergraph_db.xml_parser.XMLPrototyperContentHandler
  (:require [clj_hypergraph_db.persistance.persistance_manager :refer :all]
            [clj_hypergraph_db.common_parser.common_model_parser :refer :all])
  (:gen-class
    :extends      org.xml.sax.helpers.DefaultHandler
    :state        state
    :init         init
    :constructors {[clojure.lang.Atom] []})
  (:use [clojure.string :only [join]]))


;
; helpers
;


(defn add-child
  [parent child]
  (update-in parent [:children] #(concat % [child])))


(defn remove-child
  [parent child-name]
  (update-in parent [:children] (fn [children] (filter #(not= child-name (:name %)) children))))


(defn finalize-text
  [this]
  (let [{current :current
         is-text-non-whitespace :is-text-non-whitespace} (.state this)]
    (when @is-text-non-whitespace
      (let [new-name (-> @current :description (str "-text-node") keyword)]
        (when-not (find-first-item-by-type-and-name (:children @current) :text new-name)
          (swap! current add-child {:type :text
                                    :name new-name}))
        (reset! is-text-non-whitespace false)))))


;
; clj_hypergraph_db.xml_parser.XMLPrototyperContentHandler extetds org.xml.sax.helpers.DefaultHandler
;


(defn -init
  [atom-for-returned-config]
  [[] {:current atom-for-returned-config
       :stack (atom '())
       :is-text-non-whitespace (atom false)}])


(defn -startElement    ; String uri, String localName, String qName, Attributes attributes
  [this uri localName qName attributes]
  (let [{current :current
         stack :stack} (.state this)]
    (finalize-text this)
    (let [child-name (keyword qName)]
      (swap! stack concat [(remove-child @current child-name)])
      (reset! current (or (find-first-item-by-type-and-name (:children @current) :token child-name)
                          {:type :token
                           :name child-name
                           :children []
                           :description qName})))
    (doseq [attribute-index (range (.getLength attributes))]
      (let [q-name (.getQName attributes attribute-index)
            attribute-name (keyword (str q-name "-attribute"))]
        (when-not (find-first-item-by-type-and-name (:children @current) :attribute attribute-name)
          (swap! current add-child {:type :attribute
                                    :name attribute-name
                                    :description q-name}))))))


(defn -endElement   ; String uri, String localName, String qName
  [this uri localName qName]
  (let [{current :current
         stack :stack} (.state this)]
    (finalize-text this)
    (swap! current #(add-child (last @stack) %))
    (swap! stack drop-last)))


(defn -endDocument
  [this]
  (finalize-text this))


(defn -characters   ; char ch[], int start, int length
  [this ch start length]
  (when (->> ch (drop start) (take length) (not-every? #{\newline \tab \space}))
    (-> this .state :is-text-non-whitespace (reset! true))))
