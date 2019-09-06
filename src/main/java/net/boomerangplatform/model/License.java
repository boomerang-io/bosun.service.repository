package net.boomerangplatform.model;

import static net.boomerangplatform.util.ListUtil.sanityEmptyList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class License {

  private String name;

  @JsonProperty("full_name")
  private String fullName;

  private List<String> components;

  @JsonProperty("more_info_url")
  private List<String> moreInfoUrl;

  public License() {
    // Do nothing
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public List<String> getComponents() {
    return sanityEmptyList(components);
  }

  public void setComponents(List<String> components) {
    this.components = sanityEmptyList(components);
  }

  public List<String> getMoreInfoUrl() {
    return sanityEmptyList(moreInfoUrl);
  }

  public void setMoreInfoUrl(List<String> moreInfoUrl) {
    this.moreInfoUrl = sanityEmptyList(moreInfoUrl);
  }
}
