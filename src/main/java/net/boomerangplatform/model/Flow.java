package net.boomerangplatform.model;

import static net.boomerangplatform.util.ListUtil.sanityEmptyList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Flow {

  private List<Location> locations;

  public Flow() {
    // DO nothing
  }

  public List<Location> getLocations() {
    return sanityEmptyList(locations);
  }

  public void setLocations(List<Location> locations) {
    this.locations = sanityEmptyList(locations);
  }
}
