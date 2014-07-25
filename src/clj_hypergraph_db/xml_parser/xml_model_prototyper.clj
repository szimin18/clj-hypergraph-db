(ns clj_hypergraph_db.xml_parser.xml_model_prototyper
  (:import [org.xml.sax XMLReader]
           [javax.xml.parsers SAXParser SAXParserFactory]
           [java.io File]
           [clj_hypergraph_db.xml_parser XMLPrototyperContentHandler])
  (:require [clj_hypergraph_db.persistance.persistance_manager :refer :all]
            [clj_hypergraph_db.xml_parser.xml_common_functions :refer :all]))


(defn create-prototype-configuration
  [configuration-file-path access-vector]
  (do
    (try
      (.remove (File. configuration-file-path))
      (catch Exception e))
    (let [xml-reader (.getXMLReader (.newSAXParser (SAXParserFactory/newInstance)))
          atom-for-new-configuration (atom nil)
          data-file-path (first access-vector)]
      (.setContentHandler xml-reader (XMLPrototyperContentHandler. atom-for-new-configuration))
      (.parse xml-reader (string-to-file-url data-file-path))
      (spit configuration-file-path (str "(database :xml\n          (default-path \"" data-file-path "\"))\n\n" @atom-for-new-configuration)))))
