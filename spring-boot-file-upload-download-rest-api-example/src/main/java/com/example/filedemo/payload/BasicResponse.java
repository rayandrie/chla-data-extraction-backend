package com.example.filedemo.payload;

public class BasicResponse {
  private String status;
  private String message;

  public BasicResponse(String status, String message) {
    this.status = status;
    this.message = message;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}