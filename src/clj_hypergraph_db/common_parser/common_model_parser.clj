(ns clj_hypergraph_db.common_parser.common_model_parser
  (:require [clj_hypergraph_db.persistance.persistance_manager :refer :all]))


(defn add-class
  [class-token metaclass-handle]
  (let [name (:name class-token)
        configuration (:attributes class-token)
        class-handle (add-node name)
        new-class-token-atom (atom (assoc class-token
                                     :attributes '()
                                     :handle class-handle
                                     :handle-to-top (add-value-link :class (list metaclass-handle class-handle))))]
    (for [field-token (find-all-items-by-type configuration :field)]
      (reset! new-class-token-atom
              (assoc @new-class-token-atom :attributes (cons (add-field field-token class-handle)
                                                               (:attributes @new-class-token-atom)))))
    (let [pk-token (find-first-item-by-type configuration :pk)]
      (if (identity pk-token)
        (reset! new-class-token-atom
                (assoc @new-class-token-atom :attributes (cons (assoc pk-token
                                                                 :handle (add-value-link :pk (map #(:handle (find-first-item-by-type-and-name :field %)) (:attributes pk-token))))
                                                               (:attributes @new-class-token-atom))))))
    ))


(defn add-field
  [field-token class-handle]
  (let [name (:name field-token)
        configuration (:attributes field-token)
        field-handle (add-node name)
        new-field-token-atom (atom (assoc field-token
                                     :attributes '()
                                     :handle field-handle
                                     :handle-to-top (add-value-link :field (list class-handle field-handle))))]
    (let [path-token (find-first-item-by-type configuration :path)]
      (if (identity path-token)
        (reset! new-field-token-atom (assoc @new-field-token-atom :attributes (cons path-token (:attributes @new-field-token-atom))))))
    (let [type-token (find-first-item-by-type configuration :type)]
      (if (identity type-token)
        (reset! new-field-token-atom (assoc @new-field-token-atom :attributes (cons type-token (:attributes @new-field-token-atom))))))
    ))


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