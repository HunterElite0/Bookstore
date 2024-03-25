package com.bookstore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.bookstore.connection.SQLiteConn;
import com.bookstore.controller.AuthController;
import com.bookstore.controller.BooksController;
import com.bookstore.controller.ChatController;
import com.bookstore.controller.RequestController;
import com.bookstore.model.Book;
import com.bookstore.model.Request;
import com.bookstore.model.UserAccount;

public class ServerThread implements Runnable {

  private Socket socket;
  private BufferedReader bufferedReader;
  private BufferedWriter bufferedWriter;
  private boolean isAuthorized = false;
  Connection connection = null;
  private UserAccount userAccount;
  private BooksController booksController;
  private RequestController requestController;
  private ChatController chatController;
  private List<String[]> chats;

  public ServerThread(Socket socket) {
    this.socket = socket;
    try {
      this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      connection = SQLiteConn.getConnection();
      booksController = new BooksController(connection);
      requestController = new RequestController(connection);
      chatController = new ChatController(connection);
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
        chats = chatController.retrieveChats(userAccount.getId());
        for (String[] chat : chats) {
          Server.rooms.put(Integer.parseInt(chat[0]), new ArrayList<>());
          Server.requests.put(Integer.parseInt(chat[0]), new HashSet<>());
          Server.requests.get(Integer.parseInt(chat[0])).add(Integer.parseInt(chat[1]));
          Server.requests.get(Integer.parseInt(chat[0])).add(Integer.parseInt(chat[2]));
        }
        showMenu();
        String request = bufferedReader.readLine();
        handleRequest(request);
      } catch (Exception e) {
        System.out.println("Error processing request");
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
      bufferedWriter.write("2. Remove book (type remove)");
      bufferedWriter.newLine();
      bufferedWriter.write("3. Search for a book (type search)");
      bufferedWriter.newLine();
      bufferedWriter.write("4. Submit request for book (type borrow)");
      bufferedWriter.newLine();
      bufferedWriter.write("5. View borrowing requests (type borrowing)");
      bufferedWriter.newLine();
      bufferedWriter.write("6. View lending requests (type lending)");
      bufferedWriter.newLine();
      bufferedWriter.write("7. Accept or reject lending requests (type manage)");
      bufferedWriter.newLine();
      bufferedWriter.write("8. View chats (type chats)");
      bufferedWriter.newLine();
      bufferedWriter.write("9. Open chat (type chat)");
      bufferedWriter.newLine();
      bufferedWriter.write("10. Quit (type quit)");
      bufferedWriter.newLine();
      bufferedWriter.flush();
    } catch (Exception e) {
      System.out.println("Error while showing menu");
    }
  }

  private void handleRequest(String request) throws IOException {
    switch (request) {
      case "add":
        handleAdd();
        break;
      case "search":
        handleList();
        break;
      case "remove":
        handleRemove();
        break;
      case "borrow":
        handleBorrow();
        break;
      case "borrowing":
        handleListBorrowing();
        break;
      case "lending":
        handleListLending();
        break;
      case "manage":
        handleManage();
        break;
      case "chats":
        handleViewChats();
        break;
      case "chat":
        handleOpenChat();
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
      String genre = bufferedReader.readLine().toUpperCase();
      bufferedWriter.write("Book price: ");
      bufferedWriter.newLine();
      bufferedWriter.flush();
      Double price = Double.parseDouble(bufferedReader.readLine());
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
      List<String> request = new ArrayList<>();
      bufferedWriter.write("Book title (optional): ");
      bufferedWriter.newLine();
      bufferedWriter.flush();
      request.add(bufferedReader.readLine());
      bufferedWriter.write("Book author (optional): ");
      bufferedWriter.newLine();
      bufferedWriter.flush();
      request.add(bufferedReader.readLine());
      bufferedWriter.write("Book genre (optional): ");
      bufferedWriter.newLine();
      bufferedWriter.flush();
      request.add(bufferedReader.readLine().toUpperCase());
      List<Book> result = booksController.listBooks(request);
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

  private void handleRemove() throws IOException {
    try {
      bufferedWriter.write("Type the id of the book you want to remove: ");
      bufferedWriter.newLine();
      bufferedWriter.flush();
      Integer id = Integer.parseInt(bufferedReader.readLine());
      String result = booksController.removeBook(id, userAccount.getId());
      bufferedWriter.write(result);
      bufferedWriter.newLine();
      bufferedWriter.flush();
    } catch (Exception e) {
      bufferedWriter.write(e.getMessage());
      bufferedWriter.newLine();
      bufferedWriter.flush();
    }
  }

  private void handleBorrow() throws IOException {
    try {
      bufferedWriter.write("Type the id of the book you want to request: ");
      bufferedWriter.newLine();
      bufferedWriter.flush();
      Integer id = Integer.parseInt(bufferedReader.readLine());
      Book book = booksController.getBookInfo(id);
      String result = requestController.borrowBook(book, userAccount.getId());
      bufferedWriter.write(result);
      bufferedWriter.newLine();
      bufferedWriter.flush();
    } catch (Exception e) {
      bufferedWriter.write(e.getMessage());
      bufferedWriter.newLine();
      bufferedWriter.flush();
    }
  }

  private void handleListBorrowing() {
    try {
      List<Request> result = requestController.listBorrowingRequests(userAccount.getId());
      if (result.isEmpty()) {
        bufferedWriter.write("No borrowing requests found");
        bufferedWriter.newLine();
        bufferedWriter.flush();
        return;
      }
      for (Request request : result) {
        bufferedWriter.write(request.toString());
        bufferedWriter.newLine();
      }
    } catch (Exception e) {
      System.out.println("Error while listing borrowing requests");
    }
  }

  private void handleListLending() {
    try {
      List<Request> result = requestController.listLendingRequests(userAccount.getId());
      if (result.isEmpty()) {
        bufferedWriter.write("No lending requests found");
        bufferedWriter.newLine();
        bufferedWriter.flush();
        return;
      }
      for (Request request : result) {
        bufferedWriter.write(request.toString());
        bufferedWriter.newLine();
      }
    } catch (Exception e) {
      System.out.println("Error while listing lending requests");
    }
  }

  private void handleManage() throws IOException {
    try {
      bufferedWriter.write("Type the id of the lending request you want to manage: ");
      bufferedWriter.newLine();
      bufferedWriter.flush();
      Integer id = Integer.parseInt(bufferedReader.readLine());
      bufferedWriter.write("Type 'accept' to accept the request or 'reject' to reject it: ");
      bufferedWriter.newLine();
      bufferedWriter.flush();
      String action = bufferedReader.readLine();
      String result = action.equals("accept") ? requestController.acceptRequest(id)
          : requestController.rejectRequest(id);
      bufferedWriter.write(result);
      bufferedWriter.newLine();
      bufferedWriter.flush();
      if (result.equals("Request accepted")) {
        booksController.updateStatus(id, "borrowed");
      }
    } catch (Exception e) {
      bufferedWriter.write(e.getMessage());
      bufferedWriter.newLine();
      bufferedWriter.flush();
    }
  }

  private void handleViewChats() {
    try {
      bufferedWriter.write("Chats:");
      bufferedWriter.newLine();
      bufferedWriter.flush();
      List<String> result = chatController.listChats(userAccount.getId());
      if (result.isEmpty() || result == null) {
        bufferedWriter.write("You have no chats");
        bufferedWriter.newLine();
        bufferedWriter.flush();
        return;
      }
      for (String chat : result) {
        bufferedWriter.write(chat);
        bufferedWriter.newLine();
      }
      bufferedWriter.flush();
    } catch (Exception e) {
      System.out.println("Error while viewing chats");
    }
  }

  private void handleOpenChat() {
    try {
      bufferedWriter.write("Type the id of the chat you want to open: ");
      bufferedWriter.newLine();
      bufferedWriter.flush();
      Integer id = Integer.parseInt(bufferedReader.readLine());
      if (Server.requests.get(id) == null || !Server.requests.get(id).contains(userAccount.getId())) {
        bufferedWriter.write("You are not authorized to open this chat");
        bufferedWriter.newLine();
        bufferedWriter.flush();
        return;
      }
      List<String> result = chatController.openChat(id, userAccount.getId());
      for (String message : result) {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
      }
      Server.rooms.get(id).add(this);
      bufferedWriter.write("Type '/quit' to exit the chat");
      bufferedWriter.newLine();
      bufferedWriter.flush();
      while (isAuthorized && !socket.isClosed()) {
        String message = bufferedReader.readLine();
        if (message.equals("/quit")) {
          Server.rooms.get(id).remove(this);
          break;
        }
        chatController.persistMessage(id, message);
        for (ServerThread client : Server.rooms.get(id)) {
          client.bufferedWriter.write(userAccount.getName() + ": " + message);
          client.bufferedWriter.newLine();
          client.bufferedWriter.flush();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Error while opening chat");
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
