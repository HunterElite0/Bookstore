package com.bookstore;

import com.bookstore.connection.SQLiteConn;

public class App 
{
    public static void main( String[] args )
    {
      SQLiteConn.initDb();
      System.out.println( "Hello World!" );
    }
}
