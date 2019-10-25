package com.example.filedemo.internal;

import java.util.Map;

public class PatientInfo {
  // Initial ACS
  private String state = null;
  private String county = null;
  private String tract = null;
  
  // Initial SSDI
  private String firstName = null;
  private String middleInitial = null;
  private String lastName = null;
  
  // Initial BMI
  private String weight = null;
  private String height = null;
  private String gender = null;
  private String dateOfMeasurement = null;

  // Initial SSDI and BMI
  private String dob = null; // Will be in format MM/dd/yyyy

  // Appended ACS
  private Map<String, String> varValByVarName = null;

  // Appended SSDI
  private String vitalInformation = null;
  private String dateOfDeath = null;

  // Appended BMI
  private String bmi = null;
  private String zScore = null;
  private String percentile = null;

  public PatientInfo() {}

  public String getState() {
    return this.state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCounty() {
    return this.county;
  }

  public void setCounty(String county) {
    this.county = county;
  }

  public String getTract() {
    return this.tract;
  }

  public void setTract(String tract) {
    this.tract = tract;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getMiddleInitial() {
    return this.middleInitial;
  }

  public void setMiddleInitial(String middleInitial) {
    this.middleInitial = middleInitial;
  }

  public String getLastName() {
    return this.lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getHeight() {
    return this.height;
  }

  public void setHeight(String height) {
    this.height = height;
  }

  public String getWeight() {
    return this.weight;
  }

  public void setWeight(String weight) {
    this.weight = weight;
  }

  public String getGender() {
    return this.gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getDateOfMeasurement() {
    return this.dateOfMeasurement;
  }

  public void setDateOfMeasurement(String dateOfMeasurement) {
    this.dateOfMeasurement = dateOfMeasurement;
  }

  public String getDob() {
    return this.dob;
  }

  public void setDob(String dob) {
    this.dob = dob;
  }

  public Map<String,String> getVarValByVarName() {
    return this.varValByVarName;
  }

  public void setVarValByVarName(Map<String,String> varValByVarName) {
    this.varValByVarName = varValByVarName;
  }

  public String getVitalInformation() {
    return this.vitalInformation;
  }

  public void setVitalInformation(String vitalInformation) {
    this.vitalInformation = vitalInformation;
  }

  public String getDateOfDeath() {
    return this.dateOfDeath;
  }

  public void setDateOfDeath(String dateOfDeath) {
    this.dateOfDeath = dateOfDeath;
  }

  public String getBmi() {
    return this.bmi;
  }

  public void setBmi(String bmi) {
    this.bmi = bmi;
  }

  public String getZScore() {
    return this.zScore;
  }

  public void setZScore(String zScore) {
    this.zScore = zScore;
  }

  public String getPercentile() {
    return this.percentile;
  }

  public void setPercentile(String percentile) {
    this.percentile = percentile;
  }

  @Override
  public String toString() {
    return "{" +
      " state='" + getState() + "'" +
      ", county='" + getCounty() + "'" +
      ", tract='" + getTract() + "'" +
      ", firstName='" + getFirstName() + "'" +
      ", middleInitial='" + getMiddleInitial() + "'" +
      ", lastName='" + getLastName() + "'" +
      ", height='" + getHeight() + "'" +
      ", weight='" + getWeight() + "'" +
      ", gender='" + getGender() + "'" +
      ", dateOfMeasurement='" + getDateOfMeasurement() + "'" +
      ", dob='" + getDob() + "'" +
      ", varValByVarName='" + getVarValByVarName() + "'" +
      ", vitalInformation='" + getVitalInformation() + "'" +
      ", dateOfDeath='" + getDateOfDeath() + "'" +
      ", bmi='" + getBmi() + "'" +
      ", zScore='" + getZScore() + "'" +
      ", percentile='" + getPercentile() + "'" +
      "}";
  }

}