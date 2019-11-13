package com.example.filedemo.response.jewishgen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SsdiObjectTest {

  @Test
  public void createSsdiObjectTest() {
    SsdiObject s = new SsdiObject();
    assertNotNull(s);
  }

  @Test
  public void setSsdiNameTest() {
    SsdiObject s = new SsdiObject();
    s.setName("John");
    assertEquals("John", s.getName());
  }

  @Test
  public void setSsdiDateOfDeathTest() {
    SsdiObject s = new SsdiObject();
    s.setDateOfDeath("10/10/2010");
    assertEquals("10/10/2010", s.getDateOfDeath());
  }

  @Test
  public void setSsdiDateOfBirthTest() {
    SsdiObject s = new SsdiObject();
    s.setDateOfBirth("01/01/2000");
    assertEquals("01/01/2000", s.getDateOfBirth());
  }

  @Test
  public void setSsdiAgeTest() {
    SsdiObject s = new SsdiObject();
    s.setAge("15");
    assertEquals("15", s.getAge());
  }

  @Test
  public void setSsdiSocialSecurityNumberTest() {
    SsdiObject s = new SsdiObject();
    s.setSocialSecurityNumber("12345678");
    assertEquals("12345678", s.getSocialSecurityNumber());
  }

  @Test
  public void setSsdiStateIssuedTest() {
    SsdiObject s = new SsdiObject();
    s.setStateIssued("California");
    assertEquals("California", s.getStateIssued());
  }

}