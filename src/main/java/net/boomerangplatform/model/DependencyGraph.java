package net.boomerangplatform.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DependencyGraph {
	
	@JsonProperty("artifact")
	private Artifact artifact;
	
	@JsonProperty("components")
	private List<Component> components = null;
	
	public DependencyGraph() {
		
	}

	public Artifact getArtifact() {
		return artifact;
	}

	public void setArtifact(Artifact artifact) {
		this.artifact = artifact;
	}

	public List<Component> getComponents() {
		if (components == null) {
			components = new ArrayList<Component>();
		}
		return components;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}
}
