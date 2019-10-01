package com.example.filedemo.response.acs;

import com.example.filedemo.response.acs.wrapper.AcsVariableObject;

public class AcsInternalVariableObject {
  private String id;
  private String label;
  private String concept;
  private String group;
  private int limit;
  private String attributes;
  private String predicateType;

  public AcsInternalVariableObject(AcsVariableObject o, String id) {
    this.id = id;
    this.label = o.getLabel();
    this.concept = o.getConcept();
    this.group = o.getGroup();
    this.limit = o.getLimit();
    this.attributes = o.getAttributes();
    this.predicateType = o.getPredicateType();
  }


  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLabel() {
    return this.label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getConcept() {
    return this.concept;
  }

  public void setConcept(String concept) {
    this.concept = concept;
  }

  public String getGroup() {
    return this.group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public int getLimit() {
    return this.limit;
  }

  public void setLimit(int limit) {
    this.limit = limit;
  }

  public String getAttributes() {
    return this.attributes;
  }

  public void setAttributes(String attributes) {
    this.attributes = attributes;
  }

  public String getPredicateType() {
    return this.predicateType;
  }

  public void setPredicateType(String predicateType) {
    this.predicateType = predicateType;
  }

  @Override
  public String toString() {
    return "{" +
      " id='" + getId() + "'" +
      ", label='" + getLabel() + "'" +
      ", concept='" + getConcept() + "'" +
      ", group='" + getGroup() + "'" +
      ", limit='" + getLimit() + "'" +
      ", attributes='" + getAttributes() + "'" +
      ", predicateType='" + getPredicateType() + "'" +
      "}";
  }
}