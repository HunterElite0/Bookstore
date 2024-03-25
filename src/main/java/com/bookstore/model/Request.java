package com.bookstore.model;

public class Request {
  private Integer id;
  private Integer bookId;
  private Integer ownerId;
  private Integer userId;
  private String status;

  public Request() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getBookId() {
    return bookId;
  }

  public void setBookId(Integer bookId) {
    this.bookId = bookId;
  }

  public Integer getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Integer ownerId) {
    this.ownerId = ownerId;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String toString() {
    return "Request [ID : " + id + "] Owner: " + ownerId + " Requester: " + userId + " Status: " + status + " Book: " + bookId;
  }

}
