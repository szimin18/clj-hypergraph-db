(defproject clj_hypergraph_db "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.hypergraphdb/hgdb "1.2"]
                 [org.hypergraphdb/hgbdbje "1.2"]
                 [com.sleepycat/je "5.0.73"]
                 [com.unboundid/unboundid-ldapsdk "2.3.5" ]
                 [org.clojars.pntblnk/clj-ldap "0.0.9"]
                 [org.clojure/tools.logging "0.2.6"]
                 [mysql/mysql-connector-java "5.1.18"]
                 [orientdb.clj "0.1.0-SNAPSHOT"]]
  :repositories [["hgdb" "http://hypergraphdb.org/maven"]]
  :main clj_hypergraph_db.core)
