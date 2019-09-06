package net.boomerangplatform.model;

import static net.boomerangplatform.util.ListUtil.sanityEmptyList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtifactSummary {

  private List<Artifact> artifacts;

  public ArtifactSummary() {
    // DO nothing
  }

  public List<Artifact> getArtifacts() {
    return sanityEmptyList(artifacts);
  }

  public void setArtifacts(List<Artifact> artifacts) {
    this.artifacts = sanityEmptyList(artifacts);
  }
}
