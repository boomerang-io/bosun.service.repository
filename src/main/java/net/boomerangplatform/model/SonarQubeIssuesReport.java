package net.boomerangplatform.model;

import static net.boomerangplatform.util.ListUtil.sanityEmptyList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SonarQubeIssuesReport {

  private Integer total;

  private Integer p;

  private Integer ps;

  private Paging paging;

  private Integer effortTotal;

  private Integer debtTotal;

  private List<SonarQubeIssue> issues;

  private List<IssueComponent> components;

  private List<String> facets;

  public SonarQubeIssuesReport() {
    // Do nothing
  }

  public Integer getTotal() {
    return total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  public Integer getP() {
    return p;
  }

  public void setP(Integer p) {
    this.p = p;
  }

  public Integer getPs() {
    return ps;
  }

  public void setPs(Integer ps) {
    this.ps = ps;
  }

  public Paging getPaging() {
    return paging;
  }

  public void setPaging(Paging paging) {
    this.paging = paging;
  }

  public Integer getEffortTotal() {
    return effortTotal;
  }

  public void setEffortTotal(Integer effortTotal) {
    this.effortTotal = effortTotal;
  }

  public Integer getDebtTotal() {
    return debtTotal;
  }

  public void setDebtTotal(Integer debtTotal) {
    this.debtTotal = debtTotal;
  }

  public List<SonarQubeIssue> getIssues() {
    return sanityEmptyList(issues);
  }

  public void setIssues(List<SonarQubeIssue> issues) {
    this.issues = sanityEmptyList(issues);
  }

  public List<IssueComponent> getComponents() {
    return sanityEmptyList(components);
  }

  public void setComponents(List<IssueComponent> components) {
    this.components = sanityEmptyList(components);
  }

  public List<String> getFacets() {
    return sanityEmptyList(facets);
  }

  public void setFacets(List<String> facets) {
    this.facets = sanityEmptyList(facets);
  }
}
