package net.boomerangplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import net.boomerangplatform.model.SonarQubeDetailReport;
import net.boomerangplatform.model.SonarQubeReport;
import net.boomerangplatform.service.SonarQubeRepositoryService;

@RequestMapping("/repository/sonarqube")
@RestController
public class SonarQubeRepositoryController {

	@Autowired
	private SonarQubeRepositoryService repositoryService;

	@GetMapping("/report")
	public SonarQubeReport getReport(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "version", required = true) String version) {
		return repositoryService.getReport(id, version);
	}

	@GetMapping("/report/testcoverage")
	public SonarQubeReport getTestCoverageReport(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "version", required = true) String version) {
		return repositoryService.getTestCoverageReport(id, version);
	}

	@GetMapping("/report/detail/testcoverage")
	public SonarQubeDetailReport getDetailTestCoverageReport(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "version", required = true) String version) {
		return repositoryService.getDetailTestCoverageReport(id, version);
	}
}
