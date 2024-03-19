package com.bookstore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class ServerThread implements Runnable {

  private Socket socket;
  private BufferedReader bufferedReader;
  private BufferedWriter bufferedWriter;
  // private boolean isAuthorized = false;
  Connection connection = null;

  public ServerThread(Socket socket) {
    this.socket = socket;
    try {
      this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      String url = "jdbc:sqlite:./db/bookstore.db";
      connection = DriverManager.getConnection(url);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    while (socket.isConnected()) {
      try {
        bufferedWriter.write("Welcome to the Bookstore Server");
        bufferedWriter.newLine();
        bufferedWriter.flush();
        showMenu();
        String request = bufferedReader.readLine();
        handleRequest(request);
      } catch (Exception e) {
        stop();
        System.out.println("Client disconnected");
        break;
      }
    }
  }

  private void showMenu() {
    try {
      bufferedWriter.write("1. Login (type login <username> <password>)");
      bufferedWriter.newLine();
      bufferedWriter.write("2. Register (type register <username> <name> <password>)");
      bufferedWriter.newLine();
      bufferedWriter.write("3. Add book");
      bufferedWriter.newLine();
      bufferedWriter.write("4. List books");
      bufferedWriter.newLine();
      bufferedWriter.write("5. Buy book");
      bufferedWriter.newLine();
      bufferedWriter.write("6. Quit");
      bufferedWriter.newLine();
      bufferedWriter.flush();
    } catch (Exception e) {
      System.out.println("Error while showing menu");
    }
  }

  private void handleRequest(String request) {
    String[] requestParts = request.split(" ");
    String command = requestParts[0];
    switch (command) {
      case "login":
        // handleLogin(requestParts);
        break;
      case "register":
        handleRegister(requestParts);
        break;
      case "add":
        // handleAdd(requestParts);
        break;
      case "list":
        // handleList(requestParts);
        break;
      case "buy":
        // handleBuy(requestParts);
        break;
      case "quit":
        stop();
        break;
      default:
        break;
    }
  }

  private void handleRegister(String[] requestParts) {
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
      bufferedWriter.write("User registered successfully");
      bufferedWriter.newLine();
      bufferedWriter.flush();
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Error while registering user");
    }
  }

  private void stop() {
    try {
      if (socket != null) {
        socket.close();
      }
      if (bufferedReader != null) {
        bufferedReader.close();
      }
      if (bufferedWriter != null) {
        bufferedWriter.close();
      }
    } catch (Exception e) {
      System.out.println("Error while closing resources");
    }
  }
}
