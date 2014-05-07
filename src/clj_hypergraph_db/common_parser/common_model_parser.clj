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


(defn add-field
  [field-token class-handle]
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
        (reset! new-field-token (assoc @new-field-token :attributes (cons path-token (:attributes @new-field-token))))))
    ;parsing type
    (let [type-token (find-first-item-by-type configuration :type)]
      (if (identity type-token)
        (reset! new-field-token (assoc @new-field-token :attributes (cons type-token (:attributes @new-field-token))))))
    ;returned value
    @new-field-token
    ))


(defn add-class
  [class-token metaclass-handle]
  (let [name (:name class-token)
        configuration (:attributes class-token)
        class-handle (add-node name)
        new-class-token (atom (assoc class-token
                                :attributes '()
                                :handle class-handle
                                :handle-to-top (add-value-link :class (list metaclass-handle class-handle))))]
    ;parsing fields
    (doall (map
             #(reset! new-class-token (assoc @new-class-token :attributes (cons (add-field % class-handle) (:attributes @new-class-token))))
             (find-all-items-by-type configuration :field)))
    ;parsing pk
    (let [pk-token (find-first-item-by-type configuration :pk)]
      (if pk-token
        (reset! new-class-token
                (assoc @new-class-token :attributes (cons (assoc pk-token
                                                            :handle (add-value-link :pk (map #(:handle (find-first-item-by-type-and-name (:attributes @new-class-token) :field %)) (:attributes pk-token))))
                                                          (:attributes @new-class-token))))))
    ;returned value
    @new-class-token
    ))


(comment
{:type :class
 :name :Person
 :attributes ({:type :field
               :name :Name
               :attributes ({:type :path
                             :name nil
                             :attributes ("Name" :Name-data)}
                            {:type :type
                             :name :string})}
              {:type :field
               :name :Surname
               :attributes ({:type :path
                             :name nil
                             :attributes (:Surname)}
                            {:type :type
                             :name :string})}
              {:type :pk
               :name nil
               :attributes (:Name :Surname)})}

)