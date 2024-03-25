package com.bookstore;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Server {
  private ServerSocket serverSocket;
  public static Map<Integer, List<ServerThread>> rooms = new HashMap<>(); // Stores the request id and the list of clients threads
  public static Map<Integer, Set<Integer>> requests = new HashMap<>(); // Stores the request id and the list of users
  public Server(ServerSocket serverSocket) {
    this.serverSocket = serverSocket;
  }

  public void start() {
    try {
      System.out.println("Server started");
      while (true) {
        Socket socket = serverSocket.accept();
        // System.out.println("Client connected");
        ServerThread serverThread = new ServerThread(socket);
        Thread thread = new Thread(serverThread);
        thread.start();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void stop() {
    if (serverSocket != null) {
      try {
        serverSocket.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    try {
      ServerSocket serverSocket = new ServerSocket(8080);
      Server server = new Server(serverSocket);
      server.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
