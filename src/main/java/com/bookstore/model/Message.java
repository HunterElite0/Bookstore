package com.bookstore.model;

public class Message {
  private String message;
  private Integer senderId;
  private Integer receiverId;

  public Message() {
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Integer getSender() {
    return senderId;
  }

  public void setSender(Integer senderId) {
    this.senderId = senderId;
  }

  public Integer getReceiver() {
    return receiverId;
  }

  public void setReceiver(Integer receiverId) {
    this.receiverId = receiverId;
  }
}
