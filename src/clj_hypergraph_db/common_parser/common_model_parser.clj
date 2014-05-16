(ns clj_hypergraph_db.common_parser.common_model_parser
  (:require [clj_hypergraph_db.persistance.persistance_manager :refer :all])
  (:use [clojure.tools.logging :only (info)]))


(defn find-all-items-by-type
  [items type]
  (if (seq? items)
    (filter #(= type (:type %)) items)
    '()))


(defn find-first-item-by-type
  [items type]
  (if (seq? items)
    (first (filter #(= type (:type %)) items))
    nil))


(defn find-first-item-by-type-and-name
  [items type name]
  (if (seq? items)
    (first (filter #(and (= name (:name %)) (= type (:type %))) items))
    nil))


(defn eval-path-tokens
  [unparsed-path-token-list parsed-config available-path-types]
  (if (empty? unparsed-path-token-list)
    '()
    (let [selected-token (first (filter identity (map #(find-first-item-by-type-and-name parsed-config % (first unparsed-path-token-list)) available-path-types)))]
      (concat (list {:type (:type selected-token) :name (:name selected-token)}) (eval-path-tokens (rest unparsed-path-token-list) (:attributes selected-token) available-path-types)))))


(defn eval-path
  [path-token parsed-config available-path-types]
  (assoc path-token :attributes (eval-path-tokens (:attributes path-token) parsed-config available-path-types)))


(defn add-field
  [field-token class-handle parsed-config available-path-types]
  (let [name (:name field-token)
        configuration (:attributes field-token)
        field-handle (add-node name)
        new-field-token (atom (assoc field-token
                                :attributes '()
                                :handle field-handle
                                :handle-to-top (add-value-link :field (list class-handle field-handle))))]
    ;parsing path
    (let [path-token (find-first-item-by-type configuration :path)]
      (if (identity path-token)
        (swap! new-field-token assoc :attributes (cons (eval-path path-token parsed-config available-path-types) (:attributes @new-field-token)))))
    ;parsing type
    (let [type-token (find-first-item-by-type configuration :type)]
      (if (identity type-token)
        (swap! new-field-token assoc :attributes (cons type-token (:attributes @new-field-token)))))
    ;returned value
    @new-field-token
    ))


(defn add-class
  [class-token metaclass-handle relative-path parsed-config available-path-types]
  (let [name (:name class-token)
        configuration (:attributes class-token)
        class-handle (add-node name)
        new-class-token (atom (assoc class-token
                                :attributes '()
                                :relative-path relative-path
                                :handle class-handle
                                :handle-to-top (add-value-link :class (list metaclass-handle class-handle))))]
    ;parsing fields
    (doseq [field-token (find-all-items-by-type configuration :field)]
      (swap! new-class-token assoc :attributes (cons (add-field field-token class-handle parsed-config available-path-types) (:attributes @new-class-token))))
    ;parsing pk
    (let [pk-token (find-first-item-by-type configuration :pk)]
      (if pk-token
        (swap! new-class-token assoc :attributes (cons (assoc pk-token
                                                         :handle (add-value-link :pk (map #(:handle (find-first-item-by-type-and-name (:attributes @new-class-token) :field %)) (:attributes pk-token))))
                                                       (:attributes @new-class-token)))))
    ;returned value
    @new-class-token
    ))


;{:type :class
; :name :Person
; :attributes ({:type :field
;               :name :Name
;               :attributes ({:type :path
;                             :attributes ("Name" :Name-data)}
;                            {:type :type
;                             :name :string})}
;              {:type :field
;               :name :Surname
;               :attributes ({:type :path
;                             :attributes (:Surname)}
;                            {:type :type
;                             :name :string})}
;              {:type :pk
;               :attributes (:Name :Surname)})}
