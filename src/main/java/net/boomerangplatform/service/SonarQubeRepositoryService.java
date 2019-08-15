package net.boomerangplatform.service;

import net.boomerangplatform.model.SonarQubeDetailReport;
import net.boomerangplatform.model.SonarQubeReport;

public interface SonarQubeRepositoryService {

  SonarQubeReport getReport(String ciComponentId, String version);

  SonarQubeReport getTestCoverageReport(String ciComponentId, String version);

  SonarQubeDetailReport getDetailTestCoverageReport(String ciComponentId, String version);
}
