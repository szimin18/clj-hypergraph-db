(ns clj_hypergraph_db.xml_parser.xml_to_uml_persistance_manager
  (:import [org.xml.sax XMLReader]
           [javax.xml.parsers SAXParser SAXParserFactory]
           [java.io File PrintWriter]
           [clj_hypergraph_db.xml_parser XMLLoaderContentHandler])
  (:require [clj_hypergraph_db.persistance.persistance_manager :refer :all]
            [clj_hypergraph_db.common_parser.common_model_parser :refer :all]
            [clj_hypergraph_db.xml_parser.xml_common_functions :refer :all]))


(defn load-input-data
  [extent-model access-vector]
  (let [xml-reader (.getXMLReader (.newSAXParser (SAXParserFactory/newInstance)))]
    (.setContentHandler xml-reader (XMLLoaderContentHandler. (:root extent-model)))
    (.parse xml-reader (string-to-file-url (first access-vector)))))


;(defn write-output-xml-data
;  [configration file-path]
;  (do
;    (try
;      (.remove (File. file-path))
;      (catch Exception e))
;    (with-open [print-writer (PrintWriter. file-path)]
;      (let [tab-counter (atom 0)
;            spaces-in-tab 4]
;        (doseq [class-config configration]
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
