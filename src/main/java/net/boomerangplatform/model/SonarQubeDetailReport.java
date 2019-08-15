package net.boomerangplatform.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SonarQubeDetailReport {

  @JsonProperty("paging")
  private Paging Paging;
  
  @JsonProperty("baseComponent")
  private BaseComponent baseComponent;
  
  @JsonProperty("components")
  private List<SonarComponent> components;

  public Paging getPaging() {
    return Paging;
  }

  public void setPaging(Paging paging) {
    Paging = paging;
  }

  public BaseComponent getBaseComponent() {
    return baseComponent;
  }

  public void setBaseComponent(BaseComponent baseComponent) {
    this.baseComponent = baseComponent;
  }

  public List<SonarComponent> getComponents() {
    return components;
  }

  public void setComponents(List<SonarComponent> components) {
    this.components = components;
  }

}
