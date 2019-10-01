package com.example.filedemo.response.acs;

public class AcsStateObject {
  private String stateName;
  private String population;
  private String date;
  private String stateCode;

  public AcsStateObject(String stateName, String population, String date, String stateCode) {
    this.stateName = stateName;
    this.population = population;
    this.date = date;
    this.stateCode = stateCode;
  }

  public String getStateName() {
    return this.stateName;
  }

  public void setStateName(String stateName) {
    this.stateName = stateName;
  }

  public String getPopulation() {
    return this.population;
  }

  public void setPopulation(String population) {
    this.population = population;
  }

  public String getDate() {
    return this.date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getStateCode() {
    return this.stateCode;
  }

  public void setStateCode(String stateCode) {
    this.stateCode = stateCode;
  }

  @Override
  public String toString() {
    return "{" +
      " stateName='" + getStateName() + "'" +
      ", population='" + getPopulation() + "'" +
      ", date='" + getDate() + "'" +
      ", stateCode='" + getStateCode() + "'" +
      "}";
  }

}