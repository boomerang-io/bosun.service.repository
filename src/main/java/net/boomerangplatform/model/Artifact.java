package net.boomerangplatform.model;

import static net.boomerangplatform.util.ListUtil.sanityEmptyList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Artifact {

  private General general;

  private List<Issue> issues;

  private List<License> licenses;

  public Artifact() {
    // Do nothing
  }

  public General getGeneral() {
    return general;
  }

  public void setGeneral(General general) {
    this.general = general;
  }

  public List<Issue> getIssues() {
    return sanityEmptyList(issues);
  }

  public void setIssues(List<Issue> issues) {
    this.issues = sanityEmptyList(issues);
  }

  public List<License> getLicenses() {
    return sanityEmptyList(licenses);
  }

  public void setLicenses(List<License> licenses) {
    this.licenses = sanityEmptyList(licenses);
  }
}
