package net.boomerangplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {

  private String component;

  private TextRange textRange;

  private String msg;

  public Location() {
    // Do nothing
  }

  public String getComponent() {
    return component;
  }

  public void setComponent(String component) {
    this.component = component;
  }

  public TextRange getTextRange() {
    return textRange;
  }

  public void setTextRange(TextRange textRange) {
    this.textRange = textRange;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}
