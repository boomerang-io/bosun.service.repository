package net.boomerangplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtifactPackage extends General {

  public ArtifactPackage() {
    // Do nothing
  }
}
