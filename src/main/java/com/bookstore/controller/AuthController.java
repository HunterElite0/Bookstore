package com.bookstore.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bookstore.model.UserAccount;

public class AuthController {
  private Connection connection;

  public AuthController(Connection connection) {
    this.connection = connection;
  }

  public UserAccount login(String[] requestParts) throws Exception {
    String username = requestParts[1];
    String password = requestParts[2];
    UserAccount userAccount = new UserAccount();
    String sql = "SELECT * FROM users WHERE username = ?";
    PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
    preparedStatement.setString(1, username);
    ResultSet res = preparedStatement.executeQuery();
    if (res.next()) {
      if (!password.equals(res.getString("password"))) {
        throw new Exception("401 - Invalid password");
      }
      userAccount.setId(res.getInt("id"));
      userAccount.setName(res.getString("name"));
      userAccount.setUsername(res.getString("username"));
      userAccount.setPassword(res.getString("password"));
      return userAccount;
    } else {
      throw new Exception("404 - User not found");
    }
  }

  public String register(String[] requestParts) {
    String username = requestParts[1];
    String name = requestParts[2];
    String password = requestParts[3];
    try {
      String sql = "INSERT INTO users (username, name, password) VALUES (?, ?, ?)";
      PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
      preparedStatement.setString(1, username);
      preparedStatement.setString(2, name);
      preparedStatement.setString(3, password);
      preparedStatement.executeUpdate();
      return "User registered successfully";
    } catch (Exception e) {
      // e.printStackTrace();
      return "Error while registering user (Try another username)";
    }
  }
}
