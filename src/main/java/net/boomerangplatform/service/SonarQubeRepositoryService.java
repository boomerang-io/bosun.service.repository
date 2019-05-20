package net.boomerangplatform.service;

import net.boomerangplatform.model.SonarQubeReport;

public interface SonarQubeRepositoryService {

	SonarQubeReport getReport(String ciComponentId, String version);	
	SonarQubeReport getTestReport(String ciComponentId, String version);	
	SonarQubeReport getCoverageReport(String ciComponentId, String version);
}
