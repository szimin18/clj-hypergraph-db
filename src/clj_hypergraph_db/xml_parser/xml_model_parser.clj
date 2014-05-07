(ns clj_hypergraph_db.xml_parser.xml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]))


(defn create-model
  [parsed-config metaclass-handle]
  (let [new-config (atom '())]
    (doall (map
             (fn [token]
               (if (= (:type token) :class)
                 (reset! new-config (cons (add-class token metaclass-handle) @new-config))
                 (if (contains? #{:token} (:type token))
                   (reset! new-config (cons (assoc token :attributes (create-model (:attributes token) metaclass-handle)) @new-config))
                   (reset! new-config (cons token @new-config)))))
             (filter map? parsed-config)))
    @new-config))


(comment nil
 {:type :token
  :name "People"
  :attributes ({:type :token
                :name "Person"
                :attributes ({:type :class
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
                             {:type :attribute
                              :name :Surname}
                             {:type :token
                              :name "Name"
                              :attributes ({:type :data
                                            :name :Name-data})})})})