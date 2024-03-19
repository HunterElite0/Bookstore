package com.bookstore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;

public class ServerThread implements Runnable {

  private Socket socket;
  private BufferedReader bufferedReader;
  private BufferedWriter bufferedWriter;
  private boolean isAuthorized = false;
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
    while (socket.isConnected() && !isAuthorized) {
      try {
        bufferedWriter.write("Welcome to the Bookstore Server");
        bufferedWriter.newLine();
        bufferedWriter.write("1. Login (type login <username> <password>)");
        bufferedWriter.newLine();
        bufferedWriter.write("2. Register (type register <username> <name> <password>)");
        bufferedWriter.newLine();
        bufferedWriter.flush();
        String request = bufferedReader.readLine();
        handleAuthRequest(request);
      } catch (Exception e) {
        System.out.println("Error while sending welcome message");
      }
    }
    while (socket.isConnected() && isAuthorized) {
      try {
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
      bufferedWriter.write("1. Add book");
      bufferedWriter.newLine();
      bufferedWriter.write("2. List books");
      bufferedWriter.newLine();
      bufferedWriter.write("3. Buy book");
      bufferedWriter.newLine();
      bufferedWriter.write("4. Quit");
      bufferedWriter.newLine();
      bufferedWriter.flush();
    } catch (Exception e) {
      System.out.println("Error while showing menu");
    }
  }

  private void handleAuthRequest(String request) {
    String[] requestParts = request.split(" ");
    String command = requestParts[0];
    switch (command) {
      case "login":
        handleLogin(requestParts);
        break;
      case "register":
        handleRegister(requestParts);
        break;
      default:
        break;
    }
  }

  private void handleRequest(String request) {
    String[] requestParts = request.split(" ");
    String command = requestParts[0];
    switch (command) {
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

  private void handleLogin(String[] requestParts) {
    String username = requestParts[1];
    String password = requestParts[2];
    try {
      AuthController authController = new AuthController(connection);
      boolean result = authController.login(username, password);
      if (result) {
        bufferedWriter.write("User logged in successfully");
        bufferedWriter.newLine();
        bufferedWriter.flush();
        isAuthorized = true;
      }
      else
      {
        bufferedWriter.write("Invalid username or password");
        bufferedWriter.newLine();
        bufferedWriter.flush();
      }
    } catch (Exception e) {
      System.out.println("Error while logging in user");
    }
  }

  private void handleRegister(String[] requestParts) {
    String username = requestParts[1];
    String name = requestParts[2];
    String password = requestParts[3];
    try {
      AuthController authController = new AuthController(connection);
      String result = authController.register(username, name, password);
      bufferedWriter.write(result);
      bufferedWriter.newLine();
      bufferedWriter.flush();
    } catch (Exception e) {
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
