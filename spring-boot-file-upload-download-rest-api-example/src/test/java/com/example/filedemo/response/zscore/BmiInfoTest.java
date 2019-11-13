package com.example.filedemo.response.zscore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BmiInfoTest {

  @Test
  public void createBmiInfoTest() {
    BmiInfoResponse b = new BmiInfoResponse();
    assertNotNull(b);
  }

  @Test
  public void setBmiWeightTest() {
    BmiInfoResponse b = new BmiInfoResponse();
    b.setWeight("80");
    assertEquals("80", b.getWeight());
  }

  @Test
  public void setBmiHeightTest() {
    BmiInfoResponse b = new BmiInfoResponse();
    b.setHeight("180");
    assertEquals("180", b.getHeight());
  }

  @Test
  public void setBmiGenderTest() {
    BmiInfoResponse b = new BmiInfoResponse();
    b.setGender("Male");
    assertEquals("Male", b.getGender());
  }

  @Test
  public void setBmiDobTest() {
    BmiInfoResponse b = new BmiInfoResponse();
    b.setDob("01/01/2000");
    assertEquals("01/01/2000", b.getDob());
  }

  @Test
  public void setBmiOfficeVisitDateTest() {
    BmiInfoResponse b = new BmiInfoResponse();
    b.setOfficeVisitDate("07/07/2019");
    assertEquals("07/07/2019", b.getOfficeVisitDate());
  }

  @Test
  public void setBmiAgeInMonthsAtTimeOfVisitTest() {
    BmiInfoResponse b = new BmiInfoResponse();
    b.setAgeInMonthsAtTimeOfVisit("10");
    assertEquals("10", b.getAgeInMonthsAtTimeOfVisit());
  }

  @Test
  public void setBmiBmiTest() {
    BmiInfoResponse b = new BmiInfoResponse();
    b.setBmi("21.6");
    assertEquals("21.6", b.getBmi());
  }

  @Test
  public void setBmiZScoreTest() {
    BmiInfoResponse b = new BmiInfoResponse();
    b.setZScore("-2.06");
    assertEquals("-2.06", b.getZScore());
  }

  @Test
  public void setBmiPercentileTest() {
    BmiInfoResponse b = new BmiInfoResponse();
    b.setPercentile("42%");
    assertEquals("42%", b.getPercentile());
  }
}