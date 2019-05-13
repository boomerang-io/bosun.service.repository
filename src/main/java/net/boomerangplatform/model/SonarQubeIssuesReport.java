package net.boomerangplatform.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SonarQubeIssuesReport {

	@JsonProperty("total")
	private Integer total;
	
	@JsonProperty("p")
	private Integer p;
	
	@JsonProperty("ps")
	private Integer ps;
	
	@JsonProperty("paging")
	private Paging paging;
	
	@JsonProperty("effortTotal")
	private Integer effortTotal;
	
	@JsonProperty("debtTotal")
	private Integer debtTotal;
	
	@JsonProperty("issues")
	private List<SonarQubeIssue> issues = null;
	
	@JsonProperty("components")
	private List<IssueComponent> components = null;
	
	@JsonProperty("facets")
	private List<String> facets;
	
	public SonarQubeIssuesReport() {
		
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
		if (issues == null) {
			issues = new ArrayList<SonarQubeIssue>();
		}
		return issues;
	}

	public void setIssues(List<SonarQubeIssue> issues) {
		this.issues = issues;
	}

	public List<IssueComponent> getComponents() {
		if (components == null) {
			components = new ArrayList<IssueComponent>();
		}		
		return components;
	}

	public void setComponents(List<IssueComponent> components) {
		this.components = components;
	}

	public List<String> getFacets() {
		if (facets == null) {
			facets = new ArrayList<String>();
		}			
		return facets;
	}

	public void setFacets(List<String> facets) {
		this.facets = facets;
	}
}
