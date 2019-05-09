package net.boomerangplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.boomerangplatform.model.SonarQubeReport;
import net.boomerangplatform.service.SonarQubeRepositoryService;

@RequestMapping("/repository/sonarqube")
@RestController
public class SonarQubeRepositoryController {

	@Autowired
	private SonarQubeRepositoryService repositoryService;

	@RequestMapping("/issues/summary")
	public SonarQubeReport getIssuesSummary(
			@RequestParam(value = "ciComponentId", required = true) String ciComponentId) {
		return repositoryService.getIssuesSummary(ciComponentId);
	}
}
