package com.bookstore.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SQLiteConn {
  private static Connection connection = null;

  public static Connection getConnection() {
    if (connection == null) {
      try {
        String url = "jdbc:sqlite:./db/bookstore.db";
        connection = DriverManager.getConnection(url);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return connection;
  }

  public static void initDb() {
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
          + " username text NOT NULL UNIQUE,"
          + " name text NOT NULL,"
          + " password text NOT NULL"
          + ");";
      String sqlRequests = "CREATE TABLE IF NOT EXISTS requests ("
          + " id integer PRIMARY KEY,"
          + " book_id integer NOT NULL,"
          + " owner_id integer NOT NULL,"
          + " user_id integer NOT NULL,"
          + " status text NOT NULL,"
          + " FOREIGN KEY (book_id) REFERENCES books(id),"
          + " FOREIGN KEY (user_id) REFERENCES users(id),"
          + " FOREIGN KEY (owner_id) REFERENCES users(id)"
          + ");";
      String sqlMessage = "CREATE TABLE IF NOT EXISTS messages ("
          + " id integer PRIMARY KEY,"
          + " request_id integer NOT NULL,"
          + " message text NOT NULL,"
          + " FOREIGN KEY (request_id) REFERENCES requests(id)"
          + ");";
      Statement statement = connection.createStatement();
      statement.execute(sqlUsers);
      statement.execute(sqlBooks);
      statement.execute(sqlRequests);
      statement.execute(sqlMessage);
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
