package com.bookstore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bookstore.controller.AuthController;
import com.bookstore.controller.BooksController;
import com.bookstore.model.Book;
import com.bookstore.model.UserAccount;

public class ServerThread implements Runnable {

  private Socket socket;
  private BufferedReader bufferedReader;
  private BufferedWriter bufferedWriter;
  private boolean isAuthorized = false;
  Connection connection = null;
  private UserAccount userAccount;

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
    while (socket.isConnected() && !isAuthorized && !socket.isClosed()) {
      try {
        bufferedWriter.write("Welcome to the Bookstore Server");
        bufferedWriter.newLine();
        bufferedWriter.write("1. Login (type login <username> <password>)");
        bufferedWriter.newLine();
        bufferedWriter.write("2. Register (type register <username> <name> <password>)");
        bufferedWriter.newLine();
        bufferedWriter.write("3. Quit");
        bufferedWriter.newLine();
        bufferedWriter.flush();
        String request = bufferedReader.readLine();
        handleAuthRequest(request);
      } catch (Exception e) {
        System.out.println("Error while sending welcome message");
        stop();
        break;
      }
    }
    while (socket.isConnected() && isAuthorized && !socket.isClosed()) {
      try {
        // bufferedWriter.newLine();
        // bufferedWriter.flush();
        showMenu();
        String request = bufferedReader.readLine();
        handleRequest(request);
      } catch (Exception e) {
        stop();
        break;
      }
    }
    System.out.println("Client disconnected");
  }

  private void handleAuthRequest(String request) throws IOException {
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

  private void handleLogin(String[] requestParts) throws IOException {
    try {
      AuthController authController = new AuthController(connection);
      userAccount = authController.login(requestParts);
      if (userAccount != null) {
        bufferedWriter.write("Welcome " + userAccount.getName() + "!");
        bufferedWriter.newLine();
        bufferedWriter.flush();
        isAuthorized = true;
        System.out.println("User logged in: " + userAccount.getUsername());
      } else {
        bufferedWriter.write("Invalid username or password");
        bufferedWriter.newLine();
        bufferedWriter.flush();
      }
    } catch (Exception e) {
      bufferedWriter.write(e.getMessage());
      bufferedWriter.newLine();
      bufferedWriter.flush();
    }
  }

  private void handleRegister(String[] requestParts) {
    try {
      AuthController authController = new AuthController(connection);
      String result = authController.register(requestParts);
      bufferedWriter.write(result);
      bufferedWriter.newLine();
      bufferedWriter.flush();
    } catch (Exception e) {
      System.out.println("Error while registering user");
    }
  }

  private void showMenu() {
    try {
      bufferedWriter.write("1. Add book (type add)");
      bufferedWriter.newLine();
      bufferedWriter.write("2. Remove book (type remove <book_id>)");
      bufferedWriter.newLine();
      bufferedWriter.write("3. Search for a book (type search)");
      bufferedWriter.newLine();
      bufferedWriter.write("4. Submit request for book (type request <book_id>)");
      bufferedWriter.newLine();
      bufferedWriter.write("5. View requests (type requests)");
      bufferedWriter.newLine();
      bufferedWriter.write("6. Quit");
      bufferedWriter.newLine();
      bufferedWriter.flush();
    } catch (Exception e) {
      System.out.println("Error while showing menu");
    }
  }

  private void handleRequest(String request) {
    switch (request) {
      case "add":
        handleAdd();
        break;
      case "search":
        handleList();
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

  private void handleAdd() {
    try {
      bufferedWriter.write("Book title: ");
      bufferedWriter.newLine();
      bufferedWriter.flush();
      String title = bufferedReader.readLine();
      bufferedWriter.write("Book author: ");
      bufferedWriter.newLine();
      bufferedWriter.flush();
      String author = bufferedReader.readLine();
      bufferedWriter.write("Book genre: ");
      bufferedWriter.newLine();
      bufferedWriter.flush();
      String genre = bufferedReader.readLine();
      bufferedWriter.write("Book price: ");
      bufferedWriter.newLine();
      bufferedWriter.flush();
      Double price = Double.parseDouble(bufferedReader.readLine());
      BooksController booksController = new BooksController(connection);
      String result = booksController.addBook(title, author, genre, userAccount.getId(), price);
      bufferedWriter.write(result);
      bufferedWriter.newLine();
      bufferedWriter.flush();
    } catch (Exception e) {
      System.out.println("Error while adding book");
    }
  }

  private void handleList() {
    try {
      BooksController booksController = new BooksController(connection);
      bufferedWriter.write(
          "Type <title> <author> <genre> of the book you are looking for (leave fields empty to list all books): ");
      bufferedWriter.newLine();
      bufferedWriter.flush();
      String[] keys = { "title", "author", "genre" };
      String[] request = bufferedReader.readLine().split(" ");
      Map<String, String> requestParts = new HashMap<String, String>();
      for (int i = 0; i < keys.length; i++) {
        requestParts.put(keys[i], request.length > i && !request[i].isEmpty() ? request[i] : null);
      }
      List<Book> result = booksController.listBooks(requestParts);
      if (result.isEmpty()) {
        bufferedWriter.write("No books found");
        bufferedWriter.newLine();
        bufferedWriter.flush();
        return;
      }
      for (Book book : result) {
        bufferedWriter.write(book.getId() + ". ");
        bufferedWriter.write(book.toString());
        bufferedWriter.newLine();
      }
    } catch (Exception e) {
      System.out.println("Error while listing books");
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
