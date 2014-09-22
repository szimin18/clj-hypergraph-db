(ns clj_hypergraph_db.xml_parser.uml_to_xml_persistance_manager
  (:require [clj_hypergraph_db.xml_parser.xml_common_functions :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all])
  (:import [java.io File PrintWriter]))


(declare write-subtoken)


(defn get-values
  [mapping-token]
  (when-let [iterator (:class-instance-iterator mapping-token)]
    (when-let [handle (iterator-get @iterator)]
      (get-instance-attributes handle (:attribute-name mapping-token)))))


(defn tabs
  [tab-count]
  (apply str (repeat tab-count \tab)))


(defn write-all-subtokens
  [xml-token print-writer tab-count token-starter]
  (let [any-subtoken-occured (atom false)]
    (doseq [[child-name child-token] (:children xml-token)]
      (if (write-subtoken child-name child-token print-writer (inc tab-count) token-starter)
        (reset! any-subtoken-occured true)))
    @any-subtoken-occured))


(defn write-subtoken
  [token-name xml-token print-writer tab-count token-starter]
  (let [token-name-occured (atom false)]
    ;print add-tokens
    (doseq [add-token (:add-token xml-token)]
      (let [iterator @(:iterator add-token)
            current-handle (atom (iterator-next iterator))]
        (when @current-handle
          (reset! token-name-occured (token-starter print-writer)))
        ;print token
        (while @current-handle
          ;start token
          (.print print-writer (str (tabs tab-count) "<" token-name))
          ;print attributes
          (doseq [add-attribute-mapping (:add-attribute-mapping xml-token)]
            (let [attribute-name (:attribute-string-name add-attribute-mapping)]
              (doseq [attribute-value (get-values add-attribute-mapping)]
                (.print print-writer (str " " attribute-name "=\"" attribute-value "\"")))))
          ;end token
          (.print print-writer ">")
          (if-let [attribute-value (some #(first (get-values %)) (:add-text-mapping xml-token))]
            (.print print-writer attribute-value)
            (do
              (.println print-writer)
              (write-all-subtokens xml-token print-writer tab-count token-starter)
              (.print print-writer (tabs tab-count))))
          ;print ending token
          (.println print-writer (str "</" token-name ">"))
          ;reset iterator
          (reset! current-handle (iterator-next iterator)))
        (iterator-reset iterator)))
    ;print text values
    (when-not @token-name-occured
      (doseq [add-text-mapping (:add-text-mapping xml-token)]
        (let [attribute-values (get-values add-text-mapping)]
          (when (not-empty attribute-values)
            (reset! token-name-occured (token-starter print-writer)))
          (doseq [attribute-value attribute-values]
            (.println print-writer (str (tabs tab-count) "<" token-name ">" attribute-value "</" token-name ">"))))))
    ;write subtokens of container
    (or
      @token-name-occured
      (let [token-started (atom false)
            token-starter (fn [print-writer]
                            (do
                              '(token-starter print-writer)
                              (when-not @token-started
                                (reset! token-started true)
                                (.println print-writer (str (tabs tab-count) "<" token-name ">")))
                              true))]
        (when (write-all-subtokens xml-token print-writer tab-count token-starter)
          (.println print-writer (str (tabs tab-count) "</" token-name ">"))
          true)))))


(defn write-output-data
  [extent-model access-vector]
  (let [file-path (first access-vector)]
    (try
      (.remove (File. file-path))
      (catch Exception e))
    (with-open [print-writer (PrintWriter. file-path)]
      (write-all-subtokens (:root extent-model) print-writer -1 #(vector %))
      (.flush print-writer))))
