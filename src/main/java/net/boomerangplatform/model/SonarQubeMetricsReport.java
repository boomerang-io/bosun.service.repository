package net.boomerangplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SonarQubeMetricsReport {

	@JsonProperty("component")
	private MetricComponent component;
	
	public SonarQubeMetricsReport() {
		
	}

	public MetricComponent getComponent() {
		return component;
	}

	public void setComponent(MetricComponent component) {
		this.component = component;
	}
}
