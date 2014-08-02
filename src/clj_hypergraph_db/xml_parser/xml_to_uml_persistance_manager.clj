(ns clj_hypergraph_db.xml_parser.xml_to_uml_persistance_manager
  (:import [org.xml.sax XMLReader]
           [javax.xml.parsers SAXParser SAXParserFactory]
           [java.io File PrintWriter]
           [clj_hypergraph_db.xml_parser XMLLoaderContentHandler])
  (:require [clj_hypergraph_db.xml_parser.xml_common_functions :refer :all]))


(defn load-input-data
  [extent-model access-vector]
  (let [xml-reader (.getXMLReader (.newSAXParser (SAXParserFactory/newInstance)))]
    (.setContentHandler xml-reader (XMLLoaderContentHandler. (:root extent-model)))
    (.parse xml-reader (string-to-file-url (first access-vector)))))
