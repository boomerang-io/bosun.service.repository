package net.boomerangplatform.model;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtifactoryFileListContainer {

	@JsonProperty("children")
	private ArrayList<ArtifactoryFile> children;
	
	@JsonProperty("created")
	private String created;
	
	public ArtifactoryFileListContainer(){
		
	}

	public ArrayList<ArtifactoryFile> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<ArtifactoryFile> children) {
		this.children = children;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

}
