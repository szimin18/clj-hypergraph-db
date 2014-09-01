(ns clj_hypergraph_db.xml_parser.uml_to_xml_persistance_manager
  (:require [clj_hypergraph_db.xml_parser.xml_common_functions :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all])
  (:import [java.io File PrintWriter]))


(defn write-subtoken
  [token-name xml-token print-writer tab-number]
  (let [token-name-occured (atom false)]
    (doseq [add-token (:add-token xml-token)]
      (let [iterator @(:iterator add-token)
            current-handle (atom (iterator-next iterator))]
        (while @current-handle
          (reset! token-name-occured true)
          (.print print-writer (str (apply str (repeat tab-number \tab)) "<" token-name))
          (doall (for [[attribute-name attribute-token] (:attributes xml-token)
                       add-attribute-mapping (:add-attribute-mapping attribute-token)
                       attribute-value (get-instance-extensions
                                         (iterator-get @(:class-instance-iterator add-attribute-mapping))
                                         (:attribute-name add-attribute-mapping))]
                   (.print print-writer (str " " attribute-name "=\"" attribute-value "\""))))
          (.print print-writer ">")
          (if (when-let [add-text-mapping (first (:add-text-mapping xml-token))]
                (when-let [handle (iterator-get @(:class-instance-iterator add-text-mapping))]
                  (when-let [attribute-value (first (get-instance-extensions
                                                      handle
                                                      (:attribute-name add-text-mapping)))]
                    (.print print-writer attribute-value)
                    true)))
            (.println print-writer (str "</" token-name ">"))
            (do
              (.println print-writer)
              (doseq [[child-name child-token] (:children xml-token)]
                (write-subtoken child-name child-token print-writer (inc tab-number)))
              (.println print-writer (str (apply str (repeat tab-number \tab)) "</" token-name ">"))))
          (reset! current-handle (iterator-next iterator)))
        (iterator-reset iterator)))
    (when-not @token-name-occured
      (doseq [add-text-mapping (:add-text-mapping xml-token)]
        (reset! token-name-occured true)
        (when-let [handle (iterator-get @(:class-instance-iterator add-text-mapping))]
          (doseq [attribute-value (get-instance-extensions
                                    handle
                                    (:attribute-name add-text-mapping))]
            (.println print-writer (str
                                     (apply str (repeat tab-number \tab))
                                     "<" token-name ">" attribute-value "</" token-name ">"))))))
    (when-not @token-name-occured
      (.println print-writer (str (apply str (repeat tab-number \tab)) "<" token-name ">"))
      (doseq [[child-name child-token] (:children xml-token)]
        (write-subtoken child-name child-token print-writer (inc tab-number)))
      (.println print-writer (str (apply str (repeat tab-number \tab)) "</" token-name ">")))))


(defn write-output-data
  [extent-model access-vector]
  (let [file-path (first access-vector)]
    (try
      (.remove (File. file-path))
      (catch Exception e))
    (with-open [print-writer (PrintWriter. file-path)]
      (doseq [[child-name child-token] (:children (:root extent-model))]
        (write-subtoken child-name child-token print-writer 0))
      (.flush print-writer))))
