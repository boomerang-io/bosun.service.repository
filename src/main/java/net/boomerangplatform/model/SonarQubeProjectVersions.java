package net.boomerangplatform.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SonarQubeProjectVersions {

	@JsonProperty("paging")
	private Paging paging;
	
	@JsonProperty("analyses")
	private List<Analysis> analyses;
	
	public SonarQubeProjectVersions() {
		
	}

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}

	public List<Analysis> getAnalyses() {
		if (analyses == null) {
			analyses = new ArrayList<Analysis>();
		}
		return analyses;
	}

	public void setAnalyses(List<Analysis> analyses) {
		this.analyses = analyses;
	}
}
