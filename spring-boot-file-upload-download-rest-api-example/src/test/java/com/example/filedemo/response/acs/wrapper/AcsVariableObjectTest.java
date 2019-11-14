package com.example.filedemo.response.acs.wrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AcsVariableObjectTest {

  @Test
  public void createAcsVariableTest() {
    AcsVariableObject a = new AcsVariableObject();
    assertNotNull(a);
  }

  @Test
  public void setAcsLabelTest() {
    AcsVariableObject a = new AcsVariableObject();
    a.setLabel("GINI Index");
    assertEquals("GINI Index", a.getLabel());
  }

  @Test
  public void setAcsConceptTest() {
    AcsVariableObject a = new AcsVariableObject();
    a.setConcept("Subject");
    assertEquals("Subject", a.getConcept());
  }

  @Test
  public void setAcsPredicateTypeTest() {
    AcsVariableObject a = new AcsVariableObject();
    a.setPredicateType("Pred");
    assertEquals("Pred", a.getPredicateType());
  }

  @Test
  public void setAcsGroupTest() {
    AcsVariableObject a = new AcsVariableObject();
    a.setGroup("Detailed");
    assertEquals("Detailed", a.getGroup());
  }

  @Test
  public void setAcsLimitTest() {
    AcsVariableObject a = new AcsVariableObject();
    a.setLimit(2000);
    assertEquals(2000, a.getLimit());
  }

  @Test
  public void setAcsAttributesTest() {
    AcsVariableObject a = new AcsVariableObject();
    a.setAttributes("attribute");
    assertEquals("attribute", a.getAttributes());
  }

  @Test
  public void setAcsPredicateOnlyTest() {
    AcsVariableObject a = new AcsVariableObject();
    a.setPredicateOnly("pred only");
    assertEquals("pred only", a.getPredicateOnly());
  }
  
}



