package net.boomerangplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Issues {

  private Integer total;

  private Integer blocker;

  private Integer critical;

  private Integer major;

  private Integer minor;

  private Integer info;

  private Integer filesAnalyzed;

  public Issues() {
    // Do nothing
  }

  public Integer getTotal() {
    return total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  public Integer getBlocker() {
    return blocker;
  }

  public void setBlocker(Integer blocker) {
    this.blocker = blocker;
  }

  public Integer getCritical() {
    return critical;
  }

  public void setCritical(Integer critical) {
    this.critical = critical;
  }

  public Integer getMajor() {
    return major;
  }

  public void setMajor(Integer major) {
    this.major = major;
  }

  public Integer getMinor() {
    return minor;
  }

  public void setMinor(Integer minor) {
    this.minor = minor;
  }

  public Integer getInfo() {
    return info;
  }

  public void setInfo(Integer info) {
    this.info = info;
  }

  public Integer getFilesAnalyzed() {
    return filesAnalyzed;
  }

  public void setFilesAnalyzed(Integer filesAnalyzed) {
    this.filesAnalyzed = filesAnalyzed;
  }
}
