(ns clj_hypergraph_db.xml_parser.xml_model_parser
  (:require [clj_hypergraph_db.common_parser.common_model_parser :refer :all]))


(defn create-model
  [parsed-config]
  (for [token parsed-config]
    (if (map? token)
      (if (= (:type token) :class)
        (add-class (:name token) (:attributes token))
        (if (#{:token} token)
          (create-model (:attributes token)))))))


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