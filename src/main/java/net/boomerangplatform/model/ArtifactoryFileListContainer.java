package net.boomerangplatform.model;

import static net.boomerangplatform.util.ListUtil.sanityEmptyList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtifactoryFileListContainer {

  private List<ArtifactoryFile> children;

  private String created;

  public ArtifactoryFileListContainer() {
    // Do nothing
  }

  public List<ArtifactoryFile> getChildren() {
    return sanityEmptyList(children);
  }

  public void setChildren(List<ArtifactoryFile> children) {
    this.children = sanityEmptyList(children);
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

}
