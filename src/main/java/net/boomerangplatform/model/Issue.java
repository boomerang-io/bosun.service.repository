package net.boomerangplatform.model;

import static net.boomerangplatform.util.ListUtil.sanityEmptyList;
import static net.boomerangplatform.util.ListUtil.sanityNullList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue {

  private String summary;

  private String description;

  private String issueType;

  private String severity;

  private String provider;

  private List<Cfe> cves;

  private String created;

  private List<String> impactPath;

  public Issue() {
    // Do nothing
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getIssueType() {
    return issueType;
  }

  public void setIssueType(String issueType) {
    this.issueType = issueType;
  }

  public String getSeverity() {
    return severity;
  }

  public void setSeverity(String severity) {
    this.severity = severity;
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public List<Cfe> getCves() {
    return sanityNullList(cves);
  }

  public void setCves(List<Cfe> cves) {
    this.cves = sanityNullList(cves);
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public List<String> getImpactPath() {
    return sanityEmptyList(impactPath);
  }

  public void setImpactPath(List<String> impactPath) {
    this.impactPath = sanityEmptyList(impactPath);
  }
}
