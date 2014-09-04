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


(def seven-spaces (apply str (repeat 7 \space)))


(defn add-child
  [parent child]
  (update-in parent [:children] #(concat % [child])))


(defn remove-child
  [parent child-name]
  (update-in parent [:children] (fn [children] (filter #(not= child-name (:name %)) children))))


(defn finalize-text
  [this]
  (let [state (.state this)
        current (:current state)
        is-text-non-whitespace (:is-text-non-whitespace state)]
    (when @is-text-non-whitespace
      (let [new-name (-> current deref :description (str "-text-node") keyword)]
        (when-not (find-first-item-by-type-and-name (:children @current) :text new-name)
          (swap! current add-child {:type :text
                                    :name new-name}))
        (reset! is-text-non-whitespace false)))))


(defn parse-tree
  [token-map tab-count]
  (case (:type token-map)
    :token (apply str (concat ["(token \"" (:description token-map) "\" " (:name token-map)]
                              (apply concat (for [child (:children token-map)]
                                              (conj (cons \newline (repeat tab-count seven-spaces)) (parse-tree child (inc tab-count)))))
                              [")"]))
    :attribute (str "(attribute" (:description token-map) (:name token-map) ")")
    :text (str "(text" (:name token-map) ")")))


;
; clj_hypergraph_db.xml_parser.XMLPrototyperContentHandler extetds org.xml.sax.helpers.DefaultHandler
;


(defn -init
  [atom-for-returned-config]
  [[] {:current (atom {:type :root :children '()})
       :stack (atom '())
       :is-text-non-whitespace (atom false)
       :return atom-for-returned-config}])


(defn -startElement    ; String uri, String localName, String qName, Attributes attributes
  [this uri localName qName attributes]
  (let [state (.state this)
        current (:current state)
        stack (:stack state)]
    (finalize-text this)
    (let [child-name (keyword qName)]
      (swap! stack concat [(remove-child @current child-name)])
      (reset! current (if-let [selected-child (-> current deref :children (find-first-item-by-type-and-name :token child-name))]
                        selected-child
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
  (let [state (.state this)
        current (:current state)
        stack (:stack state)]
    (finalize-text this)
    (swap! current #(add-child (last @stack) %))
    (swap! stack drop-last)))


(defn -endDocument
  [this]
  (let [state (.state this)]
    (finalize-text this)
    (reset! (:return state) (-> state :current deref :children first (parse-tree 1)))))


(defn -characters   ; char ch[], int start, int length
  [this ch start length]
  (when (->> ch (drop start) (take length) (not-every? #{\newline \tab \space}))
    (-> this .state :is-text-non-whitespace (reset! true))))
