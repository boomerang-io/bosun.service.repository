package net.boomerangplatform.model;

import java.util.ArrayList;
import java.util.List;

public class BaseComponent {

  private String key;
  private String name;
  private String qualifier;
  private List<Measure> measures;
  
  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getQualifier() {
    return qualifier;
  }
  public void setQualifier(String qualifier) {
    this.qualifier = qualifier;
  }
  public List<Measure> getMeasures() {
    if (measures == null) {
      measures = new ArrayList<Measure>();
  }
  return measures;
  }
  public void setMeasures(List<Measure> measures) {
    this.measures = measures;
  }

}
