package com.bookstore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ServerThread implements Runnable {

  private Socket socket;
  private BufferedReader bufferedReader;
  private BufferedWriter bufferedWriter;

  public ServerThread(Socket socket) {
    this.socket = socket;
    try {
      this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    while (socket.isConnected()) {
      try {
        String message = bufferedReader.readLine();
        System.out.println("Message from client: "+ message);
        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
      } catch (Exception e) {
        stop();
        System.out.println("Client disconnected");
        break;
      }
    }
  }

  private void stop() {
    try {
      if (socket != null) {
        this.socket.close();
      }
      if (bufferedReader != null) {
        this.bufferedReader.close();
      }
      if (bufferedWriter != null) {
        this.bufferedWriter.close();
      }
    } catch (Exception e) {
      System.out.println("Error while closing resources");
    }
  }
}
