package net.boomerangplatform.service;

import net.boomerangplatform.model.SonarQubeReport;

public interface SonarQubeRepositoryService {

	SonarQubeReport getIssuesSummary(String ciComponentId);
}
