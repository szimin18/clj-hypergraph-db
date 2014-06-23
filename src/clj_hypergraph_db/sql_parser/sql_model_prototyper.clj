(ns clj_hypergraph_db.sql_parser.sql_model_prototyper
  (:import [org.xml.sax XMLReader]
           [java.io File]
           [com.mysql.jdbc Driver])
  (:require [clj_hypergraph_db.persistance.persistance_manager :refer :all]
            [clj_hypergraph_db.xml_parser.xml_common_functions :refer :all]))


(defn create-prototype-of-sql-configuration
  [data-file-path configuration-file-path]
  (do
    (try
      (.remove (File. configuration-file-path))
      (catch Exception e))
    (do
      (prn "done")
      ;(spit configuration-file-path @new-configuration)
      )))



;package de.vogella.mysql.first;
;
;import java.sql.Connection;
;import java.sql.DriverManager;
;import java.sql.PreparedStatement;
;import java.sql.ResultSet;
;import java.sql.SQLException;
;import java.sql.Statement;
;import java.util.Date;
;
;public class MySQLAccess {
;                           private Connection connect = null;
;                                   private Statement statement = null;
;                           private PreparedStatement preparedStatement = null;
;                                   private ResultSet resultSet = null;
;
;                           public void readDataBase() throws Exception {
;                                                                         try {
;                                                                               // this will load the MySQL driver, each DB has its own driver
;                                                                               Class.forName("com.mysql.jdbc.Driver");
;                                                                               // setup the connection with the DB.
;                                                                               connect = DriverManager
;                                                                               .getConnection("jdbc:mysql://localhost/feedback?"
;                                                                                              + "user=sqluser&password=sqluserpw");
;
;                                                                               // statements allow to issue SQL queries to the database
;                                                                               statement = connect.createStatement();
;                                                                               // resultSet gets the result of the SQL query
;                                                                               resultSet = statement
;                                                                               .executeQuery("select * from FEEDBACK.COMMENTS");
;                                                                               writeResultSet(resultSet);
;
;                                                                               // preparedStatements can use variables and are more efficient
;                                                                               preparedStatement = connect
;                                                                               .prepareStatement("insert into  FEEDBACK.COMMENTS values (default, ?, ?, ?, ? , ?, ?)");
;                                                                               // "myuser, webpage, datum, summary, COMMENTS from FEEDBACK.COMMENTS");
;// parameters start with 1
;preparedStatement.setString(1, "Test");
;preparedStatement.setString(2, "TestEmail");
;preparedStatement.setString(3, "TestWebpage");
;preparedStatement.setDate(4, new java.sql.Date(2009, 12, 11));
;preparedStatement.setString(5, "TestSummary");
;preparedStatement.setString(6, "TestComment");
;preparedStatement.executeUpdate();
;
;preparedStatement = connect
;.prepareStatement("SELECT myuser, webpage, datum, summary, COMMENTS from FEEDBACK.COMMENTS");
;resultSet = preparedStatement.executeQuery();
;writeResultSet(resultSet);
;
;// remove again the insert comment
;preparedStatement = connect
;.prepareStatement("delete from FEEDBACK.COMMENTS where myuser= ? ; ");
;preparedStatement.setString(1, "Test");
;preparedStatement.executeUpdate();
;
;resultSet = statement
;.executeQuery("select * from FEEDBACK.COMMENTS");
;writeMetaData(resultSet);
;
;} catch (Exception e) {
;                        throw e;
;                        } finally {
;                                    close();
;                                    }
;
;}
;
;private void writeMetaData(ResultSet resultSet) throws SQLException {
;                                                                      // now get some metadata from the database
;                                                                       System.out.println("The columns in the table are: ");
;                                                                       System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
;                                                                       for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
;                                                                                 System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
;                                                                       }
;}
;
;private void writeResultSet(ResultSet resultSet) throws SQLException {
;                                                                       // resultSet is initialised before the first data set
;                                                                       while (resultSet.next()) {
;                                                                                                  // it is possible to get the columns via name
;                                                                                                   // also possible to get the columns via the column number
;                                                                                                   // which starts at 1
;                                                                                                         // e.g., resultSet.getSTring(2);
;                                                                                                  String user = resultSet.getString("myuser");
;                                                                                                         String website = resultSet.getString("webpage");
;                                                                                                  String summary = resultSet.getString("summary");
;                                                                                                         Date date = resultSet.getDate("datum");
;                                                                                                  String comment = resultSet.getString("comments");
;                                                                                                         System.out.println("User: " + user);
;                                                                                                         System.out.println("Website: " + website);
;                                                                                                         System.out.println("Summary: " + summary);
;                                                                                                         System.out.println("Date: " + date);
;                                                                                                         System.out.println("Comment: " + comment);
;                                                                                                         }
;                                                                       }
;
;// you need to close all three to make sure
;private void close() {
;                       close(resultSet);
;                       close(statement);
;                       close(connect);
;                       }
;private void close(Closeable c) {
;                                  try {
;                                        if (c != null) {
;                                                         c.close();
;                                                         }
;                                           } catch (Exception e) {
;                                                                   // don't throw now as it might leave following closables in undefined state
;                                                                   }
;                                  }
;}