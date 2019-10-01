package com.example.filedemo.response.acs.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AcsVariableObject {
  private String label;
  private String concept;
  private String group;
  private int limit;
  private String attributes;
  @JsonProperty(required = false)
  private String predicateType; // Note: This is null for non-variables
  @JsonProperty(required = false)
  private String predicateOnly; // Note: This is null for actual variables

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

  public String getPredicateType() {
    return this.predicateType;
  }

  public void setPredicateType(String predicateType) {
    this.predicateType = predicateType;
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

  public String getPredicateOnly() {
    return this.predicateOnly;
  }

  public void setPredicateOnly(String predicateOnly) {
    this.predicateOnly = predicateOnly;
  }


  @Override
  public String toString() {
    return "{" +
      " label='" + getLabel() + "'" +
      ", concept='" + getConcept() + "'" +
      ", predicateType='" + getPredicateType() + "'" +
      ", group='" + getGroup() + "'" +
      ", limit='" + getLimit() + "'" +
      ", attributes='" + getAttributes() + "'" +
      ", predicateOnly='" + getPredicateOnly() + "'" +
      "}";
  }
  
}