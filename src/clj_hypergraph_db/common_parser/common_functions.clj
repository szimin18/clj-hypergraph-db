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


;todo for debuging


(defn- pr-rec-into-writer
  [s writer]
  (if (nil? s)
    (.print writer "nil")
    (if (map? s)
      (do
        (.print writer "{")
        (if-let [[k v] (first s)]
          (do
            (pr-rec-into-writer k writer)
            (.print writer " ")
            (pr-rec-into-writer v writer)
            (doseq [[k v] (rest s)]
              (.println writer)
              (pr-rec-into-writer k writer)
              (.print writer " ")
              (pr-rec-into-writer v writer))))
        (.print writer "}"))
      (if (vector? s)
        (do
          (.print writer "[")
          (if-let [e (first s)]
            (do
              (pr-rec-into-writer e writer)
              (doseq [e (rest s)]
                (.println writer)
                (pr-rec-into-writer e writer))))
          (.print writer "]"))
        (if (list? s)
          (do
            (.print writer "(")
            (if-let [e (first s)]
              (do
                (pr-rec-into-writer e writer)
                (doseq [e (rest s)]
                  (.println writer)
                  (pr-rec-into-writer e writer))))
            (.print writer ")"))
          (if (keyword? s)
            (.print writer s)
            (if (string? s)
              (.print writer (str \" s \"))
              (if (= clojure.lang.Atom (class s))
                (pr-rec-into-writer ["ATOM" (deref s)] writer)
                (if (= clojure.lang.LazySeq (class s))
                  (pr-rec-into-writer (vec s) writer)
                  (if (number? s)
                    (.print writer s)
                    (.print writer (str "##### NOT HANDLED " (class s) " #####"))))))))))))


(defn pr-rec
  [s]
  (pr-rec-into-writer s System/out))


(defn prn-rec
  [s]
  (do
    (pr-rec-into-writer s System/out)
    (.println System/out)))


(defn pr-rec-file
  [s filename]
  (do
    (try
      (.remove (File. filename))
      (catch Exception e))
    (with-open [print-writer (PrintWriter. filename)]
      (pr-rec-into-writer s print-writer)
      (.flush print-writer))))


(defn prn-rec-file
  [s filename]
  (do
    (try
      (.remove (File. filename))
      (catch Exception e))
    (with-open [print-writer (PrintWriter. filename)]
      (pr-rec-into-writer s print-writer)
      (.println print-writer)
      (.flush print-writer))))