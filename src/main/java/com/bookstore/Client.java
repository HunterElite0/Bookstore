package com.bookstore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

  protected Socket socket;
  private String name;
  private String username;
  private String password;
  private Integer id;
  BufferedReader bufferedReader;
  BufferedWriter bufferedWriter;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Client(Socket socket) {
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
    while (this.socket.isConnected()) {
      String message = scanner.nextLine();
      if (message.equalsIgnoreCase("exit")) {
        System.out.println("Bye!");
        closeResources();
        break;
      }
      try {
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
      } catch (Exception e) { 
        System.out.println("Error sending message");
        closeResources();
        break;
      }
    }
    scanner.close();
  }

  private void receiveMessage() {
    new Thread(() -> {
      while (this.socket.isConnected()) {
        try {
          String message = bufferedReader.readLine();
          System.out.println("Message from server: " + message);
        } catch (Exception e) {
          closeResources();
          break;
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
      Client client = new Client(socket);
      client.setName("John Doe");
      client.setUsername("johndoe");
      client.setPassword("password");
      client.setId(1);
      client.receiveMessage();
      client.sendMessage();
    } catch (Exception e) {
      System.out.println("Client error: You have been disconnected");
    }
  }

}
