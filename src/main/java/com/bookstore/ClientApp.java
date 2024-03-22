package com.bookstore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientApp {

  protected Socket socket;
  protected BufferedReader bufferedReader;
  protected BufferedWriter bufferedWriter;

  public ClientApp(Socket socket) {
    this.socket = socket;
    try {
      this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    } catch (Exception e) {
      closeResources();
      System.out.println("Error starting client");
    }
  }

  private void sendMessage() {
    Scanner scanner = new Scanner(System.in);
    while (socket.isConnected() && !socket.isClosed()) {
      String message = scanner.nextLine();
      if (message.equalsIgnoreCase("quit")) {
        System.out.println("Bye!");
        closeResources();
        break;
      }
      try {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
      } catch (Exception e) {
        closeResources();
        System.out.println("Error sending request");
        break;
      }
    }
    scanner.close();
  }

  private void receiveMessage() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (socket.isConnected() && !socket.isClosed()) {
          try {
            String message = bufferedReader.readLine();
            if (message == null) {
              System.out.println("Server disconnected");
              closeResources();
              break;
            }
            System.out.println(message);
          } catch (Exception e) {
            closeResources();
            break;
          }
        }
      }
    }).start();
  }

  private void closeResources() {
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

  public static void main(String[] args) {
    try {
      Socket socket = new Socket("localhost", 8080);
      ClientApp client = new ClientApp(socket);
      client.receiveMessage();
      client.sendMessage();
    } catch (Exception e) {
      System.out.println("Client error: You have been disconnected");
    }
  }

}
