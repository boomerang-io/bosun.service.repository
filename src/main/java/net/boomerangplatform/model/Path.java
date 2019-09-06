package net.boomerangplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Path {

  @JsonProperty("path")
  private String uri;

  public Path() {
    // Do nothing
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String path) {
    this.uri = path;
  }
}
