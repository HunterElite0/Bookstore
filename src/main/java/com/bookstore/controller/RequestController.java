package com.bookstore.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bookstore.model.Book;
import com.bookstore.model.Request;

public class RequestController {
  private Connection connection;

  public RequestController(Connection connection) {
    this.connection = connection;
  }

  public Request getRequestInfo(Integer id)
  {
    try {
      String sql = "SELECT * FROM requests WHERE id = ?;";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setInt(1, id);
      ResultSet result = statement.executeQuery();
      if (result.next()) {
        Request request = new Request();
        request.setId(result.getInt("id"));
        request.setOwnerId(result.getInt("owner_id"));
        request.setBookId(result.getInt("book_id"));
        request.setUserId(result.getInt("user_id"));
        request.setStatus(result.getString("status"));
        return request;
      }
      return null;
    } catch (Exception e) {
      return null;
    }
  }

  public List<Request> listBorrowingRequests(Integer userId) {
    try {
      String sql = "SELECT * FROM requests WHERE user_id = ?;";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setInt(1, userId);
      ResultSet result = statement.executeQuery();
      List<Request> requests = new ArrayList<>();
      while (result.next()) {
        Request request = new Request();
        request.setId(result.getInt("id"));
        request.setOwnerId(result.getInt("owner_id"));
        request.setBookId(result.getInt("book_id"));
        request.setUserId(result.getInt("user_id"));
        request.setStatus(result.getString("status"));
        requests.add(request);
      }
      return requests;
    } catch (Exception e) {
      return null;
    }
  }

  public List<Request> listLendingRequests(Integer userId) {
    try {
      String sql = "SELECT * FROM requests WHERE owner_id = ?;";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setInt(1, userId);
      ResultSet result = statement.executeQuery();
      List<Request> requests = new ArrayList<>();
      while (result.next()) {
        Request request = new Request();
        request.setId(result.getInt("id"));
        request.setOwnerId(result.getInt("owner_id"));
        request.setBookId(result.getInt("book_id"));
        request.setUserId(result.getInt("user_id"));
        request.setStatus(result.getString("status"));
        requests.add(request);
      }
      return requests;
    } catch (Exception e) {
      return null;
    }
  }

  public String borrowBook(Book book, Integer userId) {
    try {
      String sql = "INSERT INTO requests (book_id, user_id, owner_id, status) VALUES (?, ?, ?, ?);";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setInt(1, book.getId());
      statement.setInt(2, userId);
      statement.setInt(3, book.getOwnerId());
      statement.setString(4, "pending");
      statement.executeUpdate();
      return "Request sent";
    } catch (Exception e) {
      return "Error sending request";
    }
  }

  public String acceptRequest(Integer requestId) {
    try {
      String sql = "UPDATE requests SET status = 'accepted' WHERE id = ?;";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setInt(1, requestId);
      statement.executeUpdate();
      return "Request accepted";
    } catch (Exception e) {
      return "Error accepting request";
    }
  }

  public String rejectRequest(Integer requestId) {
    try {
      String sql = "UPDATE requests SET status = 'rejected' WHERE id = ?;";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setInt(1, requestId);
      statement.executeUpdate();
      return "Request rejected";
    } catch (Exception e) {
      return "Error rejecting request";
    }
  }

  public Map<String, Integer> getRequestsStats() {
    try {
      String sql = "SELECT COUNT(*) AS total_requests, SUM(CASE WHEN status = 'accepted' THEN 1 ELSE 0 END) AS accepted_requests, SUM(CASE WHEN status = 'pending' THEN 1 ELSE 0 END) AS pending_requests, SUM(CASE WHEN status = 'rejected' THEN 1 ELSE 0 END) AS rejected_requests FROM requests;";
      PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
      ResultSet res = preparedStatement.executeQuery();
      Map<String, Integer> stats = new HashMap<>();
      stats.put("Total requests", res.getInt("total_requests"));
      stats.put("Accepted requests",res.getInt("accepted_requests"));
      stats.put("Pending requests", res.getInt("pending_requests"));
      stats.put("Rejected requests", res.getInt("rejected_requests"));
      return stats;
    } catch (Exception e) {
      return null;
    }
  }
}
