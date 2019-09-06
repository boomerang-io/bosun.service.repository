package net.boomerangplatform.model;

import static net.boomerangplatform.util.ListUtil.sanityEmptyList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Paths {

  @JsonProperty("paths")
  private List<String> uris;

  public Paths() {
    // Do nothing
  }

  public List<String> getUris() {
    return sanityEmptyList(uris);
  }

  public void setUris(List<String> paths) {
    this.uris = sanityEmptyList(paths);
  }
}
