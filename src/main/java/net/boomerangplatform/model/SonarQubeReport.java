package net.boomerangplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SonarQubeReport {

  private Issues issues;

  private Measures measures;

  public SonarQubeReport() {
    // Do nothing
  }

  public Issues getIssues() {
    return issues;
  }

  public void setIssues(Issues issues) {
    this.issues = issues;
  }

  public Measures getMeasures() {
    return measures;
  }

  public void setMeasures(Measures measures) {
    this.measures = measures;
  }
}
