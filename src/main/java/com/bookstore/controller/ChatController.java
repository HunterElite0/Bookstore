package com.bookstore.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ChatController {
  private Connection connection;

  public ChatController(Connection connection) {
    this.connection = connection;
  }

  public List<String> listChats(Integer userId) {
    try {
      String query = "SELECT * FROM requests WHERE (user_id = ? OR owner_id = ?) AND status = 'accepted'";
      PreparedStatement preparedStatement = connection.prepareStatement(query);
      preparedStatement.setInt(1, userId);
      preparedStatement.setInt(2, userId);
      ResultSet resultSet = preparedStatement.executeQuery();
      List<String> requests = new ArrayList<>();
      while (resultSet.next()) {
        String chat = "Chat ID: " + resultSet.getInt("id") + " with owner ID: " + resultSet.getInt("owner_id")
            + " for book ID: " + resultSet.getInt("book_id");
        requests.add(chat);
      }
      return requests;
    } catch (Exception e) {
      return null;
    }
  }

  public List<String[]> retrieveChats(Integer userId) {
    try {
      String query = "SELECT * FROM requests WHERE (user_id = ? OR owner_id = ?) AND status = 'accepted'";
      PreparedStatement preparedStatement = connection.prepareStatement(query);
      preparedStatement.setInt(1, userId);
      preparedStatement.setInt(2, userId);
      ResultSet resultSet = preparedStatement.executeQuery();
      List<String[]> requests = new ArrayList<>();
      while (resultSet.next()) {
        String[] res = new String[3];
        res[0] = resultSet.getString("id");
        res[1] = resultSet.getString("owner_id");
        res[2] = resultSet.getString("user_id");
        requests.add(res);
      }
      return requests;
    } catch (Exception e) {
      return null;
    }
  }

  public List<String> openChat(Integer requestId, Integer userId) {
    try {
      String query = "SELECT m.message, m.sender_username FROM messages m INNER JOIN requests r ON m.request_id = r.id WHERE r.id = ? AND (r.user_id = ? OR r.owner_id = ?)";
      PreparedStatement preparedStatement = connection.prepareStatement(query);
      preparedStatement.setInt(1, requestId);
      preparedStatement.setInt(2, userId);
      preparedStatement.setInt(3, userId);
      ResultSet resultSet = preparedStatement.executeQuery();
      List<String> messages = new ArrayList<>();
      while (resultSet.next()) {
        messages.add(resultSet.getString("sender_username") + ": " + resultSet.getString("message"));
      }
      return messages;
    } catch (Exception e) {
      return null;
    }
  }

  public boolean persistMessage(Integer requestId, String message, String senderUsername) {
    try {
      String query = "INSERT INTO messages (request_id, message, sender_username) VALUES (?, ?, ?)";
      PreparedStatement preparedStatement = connection.prepareStatement(query);
      preparedStatement.setInt(1, requestId);
      preparedStatement.setString(2, message);
      preparedStatement.setString(3, senderUsername);
      preparedStatement.executeUpdate();
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
