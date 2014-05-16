(ns clj_hypergraph_db.xml_parser.xml_persistance_manager
  (:import [javax.xml.parsers SAXParser SAXParserFactory]
           [org.xml.sax XMLReader]
           [clj_hypergraph_db.xml_parser XMLContentHandler]
           [java.io File])
  (:use [clojure.string :only (join)]))


(defn string-to-file-url
  [filename]
  (let [path (.getAbsolutePath (File. filename))]
    (let [path (if (not= File/separatorChar (first "/"))
                 (.replace path File/separatorChar (first "/"))
                 path)]
      (let [path (if (not (.startsWith path "/"))
                   (join ["/" path])
                   path)]
        (join ["file:" path])))))


(defn load-input-data
  [configuration file-path]
  (let [xml-reader (.getXMLReader (.newSAXParser (SAXParserFactory/newInstance)))]
    (.setContentHandler xml-reader (XMLContentHandler. configuration))
    (.parse xml-reader (string-to-file-url file-path))))
