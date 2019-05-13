package net.boomerangplatform.model;

import java.util.ArrayList;
import java.util.List;

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
	
	@JsonProperty("history")
	private List<History> history;
	
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

	public List<History> getHistory() {
		if (history == null) {
			history = new ArrayList<History>();
		}
		return history;
	}

	public void setHistory(List<History> history) {
		this.history = history;
	}
}
