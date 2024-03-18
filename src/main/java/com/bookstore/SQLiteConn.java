package com.bookstore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SQLiteConn {
  public static void initDb(){
    Connection connection = null;
    try {
      String url = "jdbc:sqlite:./db/bookstore.db";
      connection = DriverManager.getConnection(url);
      System.out.println("Connection to SQLite has been established.");

      String sqlBooks = "CREATE TABLE IF NOT EXISTS books ("
          + " id INTEGER PRIMARY KEY,"
          + " title TEXT NOT NULL,"
          + " author TEXT NOT NULL,"
          + " user_id INTEGER NOT NULL,"
          + " price REAL,"
          + " genre TEXT,"
          + " status TEXT,"
          + " FOREIGN KEY (user_id) REFERENCES users(id)"
          + ");";
      String sqlUsers = "CREATE TABLE IF NOT EXISTS users ("
          + " id integer PRIMARY KEY,"
          + " username text NOT NULL,"
          + " password text NOT NULL,"
          + " name text NOT NULL"
          + ");";
      String sqlRequests = "CREATE TABLE IF NOT EXISTS requests ("
          + " id integer PRIMARY KEY,"
          + " book_id integer NOT NULL,"
          + " user_id integer NOT NULL,"
          + " status text NOT NULL,"
          + " FOREIGN KEY (book_id) REFERENCES books(id),"
          + " FOREIGN KEY (user_id) REFERENCES users(id)"
          + ");";
      Statement statement = connection.createStatement();
      statement.execute(sqlUsers);
      statement.execute(sqlBooks);
      statement.execute(sqlRequests);
      System.out.println("Table created successfully.");

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (connection != null) {
          connection.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
