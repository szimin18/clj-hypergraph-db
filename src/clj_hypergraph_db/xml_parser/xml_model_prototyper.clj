(ns clj_hypergraph_db.xml_parser.xml_model_prototyper
  (:import [org.xml.sax XMLReader]
           [javax.xml.parsers SAXParser SAXParserFactory]
           [java.io File]
           [clj_hypergraph_db.xml_parser XMLPrototyperContentHandler])
  (:require [clj_hypergraph_db.xml_parser.xml_common_functions :refer :all]))


(def seven-spaces (apply str (repeat 7 \space)))


(defn parse-tree
  [{type :type
    name :name
    children :children
    description :description} tab-count string-builder]
  (case type
    :attribute (.append string-builder (str "(attribute \"" description "\" " name ")"))
    :text (.append string-builder (str "(text " name ")"))
    :token (do
             (.append string-builder (str "(token \"" description "\" " name))
             (doseq [child children]
               (.append string-builder (apply str \newline (repeat tab-count seven-spaces)))
               (parse-tree child (inc tab-count) string-builder))
             (.append string-builder ")"))))


(defn create-prototype-configuration
  [configuration-file-path access-vector]
  (let [xml-reader (.getXMLReader (.newSAXParser (SAXParserFactory/newInstance)))
        atom-for-new-configuration (atom {:type :root :children '()})
        data-file-path (first access-vector)
        string-builder (doto (StringBuilder.)
                         (.append (str "(database :xml\n"
                                       "          (default-path \"" data-file-path "\"))\n\n")))]
    (try
      (.remove (File. configuration-file-path))
      (catch Exception e))
    (.setContentHandler xml-reader (XMLPrototyperContentHandler. atom-for-new-configuration))
    (.parse xml-reader (string-to-file-url data-file-path))
    (-> @atom-for-new-configuration :children first (parse-tree 1 string-builder))
    (spit configuration-file-path (.toString string-builder))))
