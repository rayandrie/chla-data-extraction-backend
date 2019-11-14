package com.example.filedemo.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PatientInfoTest {
  
  @Test
  public void createPatientTest() {
    PatientInfo p = new PatientInfo();
    assertNotNull(p);
  }

  @Test
  public void setPatientStateTest() {
    PatientInfo p = new PatientInfo();
    p.setState("California");
    assertEquals("California", p.getState());
  }

  @Test
  public void setPatientCountyTest() {
    PatientInfo p = new PatientInfo();
    p.setCounty("Orange County");
    assertEquals("Orange County", p.getCounty());
  }

  @Test
  public void setPatientTractTest() {
    PatientInfo p = new PatientInfo();
    p.setTract("02");
    assertEquals("02", p.getTract());
  }

  @Test
  public void setPatientFirstNameTest() {
    PatientInfo p = new PatientInfo();
    p.setFirstName("John");
    assertEquals("John", p.getFirstName());
  }

  @Test
  public void setPatientMiddleInitialTest() {
    PatientInfo p = new PatientInfo();
    p.setMiddleInitial("M");
    assertEquals("M", p.getMiddleInitial());
  }

  @Test
  public void setPatientLastNameTest() {
    PatientInfo p = new PatientInfo();
    p.setLastName("Doe");
    assertEquals("Doe", p.getLastName());
  }

  @Test
  public void setPatientHeightTest() {
    PatientInfo p = new PatientInfo();
    p.setHeight("180");
    assertEquals("180", p.getHeight());
  }

  @Test
  public void setPatientWeightTest() {
    PatientInfo p = new PatientInfo();
    p.setWeight("80");
    assertEquals("80", p.getWeight());
  }

  @Test
  public void setPatientGenderTest() {
    PatientInfo p = new PatientInfo();
    p.setGender("Male");
    assertEquals("Male", p.getGender());
  }

  @Test
  public void setPatientDateOfMeasurementTest() {
    PatientInfo p = new PatientInfo();
    p.setDateOfMeasurement("01/01/2019");
    assertEquals("01/01/2019", p.getDateOfMeasurement());
  }

  @Test
  public void setPatientDobTest() {
    PatientInfo p = new PatientInfo();
    p.setDob("01/01/2010");
    assertEquals("01/01/2010", p.getDob());
  }

  @Test
  public void setPatientVarValByVarNameTest() {
    PatientInfo p = new PatientInfo();
    Map<String, String> varValByVarName = new HashMap<String, String>();
    varValByVarName.put("GINI Index", "0.5");
    p.setVarValByVarName(varValByVarName);
    assertEquals(varValByVarName, p.getVarValByVarName());
  }

  @Test
  public void setPatientVitalInformationTest() {
    PatientInfo p = new PatientInfo();
    p.setVitalInformation("Alive");
    assertEquals("Alive", p.getVitalInformation());
  }

  @Test
  public void setDateOfDeathTest() {
    PatientInfo p = new PatientInfo();
    p.setDateOfDeath("01/01/2012");
    assertEquals("01/01/2012", p.getDateOfDeath());
  }

  @Test
  public void setPatientBmiTest() {
    PatientInfo p = new PatientInfo();
    p.setBmi("21.6");
    assertEquals("21.6", p.getBmi());
  }

  @Test
  public void setPatientZScoreTest() {
    PatientInfo p = new PatientInfo();
    p.setZScore("-0.21");
    assertEquals("-0.21", p.getZScore());
  }

  @Test
  public void setPatientPercentileTest() {
    PatientInfo p = new PatientInfo();
    p.setPercentile("42%");
    assertEquals("42%", p.getPercentile());
  }

}