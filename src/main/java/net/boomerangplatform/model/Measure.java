package net.boomerangplatform.model;

import static net.boomerangplatform.util.ListUtil.sanityEmptyList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Measure {

  private String metric;

  private String value;

  private Boolean bestValue;

  private List<History> history;

  public Measure() {
    // Do nothing
  }

  public String getMetric() {
    return metric;
  }

  public void setMetric(String metric) {
    this.metric = metric;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Boolean getBestValue() {
    return bestValue;
  }

  public void setBestValue(Boolean bestValue) {
    this.bestValue = bestValue;
  }

  public List<History> getHistory() {
    return sanityEmptyList(history);
  }

  public void setHistory(List<History> history) {
    this.history = sanityEmptyList(history);
  }
}
