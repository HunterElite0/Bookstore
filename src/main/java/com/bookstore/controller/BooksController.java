package com.bookstore.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.bookstore.model.Book;

public class BooksController {

  private Connection connection;

  public BooksController(Connection connection) {
    this.connection = connection;
  }

  public List<Book> listBooks() {
    try {
      String sql = "SELECT * FROM books";
      PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
      ResultSet res = preparedStatement.executeQuery();
      List<Book> books = new ArrayList<>();
      while (res.next()) {
        Book book = new Book();
        book.setTitle(res.getString("title"));
        book.setAuthor(res.getString("author"));
        book.setGenre(res.getString("genre"));
        book.setId(res.getInt("id"));
        book.setOwnerId(res.getInt("user_id"));
        book.setStatus(res.getString("status"));
        books.add(book);
      }
      return books;
    } catch (Exception e) {
      return null;
    }
  }

  public List<Book> listBooks(String[] requestParts) {
    try {
      String title = requestParts[1];
      String author = requestParts[2];
      String genre = requestParts[3];
      String sql = "SELECT title, author, genre" +
          " FROM books" +
          " WHERE (title LIKE CONCAT('%', ?, '%') OR ? IS NULL)" +
          " AND (author LIKE CONCAT('%', ?, '%') OR ? IS NULL)" +
          " AND (genre = ? OR ? IS NULL)" + 
          " AND status = 'available';";
      PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
      preparedStatement.setString(1, title);
      preparedStatement.setString(2, author);
      preparedStatement.setString(3, genre);
      ResultSet res = preparedStatement.executeQuery();
      List<Book> books = new ArrayList<>();
      while (res.next()) {
        Book book = new Book();
        book.setTitle(res.getString("title"));
        book.setAuthor(res.getString("author"));
        book.setGenre(res.getString("genre"));
        book.setId(res.getInt("id"));
        book.setOwnerId(res.getInt("user_id"));
        book.setStatus(res.getString("status"));
        book.setPrice(res.getDouble("price"));
        books.add(book);
      }
      return books;
    } catch (Exception e) {
      return null;
    }
  }

  public String addBook(String title, String author, String genre, Integer ownerId, Double price) {
    try {
      String sql = "INSERT INTO books (title, author, genre, user_id, status, price) VALUES (?, ?, ?, ?, ?, ?)";
      PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
      preparedStatement.setString(1, title);
      preparedStatement.setString(2, author);
      preparedStatement.setString(3, genre);
      preparedStatement.setInt(4, ownerId);
      preparedStatement.setString(5, "available");
      preparedStatement.setDouble(6, price);
      preparedStatement.executeUpdate();
      return "Book added successfully";
    } catch (Exception e) {
      return "Error while adding book";
    }
  }
}
