package net.boomerangplatform.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SonarQubeMeasuresReport {

	@JsonProperty("paging")
	private Paging Paging;
	
	@JsonProperty("measures")
	private List<Measure> measures;
	
	public SonarQubeMeasuresReport() {
		
	}

	public Paging getPaging() {
		return Paging;
	}

	public void setPaging(Paging paging) {
		Paging = paging;
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
