package net.boomerangplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Measure {

	@JsonProperty("metric")
	private String metric;
	
	@JsonProperty("value")
	private String value;
	
	@JsonProperty("bestValue")
	private Boolean bestValue;
	
	public Measure() {
		
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Boolean getBestValue() {
		return bestValue;
	}

	public void setBestValue(Boolean bestValue) {
		this.bestValue = bestValue;
	}
}
