package net.boomerangplatform.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SonarQubeReport {

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
	private List<SonarQubeComponent> components = null;
	
	@JsonProperty("facets")
	private List<String> facets;
	
	public SonarQubeReport() {
		
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
		return issues;
	}

	public void setIssues(List<SonarQubeIssue> issues) {
		this.issues = issues;
	}

	public List<SonarQubeComponent> getComponents() {
		return components;
	}

	public void setComponents(List<SonarQubeComponent> components) {
		this.components = components;
	}

	public List<String> getFacets() {
		return facets;
	}

	public void setFacets(List<String> facets) {
		this.facets = facets;
	}
}
