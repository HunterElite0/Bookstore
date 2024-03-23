package com.bookstore.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bookstore.model.Book;

public class BooksController {

  private Connection connection;

  public BooksController(Connection connection) {
    this.connection = connection;
  }

  public List<Book> listBooks(List<String> request) {
    try {
      String[] keys = { "title", "author", "genre" };
      Map<String, String> requestParts = new HashMap<String, String>();
      for (int i = 0; i < keys.length; i++) {
        requestParts.put(keys[i], request.size() > i && !request.get(i).isEmpty() ? request.get(i) : null);
      }
      String sql = "SELECT * FROM books";
      for (String key : requestParts.keySet()) {
        if (requestParts.get(key) != null) {
          sql += " WHERE " + key + " LIKE " + "'%" + requestParts.get(key) + "%'";
          break;
        }
      }
      sql += ";";
      System.out.println(sql);
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

  public String removeBook(Integer bookId, Integer userId) {
    try {
      String sql = "DELETE FROM books WHERE id = ? AND user_id = ? AND status = 'available'";
      PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
      preparedStatement.setInt(1, bookId);
      preparedStatement.setInt(2, userId);
      preparedStatement.executeUpdate();
      return "Book deleted successfully";
    } catch (Exception e) {
      return "Error while deleting book";
    }
  }

  // public String borrowBook(Integer bookId, Integer userId) {
    
  // }
}
