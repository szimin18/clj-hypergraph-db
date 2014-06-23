(ns clj_hypergraph_db.xml_parser.xml_model_prototyper
  (:import [org.xml.sax XMLReader]
           [javax.xml.parsers SAXParser SAXParserFactory]
           [java.io File]
           [clj_hypergraph_db.xml_parser XMLPrototyperContentHandler])
  (:require [clj_hypergraph_db.persistance.persistance_manager :refer :all]
            [clj_hypergraph_db.xml_parser.xml_common_functions :refer :all]))


(defn create-prototype-of-xml-configuration
  [data-file-path configuration-file-path]
  (do
    (try
      (.remove (File. configuration-file-path))
      (catch Exception e))
    (let [xml-reader (.getXMLReader (.newSAXParser (SAXParserFactory/newInstance)))
          atom-for-new-configuration (atom nil)]
      (.setContentHandler xml-reader (XMLPrototyperContentHandler. atom-for-new-configuration))
      (.parse xml-reader (string-to-file-url data-file-path))
      (spit configuration-file-path @atom-for-new-configuration))))
