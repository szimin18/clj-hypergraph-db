(ns clj_hypergraph_db.xml_parser.xml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]))


(defn create-model
  ([parsed-config metaclass-handle]
   (create-model parsed-config metaclass-handle '() '(:token :attribute :data)))
  ([parsed-config metaclass-handle relative-path available-path-types]
   (let [class-set (atom '())]
     (doseq [token (filter map? parsed-config)]
       (let [token-type (:type token)]
         (if (= token-type :class)
           (swap! class-set concat (list (add-class token
                                                    metaclass-handle
                                                    relative-path
                                                    parsed-config
                                                    available-path-types)))
           (if (contains? #{:token} token-type)
             (swap! class-set concat (create-model (:attributes token)
                                                   metaclass-handle
                                                   (concat relative-path (list (:name token)))
                                                   available-path-types))))))
     @class-set)))


;(nil
; {:type :token
;  :name "People"
;  :attributes ({:type :token
;                :name "Person"
;                :attributes ({:type :class
;                              :name :Person
;                              :attributes ({:type :field
;                                            :name :Name
;                                            :attributes ({:type :path
;                                                          :attributes ("Name" :Name-data)}
;                                                         {:type :type
;                                                          :name :string})}
;                                           {:type :field
;                                            :name :Surname
;                                            :attributes ({:type :path
;                                                          :attributes (:Surname)}
;                                                         {:type :type
;                                                          :name :string})}
;                                           {:type :pk
;                                            :attributes (:Name :Surname)})}
;                             {:type :attribute
;                              :name :Surname}
;                             {:type :token
;                              :name "Name"
;                              :attributes ({:type :data
;                                            :name :Name-data})})})})