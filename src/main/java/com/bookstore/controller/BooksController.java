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

  public Book getBookInfo(Integer bookId) {
    try {
      String sql = "SELECT * FROM books WHERE id = ?;";
      PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
      preparedStatement.setInt(1, bookId);
      ResultSet res = preparedStatement.executeQuery();
      Book book = new Book();
      while (res.next()) {
        book.setTitle(res.getString("title"));
        book.setAuthor(res.getString("author"));
        book.setGenre(res.getString("genre"));
        book.setId(res.getInt("id"));
        book.setOwnerId(res.getInt("user_id"));
        book.setStatus(res.getString("status"));
        book.setPrice(res.getDouble("price"));
      }
      return book;
    } catch (Exception e) {
      return null;
    }
  }

  public List<Book> listBooks(List<String> request) {
    try {
      String[] keys = { "title", "author", "genre" };
      Map<String, String> requestParts = new HashMap<String, String>();
      for (int i = 0; i < keys.length; i++) {
        requestParts.put(keys[i], request.size() > i && !request.get(i).isEmpty() ? request.get(i) : null);
      }
      String sql = "SELECT * FROM books WHERE ";
      for (String key : requestParts.keySet()) {
        if (requestParts.get(key) != null) {
          sql += key + " LIKE " + "'%" + requestParts.get(key) + "%'";
          sql += " AND ";
        }
      }
      sql = sql.substring(0, sql.length() - 5);
      sql += ";";
      // System.out.println(sql);
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

  public String updateStatus(Integer bookId, String status) {
    try {
      String sql = "UPDATE books SET status = ? WHERE id = ?";
      PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
      preparedStatement.setString(1, status);
      preparedStatement.setInt(2, bookId);
      preparedStatement.executeUpdate();
      return "Status updated successfully";
    } catch (Exception e) {
      return "Error while updating status";
    }
  }

  public Map<String, Integer> getBooksStats() {
    try {
      String sql = "SELECT COUNT(*) AS total_books, SUM(CASE WHEN status = 'available' THEN 1 ELSE 0 END) AS available_books, SUM(CASE WHEN status = 'pending' THEN 1 ELSE 0 END) AS pending_books, SUM(CASE WHEN status = 'borrowed' THEN 1 ELSE 0 END) AS borrowed_books FROM books;";
      PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
      ResultSet res = preparedStatement.executeQuery();
      Map<String, Integer> stats = new HashMap<>();
      stats.put("Total books", res.getInt("total_books"));
      stats.put("Available books", res.getInt("available_books"));
      stats.put("Pending books", res.getInt("pending_books"));
      stats.put("Borrowed books", res.getInt("borrowed_books"));
      return stats;
    } catch (Exception e) {
      return null;
    }
  }
}
