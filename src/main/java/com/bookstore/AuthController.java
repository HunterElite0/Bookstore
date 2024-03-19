package com.bookstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthController {
  private Connection connection;
  public AuthController(Connection connection) {
    this.connection = connection;
  }

  public boolean login(String username, String password) {
    try {
      String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
      PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
      preparedStatement.setString(1, username);
      preparedStatement.setString(2, password);
      ResultSet res = preparedStatement.executeQuery();
      if(res.next()){
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  public String register(String username, String name, String password) {
    try {
      String sql = "INSERT INTO users (username, name, password) VALUES (?, ?, ?)";
      PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
      preparedStatement.setString(1, username);
      preparedStatement.setString(2, name);
      preparedStatement.setString(3, password);
      preparedStatement.executeUpdate();
      return "User registered successfully";
    } catch (Exception e) {
      e.printStackTrace();
      return "Error while registering user (Try another username)";
    }
  }
}
