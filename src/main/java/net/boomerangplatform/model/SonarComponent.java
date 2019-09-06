package net.boomerangplatform.model;

public class SonarComponent extends BaseComponent {

  private String language;
  private String path;

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

}
