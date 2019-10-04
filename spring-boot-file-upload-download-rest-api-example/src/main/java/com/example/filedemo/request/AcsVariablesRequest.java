package com.example.filedemo.request;

public class AcsVariablesRequest {
  // Params
  private String[] listOfDetailedVariables;
  private String[] listOfSubjectVariables;

  public String[] getListOfDetailedVariables() {
    return this.listOfDetailedVariables;
  }

  public void setListOfDetailedVariables(String[] listOfDetailedVariables) {
    this.listOfDetailedVariables = listOfDetailedVariables;
  }

  public boolean detailedVariablesIsEmpty() {
    if (listOfDetailedVariables.length == 0) {
      return true;
    }
    return false;
  }

  public String[] getListOfSubjectVariables() {
    return this.listOfSubjectVariables;
  }

  public void setListOfSubjectVariables(String[] listOfSubjectVariables) {
    this.listOfSubjectVariables = listOfSubjectVariables;
  }

  public boolean subjectVariablesIsEmpty() {
    if (listOfSubjectVariables.length == 0) {
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return "{" +
      " listOfDetailedVariables='" + getListOfDetailedVariables() + "'" +
      ", listOfSubjectVariables='" + getListOfSubjectVariables() + "'" +
      "}";
  }

}