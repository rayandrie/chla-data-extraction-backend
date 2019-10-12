package com.example.filedemo.response.jewishgen;

public class SsdiObject {
  private String name;
  private String dateOfDeath;
  private String dateOfBirth;
  private String age;
  private String socialSecurityNumber;
  private String stateIssued;

  public SsdiObject() {}

  public SsdiObject(String name, String dateOfDeath, String dateOfBirth, String age, String socialSecurityNumber, String stateIssued) {
    this.name = name;
    this.dateOfDeath = dateOfDeath;
    this.dateOfBirth = dateOfBirth;
    this.age = age;
    this.socialSecurityNumber = socialSecurityNumber;
    this.stateIssued = stateIssued;
  }
  
  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDateOfDeath() {
    return this.dateOfDeath;
  }

  public void setDateOfDeath(String dateOfDeath) {
    this.dateOfDeath = dateOfDeath;
  }

  public String getDateOfBirth() {
    return this.dateOfBirth;
  }

  public void setDateOfBirth(String dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public String getAge() {
    return this.age;
  }

  public void setAge(String age) {
    this.age = age;
  }

  public String getSocialSecurityNumber() {
    return this.socialSecurityNumber;
  }

  public void setSocialSecurityNumber(String socialSecurityNumber) {
    this.socialSecurityNumber = socialSecurityNumber;
  }

  public String getStateIssued() {
    return this.stateIssued;
  }

  public void setStateIssued(String stateIssued) {
    this.stateIssued = stateIssued;
  }

  @Override
  public String toString() {
    return "{" +
      " name='" + getName() + "'" +
      ", dateOfDeath='" + getDateOfDeath() + "'" +
      ", dateOfBirth='" + getDateOfBirth() + "'" +
      ", age='" + getAge() + "'" +
      ", socialSecurityNumber='" + getSocialSecurityNumber() + "'" +
      ", stateIssued='" + getStateIssued() + "'" +
      "}";
  }
}