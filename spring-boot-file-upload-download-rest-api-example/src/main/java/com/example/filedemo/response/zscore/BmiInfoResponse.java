package com.example.filedemo.response.zscore;

public class BmiInfoResponse {
  private String weight;
  private String height;
  private String gender; // 'male' or 'female'
  private String dob; // Format - (MM/DD/YYYY)
  private String officeVisitDate; // Format - (MM/DD/YYYY)

  private String ageInMonthsAtTimeOfVisit;
  private String bmi;
  private String zScore;
  private String percentile;

  public String getWeight() {
    return this.weight;
  }

  public void setWeight(String weight) {
    this.weight = weight;
  }

  public String getHeight() {
    return this.height;
  }

  public void setHeight(String height) {
    this.height = height;
  }

  public String getGender() {
    return this.gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getDob() {
    return this.dob;
  }

  public void setDob(String dob) {
    this.dob = dob;
  }

  public String getOfficeVisitDate() {
    return this.officeVisitDate;
  }

  public void setOfficeVisitDate(String officeVisitDate) {
    this.officeVisitDate = officeVisitDate;
  }

  public String getAgeInMonthsAtTimeOfVisit() {
    return this.ageInMonthsAtTimeOfVisit;
  }

  public void setAgeInMonthsAtTimeOfVisit(String ageInMonthsAtTimeOfVisit) {
    this.ageInMonthsAtTimeOfVisit = ageInMonthsAtTimeOfVisit;
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
      " weight='" + getWeight() + "'" +
      ", height='" + getHeight() + "'" +
      ", gender='" + getGender() + "'" +
      ", dob='" + getDob() + "'" +
      ", officeVisitDate='" + getOfficeVisitDate() + "'" +
      ", ageInMonthsAtTimeOfVisit='" + getAgeInMonthsAtTimeOfVisit() + "'" +
      ", bmi='" + getBmi() + "'" +
      ", zScore='" + getZScore() + "'" +
      ", percentile='" + getPercentile() + "'" +
      "}";
  }

}