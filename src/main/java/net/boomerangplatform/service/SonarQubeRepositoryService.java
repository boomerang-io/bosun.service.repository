package net.boomerangplatform.service;

import net.boomerangplatform.model.SonarQubeDetailReport;
import net.boomerangplatform.model.SonarQubeReport;

public interface SonarQubeRepositoryService {

	SonarQubeReport getReport(String id, String version);

	SonarQubeReport getTestCoverageReport(String id, String version);

	SonarQubeDetailReport getDetailTestCoverageReport(String id, String version);
}
