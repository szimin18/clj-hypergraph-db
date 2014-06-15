(ns clj_hypergraph_db.xml_parser.XMLLoaderContentHandler
  (:require [clj_hypergraph_db.persistance.persistance_manager :refer :all]
            [clj_hypergraph_db.common_parser.common_model_parser :refer :all])
  (:gen-class
    :extends      org.xml.sax.helpers.DefaultHandler
    :state        state
    :init         init
    :constructors {[clojure.lang.ISeq] []}))


;
; helpers
;


(defn identical-paths?
  [path-one path-two]
  (if (= (count path-one) (count path-two))
    (zero? (count (filter #(not= (nth path-one %) (nth path-two %)) (range (count path-one)))))
    false))


(defn finalizeText
  [this]
  (let [current-path (:current-path (.state this))
        current-object-stack (:current-object-stack (.state this))
        current-classes (:current-classes (.state this))
        string-builder (:string-builder (.state this))
        string-builder-text (.toString @string-builder)]
    (if (count (filter #(not (contains? #{\newline \tab \space} %)) string-builder-text))
      (let [current-matching-classes (apply vector (for [class-config current-classes
                                                         :when (identical-paths?
                                                                 (:relative-path class-config)
                                                                 (take (count (:relative-path class-config)) @current-path))]
                                                     [class-config (drop (count (:relative-path class-config)) @current-path)]))]
        (doseq [class-vector current-matching-classes]
          (doseq [field-config (find-all-items-by-type (:attributes (first class-vector)) :field)]
            (if (identical-paths?
                  (for [path-token (:attributes (find-first-item-by-type (:attributes field-config) :path)) :when (= :token (:type path-token))] (:name path-token))
                  (second class-vector))
              (if (:name (find-first-item-by-type (:attributes (find-first-item-by-type (:attributes field-config) :path)) :data))
                (let [new-field-handle (add-node string-builder-text)]
                  (add-link (list new-field-handle (:handle field-config)))
                  (add-link (:name field-config) (list new-field-handle ((:name (first class-vector)) @current-object-stack))))))))))
    (reset! string-builder (StringBuilder.))
    ))


;
; clj_hypergraph_db.xml_parser.XMLLoaderContentHandler
;


(defn -init
  [current-classes]
  [[] {:current-path (atom (list))
       :current-object-stack (atom (hash-map))
       :current-classes current-classes
       :string-builder (atom (StringBuilder.))}])


(defn -startElement    ; String uri, String localName, String qName, Attributes attributes
  [this uri localName qName attributes]
  (let [current-path (:current-path (.state this))
        current-object-stack (:current-object-stack (.state this))
        current-classes (:current-classes (.state this))]
    (finalizeText this)
    (swap! current-path concat (list qName))
    (let [current-matching-classes (apply vector (for [class-config current-classes
                                                     :when (identical-paths?
                                                             (:relative-path class-config)
                                                             (take (count (:relative-path class-config)) @current-path))]
                                                 [class-config (drop (count (:relative-path class-config)) @current-path)]))]

      (doseq [class-vector (filter #(not (contains? @current-object-stack (:name (first %)))) current-matching-classes)]
        (let [new-class-handle (add-node (:name (first class-vector)))]
          (add-link (list new-class-handle (:handle (first class-vector))))
          (swap! current-object-stack assoc (:name (first class-vector)) new-class-handle)))

      (doseq [class-vector current-matching-classes]
        (doseq [field-config (find-all-items-by-type (:attributes (first class-vector)) :field)]
          (if (identical-paths?
                (for [path-token (:attributes (find-first-item-by-type (:attributes field-config) :path)) :when (= :token (:type path-token))] (:name path-token))
                (second class-vector))
            (if-let [new-field-name-keyword (:name (find-first-item-by-type (:attributes (find-first-item-by-type (:attributes field-config) :path)) :attribute))]
              (let [new-field-handle (add-node (.getValue attributes (name new-field-name-keyword)))]
                (add-link (list new-field-handle (:handle field-config)))
                (add-link (:name field-config) (list new-field-handle ((:name (first class-vector)) @current-object-stack))))))))
      )))


(defn -endElement   ; String uri, String localName, String qName
  [this uri localName qName]
  (let [current-path (:current-path (.state this))
        current-object-stack (:current-object-stack (.state this))
        current-classes (:current-classes (.state this))]
    (finalizeText this)
    (swap! current-path drop-last)
    (let [current-matching-classes (apply vector (for [class-config current-classes
                                                       :when (identical-paths?
                                                               (:relative-path class-config)
                                                               (take (count (:relative-path class-config)) @current-path))]
                                                   [class-config (drop (count (:relative-path class-config)) @current-path)]))]

      (doseq [class-name (filter #(not (contains? (hash-set (map (comp :name first) current-matching-classes)) %)) (keys @current-object-stack))]
        (swap! current-object-stack dissoc class-name))
      )))


(defn -endDocument
  [this]
  (finalizeText this))


(defn -characters   ; char ch[], int start, int length
  [this ch start length]
  (.append @(:string-builder (.state this)) ch start length))
