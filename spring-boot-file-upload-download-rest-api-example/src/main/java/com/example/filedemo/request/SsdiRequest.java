package com.example.filedemo.request;

public class SsdiRequest {
  // E.g. https://jewishgen.org/databases/eidb/engine/ssdm.php?numberKind=starts&numberMax=&firstKind=exact&firstMax=Elvis&firstSoundex=&middleKind=starts&middleMax=&lastKind=exact&lastMax=Presley&lastSoundex=&suffixKind=exact&suffixMax=&bornyearKind=between&bornyearMin=&bornyearMax=&bornmonthKind=exact&bornmonthMax=02&borndayKind=exact&borndayMax=&diedyearKind=between&diedyearMin=&diedyearMax=&diedmonthKind=exact&diedmonthMax=&dieddayKind=exact&dieddayMax=&ageKind=between&ageMin=&ageMax=&stateKind=exact&stateMax=&offset=1&pagesize=20&nototals=0&optimize=1&testing=0&engine=jg_myisam

  // Params
  private String firstNameFilter; // firstKind
  private String firstName; // firstMax
  private String lastNameFilter; // lastKind
  private String lastName; // lastMax
  private String bornYearMin; // bornYearMin
  private String bornYearMax; // bornYearMax
  private String bornMonthMax; // bornmonthMax
  private String bornDayMax; // borndayMax
  private String diedYearMin; // diedyearMin
  private String diedYearMax; // diedyearMax
  private String diedMonthMax; // diedmonthMax
  private String diedDayMax; //dieddayMax
  private String ageMin; //ageMin
  private String ageMax; // ageMax


  public String getFirstNameFilter() {
    return this.firstNameFilter;
  }

  public void setFirstNameFilter(String firstNameFilter) {
    this.firstNameFilter = firstNameFilter;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastNameFilter() {
    return this.lastNameFilter;
  }

  public void setLastNameFilter(String lastNameFilter) {
    this.lastNameFilter = lastNameFilter;
  }

  public String getLastName() {
    return this.lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getBornYearMin() {
    return this.bornYearMin;
  }

  public void setBornYearMin(String bornYearMin) {
    this.bornYearMin = bornYearMin;
  }

  public String getBornYearMax() {
    return this.bornYearMax;
  }

  public void setBornYearMax(String bornYearMax) {
    this.bornYearMax = bornYearMax;
  }

  public String getBornMonthMax() {
    return this.bornMonthMax;
  }

  public void setBornMonthMax(String bornMonthMax) {
    this.bornMonthMax = bornMonthMax;
  }

  public String getBornDayMax() {
    return this.bornDayMax;
  }

  public void setBornDayMax(String bornDayMax) {
    this.bornDayMax = bornDayMax;
  }

  public String getDiedYearMin() {
    return this.diedYearMin;
  }

  public void setDiedYearMin(String diedYearMin) {
    this.diedYearMin = diedYearMin;
  }

  public String getDiedYearMax() {
    return this.diedYearMax;
  }

  public void setDiedYearMax(String diedYearMax) {
    this.diedYearMax = diedYearMax;
  }

  public String getDiedMonthMax() {
    return this.diedMonthMax;
  }

  public void setDiedMonthMax(String diedMonthMax) {
    this.diedMonthMax = diedMonthMax;
  }

  public String getDiedDayMax() {
    return this.diedDayMax;
  }

  public void setDiedDayMax(String diedDayMax) {
    this.diedDayMax = diedDayMax;
  }

  public String getAgeMin() {
    return this.ageMin;
  }

  public void setAgeMin(String ageMin) {
    this.ageMin = ageMin;
  }

  public String getAgeMax() {
    return this.ageMax;
  }

  public void setAgeMax(String ageMax) {
    this.ageMax = ageMax;
  }

  @Override
  public String toString() {
    return "{" +
      " firstNameFilter='" + getFirstNameFilter() + "'" +
      ", firstName='" + getFirstName() + "'" +
      ", lastNameFilter='" + getLastNameFilter() + "'" +
      ", lastName='" + getLastName() + "'" +
      ", bornYearMin='" + getBornYearMin() + "'" +
      ", bornYearMax='" + getBornYearMax() + "'" +
      ", bornMonthMax='" + getBornMonthMax() + "'" +
      ", bornDayMax='" + getBornDayMax() + "'" +
      ", diedYearMin='" + getDiedYearMin() + "'" +
      ", diedYearMax='" + getDiedYearMax() + "'" +
      ", diedMonthMax='" + getDiedMonthMax() + "'" +
      ", diedDayMax='" + getDiedDayMax() + "'" +
      ", ageMin='" + getAgeMin() + "'" +
      ", ageMax='" + getAgeMax() + "'" +
      "}";
  }
  
}