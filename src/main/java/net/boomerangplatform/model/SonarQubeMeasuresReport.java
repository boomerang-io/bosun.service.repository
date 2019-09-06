package net.boomerangplatform.model;

import static net.boomerangplatform.util.ListUtil.sanityEmptyList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SonarQubeMeasuresReport {

  private Paging paging;

  private List<Measure> measures;

  public SonarQubeMeasuresReport() {
    // Do nothing
  }

  public Paging getPaging() {
    return paging;
  }

  public void setPaging(Paging paging) {
    this.paging = paging;
  }

  public List<Measure> getMeasures() {
    return sanityEmptyList(measures);
  }

  public void setMeasures(List<Measure> measures) {
    this.measures = sanityEmptyList(measures);
  }
}
