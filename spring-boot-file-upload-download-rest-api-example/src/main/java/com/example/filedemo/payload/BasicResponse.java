package com.example.filedemo.payload;

public class BasicResponse {
  private int statusCode;
  private String status;
  private String message;

  public BasicResponse(int statusCode, String status, String message) {
    this.statusCode = statusCode;
    this.status = status;
    this.message = message;
  }

  public int getStatusCode() {
    return this.statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
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