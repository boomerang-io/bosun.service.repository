package net.boomerangplatform.model;

import static net.boomerangplatform.util.ListUtil.sanityNullList;
import java.util.List;

public class SonarQubeDetailReport {

  private Paging paging;

  private BaseComponent baseComponent;

  private List<SonarComponent> components;

  public Paging getPaging() {
    return paging;
  }

  public void setPaging(Paging paging) {
    this.paging = paging;
  }

  public BaseComponent getBaseComponent() {
    return baseComponent;
  }

  public void setBaseComponent(BaseComponent baseComponent) {
    this.baseComponent = baseComponent;
  }

  public List<SonarComponent> getComponents() {
    return sanityNullList(components);
  }

  public void setComponents(List<SonarComponent> components) {
    this.components = sanityNullList(components);
  }

}
