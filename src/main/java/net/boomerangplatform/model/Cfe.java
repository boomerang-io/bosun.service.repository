package net.boomerangplatform.model;

import static net.boomerangplatform.util.ListUtil.sanityEmptyList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Cfe {

  private String cve;

  private List<String> cwe;

  @JsonProperty("cvss_v2")
  private String cvssV2;

  @JsonProperty("cvss_v3")
  private String cvssV3;

  public Cfe() {
    // DO nothing
  }

  public String getCve() {
    return cve;
  }

  public void setCve(String cve) {
    this.cve = cve;
  }

  public List<String> getCwe() {
    return sanityEmptyList(cwe);
  }

  public void setCwe(List<String> cwe) {
    this.cwe = sanityEmptyList(cwe);
  }

  public String getCvssV2() {
    return cvssV2;
  }

  public void setCvssV2(String cvssV2) {
    this.cvssV2 = cvssV2;
  }

  public String getCvssV3() {
    return cvssV3;
  }

  public void setCvssV3(String cvssV3) {
    this.cvssV3 = cvssV3;
  }
}
