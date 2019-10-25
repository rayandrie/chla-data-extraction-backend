package com.example.filedemo.request;

public class ChosenVariablesRequest {
  // ACS Params
  private String[] listOfDetailedVariables;
  private String[] listOfSubjectVariables;

  // SSDI Param
  private boolean requestedSsdiInfo;

  // BMI Param
  private boolean requestedBmiInfo;

  public boolean isDetailedVariablesEmpty() {
    return listOfDetailedVariables.length == 0;
  }

  public String[] getListOfDetailedVariables() {
    return this.listOfDetailedVariables;
  }

  public void setListOfDetailedVariables(String[] listOfDetailedVariables) {
    this.listOfDetailedVariables = listOfDetailedVariables;
  }

  public boolean isSubjectVariablesEmpty() {
    return listOfSubjectVariables.length == 0;
  }

  public String[] getListOfSubjectVariables() {
    return this.listOfSubjectVariables;
  }

  public void setListOfSubjectVariables(String[] listOfSubjectVariables) {
    this.listOfSubjectVariables = listOfSubjectVariables;
  }

  public boolean isRequestedSsdiInfo() {
    return this.requestedSsdiInfo;
  }

  public boolean getRequestedSsdiInfo() {
    return this.requestedSsdiInfo;
  }

  public void setRequestedSsdiInfo(boolean requestedSsdiInfo) {
    this.requestedSsdiInfo = requestedSsdiInfo;
  }

  public boolean isRequestedBmiInfo() {
    return this.requestedBmiInfo;
  }

  public boolean getRequestedBmiInfo() {
    return this.requestedBmiInfo;
  }

  public void setRequestedBmiInfo(boolean requestedBmiInfo) {
    this.requestedBmiInfo = requestedBmiInfo;
  }

  @Override
  public String toString() {
    return "{" +
      " listOfDetailedVariables='" + getListOfDetailedVariables() + "'" +
      ", listOfSubjectVariables='" + getListOfSubjectVariables() + "'" +
      ", requestedSsdiInfo='" + isRequestedSsdiInfo() + "'" +
      ", requestedBmiInfo='" + isRequestedBmiInfo() + "'" +
      "}";
  }
} 