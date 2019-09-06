package net.boomerangplatform.model;

import static net.boomerangplatform.util.ListUtil.sanityEmptyList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SonarQubeProjectVersions {

  private Paging paging;

  private List<Analysis> analyses;

  public SonarQubeProjectVersions() {
    // Do nothing
  }

  public Paging getPaging() {
    return paging;
  }

  public void setPaging(Paging paging) {
    this.paging = paging;
  }

  public List<Analysis> getAnalyses() {
    return sanityEmptyList(analyses);
  }

  public void setAnalyses(List<Analysis> analyses) {
    this.analyses = sanityEmptyList(analyses);
  }
}
