(ns clj_hypergraph_db.xml_parser.uml_to_xml_persistance_manager
  (:require [clj_hypergraph_db.xml_parser.xml_common_functions :refer :all]
            [clj_hypergraph_db.hdm_parser.hdm_uml_model_manager :refer :all])
  (:import [java.io File PrintWriter]))


(defn write-subtoken
  [token-name xml-token print-writer tab-number]
  (let [token-name-occured (atom false)]
    (doseq [add-text-mapping (:add-text-mapping xml-token)]
      (reset! token-name-occured true)
      (if-let [handle (iterator-get @(:class-instance-iterator add-text-mapping))]
        (doseq [attribute-value (get-instance-extensions
                                  handle
                                  (:attribute-name add-text-mapping))]
          (.println print-writer (str
                                   (apply str (repeat tab-number \tab))
                                   "<" token-name ">" attribute-value "</" token-name ">")))))
    (doseq [add-token (:add-token xml-token)]
      (let [iterator @(:iterator add-token)
            current-handle (atom (iterator-next iterator))]
        (while @current-handle
          (reset! token-name-occured true)
          (.print print-writer (str (apply str (repeat tab-number \tab)) "<" token-name))
          (doseq [[attribute-name attribute-token] (:attributes xml-token)]
            (doseq [add-attribute-mapping (:add-attribute-mapping attribute-token)]
              (doseq [attribute-value (get-instance-extensions
                                        (iterator-get @(:class-instance-iterator add-attribute-mapping))
                                        (:attribute-name add-attribute-mapping))]
                (.print print-writer (str " " attribute-name "=\"" attribute-value "\"")))))
          (.println print-writer ">")
          (doseq [[child-name child-token] (:children xml-token)]
            (write-subtoken child-name child-token print-writer (inc tab-number)))
          (.println print-writer (str (apply str (repeat tab-number \tab)) "</" token-name ">"))
          (reset! current-handle (iterator-next iterator)))))
    (if (false? @token-name-occured)
      (do
        (.println print-writer (str (apply str (repeat tab-number \tab)) "<" token-name ">"))
        (doseq [[child-name child-token] (:children xml-token)]
          (write-subtoken child-name child-token print-writer (inc tab-number)))
        (.println print-writer (str (apply str (repeat tab-number \tab)) "</" token-name ">"))))))


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


;(defn write-output-data
;  [configuration file-path]
;  (do
;    (try
;      (.remove (File. file-path))
;      (catch Exception e))
;    (with-open [print-writer (PrintWriter. file-path)]
;      (let [tab-counter (atom 0)
;            spaces-in-tab 4]
;        (doseq [class-config configuration]
;          (doseq [token (drop-last (:relative-path class-config))]
;            (.println print-writer (str (apply str (repeat @tab-counter (apply str (repeat spaces-in-tab \space)))) "<" token ">"))
;            (swap! tab-counter inc))
;          (doseq [class-object (get-all-objects-of-class (:name class-config))]
;            (.print print-writer (str (apply str (repeat @tab-counter "    ")) "<" (last (:relative-path class-config))))
;            (doseq [field-config (find-all-items-by-type (:attributes class-config) :field)]
;              (let [path (:attributes (find-first-item-by-type (:attributes field-config) :path))]
;                (if (and (= 1 (count path)) (= :attribute (:type (first path))))
;                  (.print print-writer (str " " (name (:name field-config)) "=\"" ((:name field-config) class-object) "\"")))))
;            (.println print-writer ">")
;            (swap! tab-counter inc)
;            (doseq [field-config (find-all-items-by-type (:attributes class-config) :field)]
;              (let [path (:attributes (find-first-item-by-type (:attributes field-config) :path))]
;                (doseq [token (drop-last (filter #(= :token (:type %)) path))]
;                  (.println print-writer (str (apply str (repeat @tab-counter (apply str (repeat spaces-in-tab \space)))) "<" token ">"))
;                  (swap! tab-counter inc))
;                (let [last-token (last (filter #(= :token (:type %)) path))
;                      specific-token (first (filter #(not= :token (:type %)) path))]
;                  (if last-token
;                    (do
;                      (.print print-writer (apply str (repeat @tab-counter (apply str (repeat spaces-in-tab \space)))))
;                      (if (= :attribute (:type specific-token))
;                        (.println print-writer (str "<" (:name last-token) " " (name (:name field-config)) "=\"" ((:name field-config) class-object) "\"/>"))
;                        (.println print-writer (str "<" (:name last-token) ">" ((:name field-config) class-object) "</" (:name last-token) ">"))))))
;                (doseq [token (drop-last (filter #(= :token (:type %)) path))]
;                  (swap! tab-counter dec)
;                  (.println print-writer (str (apply str (repeat @tab-counter "    ")) "</" token ">")))))
;            (swap! tab-counter dec)
;            (.println print-writer (str (apply str (repeat @tab-counter "    ")) "</" (last (:relative-path class-config)) ">")))
;          (doseq [token (reverse (drop-last (:relative-path class-config)))]
;            (swap! tab-counter dec)
;            (.println print-writer (str (apply str (repeat @tab-counter "    ")) "</" token ">"))))
;        (.flush print-writer)))))
