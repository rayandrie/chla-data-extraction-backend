package com.example.filedemo.payload;

public class RequiredVariablesResponse {
  private int statusCode;
  private String status;
  private String message;
  private String[] requiredVariables;

  public RequiredVariablesResponse() {}

  public RequiredVariablesResponse(int statusCode, String status, String message, String[] requiredVariables) {
    this.statusCode = statusCode;
    this.status = status;
    this.message = message;
    this.requiredVariables = requiredVariables;
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

  public String[] getRequiredVariables() {
    return this.requiredVariables;
  }

  public void setRequiredVariables(String[] requiredVariables) {
    this.requiredVariables = requiredVariables;
  }

  @Override
  public String toString() {
    return "{" +
      " statusCode='" + getStatusCode() + "'" +
      ", status='" + getStatus() + "'" +
      ", message='" + getMessage() + "'" +
      ", requiredVariables='" + getRequiredVariables() + "'" +
      "}";
  }
} 