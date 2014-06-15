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
  (assoc parent :children (concat (:children parent) (list child))))


(defn remove-child
  [parent child-name]
  (assoc parent :children (filter #(not= child-name (:name %)) (:children parent))))


(defn finalize-text
  [this]
  (let [state (.state this)
        current (:current state)
        is-text-non-whitespace (:is-text-non-whitespace (.state this))]
    (if @is-text-non-whitespace
      (let [new-name (keyword (apply join (list "" (flatten [(map #(list (name (:name %)) "-") (concat (rest @(:stack state)) (list @current))) "text-node"]))))]
        (if (nil? (find-first-item-by-type-and-name (:children @current) :text new-name))
          (swap!
            current
            add-child
            {:type :text
             :name new-name}))
        (reset! is-text-non-whitespace false)))))


(defn parse-tree
  [token-map]
  (case (:type token-map)
    :token (apply list (concat (list 'token (:name token-map)) (map parse-tree (:children token-map))))
    :attribute (list 'attribute (:name token-map))
    :text (list 'text (:name token-map))))


;
; clj_hypergraph_db.xml_parser.XMLLoaderContentHandler
;


(defn -init
  [atom-for-returned-config]
  [[] {:current (atom {:type :root :children '()})
       :stack (atom '())
       :is-text-non-whitespace (atom false)}])


(defn -startElement    ; String uri, String localName, String qName, Attributes attributes
  [this uri localName qName attributes]
  (let [state (.state this)
        current (:current state)
        stack (:stack state)]
    (finalize-text this)
    (let [child-name (keyword qName)
          selected-child (find-first-item-by-type-and-name (:children @current) :token child-name)]
      (reset! stack (concat @stack (list (remove-child @current child-name))))
      (reset! current (if selected-child
                        selected-child
                        {:type :token :name child-name :children '()})))
    (doseq [attribute-index (range (.getLength attributes))]
      (let [attribute-name (keyword (.getQName attributes attribute-index))]
        (if (nil? (find-first-item-by-type-and-name (:children @current) :attribute attribute-name))
          (swap! current add-child {:type :attribute :name attribute-name}))))
    ))


(defn -endElement   ; String uri, String localName, String qName
  [this uri localName qName]
  (let [state (.state this)
        current (:current state)
        stack (:stack state)]
    (finalize-text this)
    (reset! current (add-child (last @stack) @current))
    (swap! stack drop-last)
    ))


(defn -endDocument
  [this]
  (do
    (finalize-text this)
    (prn (parse-tree (first (:children @(:current (.state this))))))))


(defn -characters   ; char ch[], int start, int length
  [this ch start length]
  (if (not= 0 (count (filter #(not (contains? #{\newline \tab \space} %)) (map #(nth ch %) (range start (+ start length))))))
    (reset! (:is-text-non-whitespace (.state this)) true)))
