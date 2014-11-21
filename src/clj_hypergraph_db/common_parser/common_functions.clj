(ns clj_hypergraph_db.common_parser.common_functions
  (:import (java.io File PrintWriter)))


(defn dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
  nested structure. keys is a sequence of keys. Any empty maps that result
  will not be present in the new structure."
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))


(defn merge-concat
  [& maps]
  (apply merge-with concat maps))


;todo for debuging


(defn- pr-rec-into-writer
  [s writer]
  (cond
    (nil? s) (.print writer "nil")
    (map? s) (do
               (.print writer "{")
               (when-first [[k v] s]
                 (pr-rec-into-writer k writer)
                 (.print writer " ")
                 (pr-rec-into-writer v writer)
                 (doseq [[k v] (rest s)]
                   (.println writer)
                   (pr-rec-into-writer k writer)
                   (.print writer " ")
                   (pr-rec-into-writer v writer)))
               (.print writer "}"))
    (vector? s) (do
                  (.print writer "[")
                  (when-first [e s]
                    (pr-rec-into-writer e writer)
                    (doseq [e (rest s)]
                      (.println writer)
                      (pr-rec-into-writer e writer)))
                  (.print writer "]"))
    (= clojure.lang.PersistentHashSet (class s)) (do
                                                   (.print writer "#{")
                                                   (when-first [e s]
                                                     (pr-rec-into-writer e writer)
                                                     (doseq [e (rest s)]
                                                       (.println writer)
                                                       (pr-rec-into-writer e writer)))
                                                   (.print writer "}"))
    (list? s) (do
                (.print writer "(")
                (when-first [e s]
                  (pr-rec-into-writer e writer)
                  (doseq [e (rest s)]
                    (.println writer)
                    (pr-rec-into-writer e writer)))
                (.print writer ")"))
    (or (keyword? s) (number? s)) (.print writer s)
    (string? s) (.print writer (str \" s \"))
    (= clojure.lang.Atom (class s)) (pr-rec-into-writer ["ATOM" @s] writer)
    (#{clojure.lang.LazySeq clojure.lang.Cons} (class s)) (pr-rec-into-writer (apply list s) writer)
    (#{clojure.lang.ArraySeq} (class s)) (pr-rec-into-writer (vec s) writer)
    (= java.lang.Boolean (class s))
    (if s
      (.print writer "true")
      (.print writer "false"))
    :else (pr-rec-into-writer (str "##### NOT HANDLED " (class s) " #####") writer)))


(defn pr-rec
  [s]
  (pr-rec-into-writer s System/out))


(defn prn-rec
  [s]
  (pr-rec-into-writer s System/out)
  (.println System/out))


(defn pr-rec-file
  [s filename]
  (try
    (.remove (File. filename))
    (catch Exception e))
  (with-open [print-writer (PrintWriter. filename)]
    (pr-rec-into-writer s print-writer)
    (.flush print-writer)))


(defn prn-rec-file
  [s filename]
  (try
    (.remove (File. filename))
    (catch Exception e))
  (with-open [print-writer (PrintWriter. filename)]
    (pr-rec-into-writer s print-writer)
    (.println print-writer)
    (.flush print-writer)))
