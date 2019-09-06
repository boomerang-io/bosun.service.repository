package net.boomerangplatform.model;

import static net.boomerangplatform.util.ListUtil.sanityEmptyList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DependencyGraph {

  private ArtifactPackage artifact;

  private List<Component> components;

  public DependencyGraph() {
    // Do nothing
  }

  public ArtifactPackage getArtifact() {
    return artifact;
  }

  public void setArtifact(ArtifactPackage artifact) {
    this.artifact = artifact;
  }

  public List<Component> getComponents() {
    return sanityEmptyList(components);
  }

  public void setComponents(List<Component> components) {
    this.components = sanityEmptyList(components);
  }
}
