package com.bookstore.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.bookstore.model.Request;

public class RequestController {
  private Connection connection;

  public RequestController(Connection connection) {
    this.connection = connection;
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
}
