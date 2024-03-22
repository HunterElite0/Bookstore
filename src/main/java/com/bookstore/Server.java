package com.bookstore;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
  private ServerSocket serverSocket;

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

  public void stop(){
    if(serverSocket != null){
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
