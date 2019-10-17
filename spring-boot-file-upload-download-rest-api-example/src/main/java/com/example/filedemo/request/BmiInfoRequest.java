package com.example.filedemo.request;

public class BmiInfoRequest {
  private String weight;
  private String weightMetric; // 'kg' or 'lb'
  private String height;
  private String heightMetric; // 'cm' or 'in'
  private String gender; // 'male' or 'female'
  private String dob; // Format - (MM/DD/YYYY)
  private String officeVisitDate; // Format - (MM/DD/YYYY)

  public String getWeight() {
    return this.weight;
  }

  public void setWeight(String weight) {
    this.weight = weight;
  }

  public String getWeightMetric() {
    return this.weightMetric;
  }

  public void setWeightMetric(String weightMetric) {
    this.weightMetric = weightMetric;
  }

  public String getHeight() {
    return this.height;
  }

  public void setHeight(String height) {
    this.height = height;
  }

  public String getHeightMetric() {
    return this.heightMetric;
  }

  public void setHeightMetric(String heightMetric) {
    this.heightMetric = heightMetric;
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

  @Override
  public String toString() {
    return "{" +
      " weight='" + getWeight() + "'" +
      ", weightMetric='" + getWeightMetric() + "'" +
      ", height='" + getHeight() + "'" +
      ", heightMetric='" + getHeightMetric() + "'" +
      ", gender='" + getGender() + "'" +
      ", dob='" + getDob() + "'" +
      ", officeVisitDate='" + getOfficeVisitDate() + "'" +
      "}";
  }

}