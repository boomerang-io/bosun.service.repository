package net.boomerangplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

	@RequestMapping("/report")
	public SonarQubeReport getReport(
			@RequestParam(value = "ciComponentId", required = true) String ciComponentId,
			@RequestParam(value = "version", required = true) String version) {
		return repositoryService.getReport(ciComponentId, version);
	}
	
	@RequestMapping("/report/testcoverage")
	public SonarQubeReport getTestCoverageReport(
			@RequestParam(value = "ciComponentId", required = true) String ciComponentId,
			@RequestParam(value = "version", required = true) String version) {
		return repositoryService.getTestCoverageReport(ciComponentId, version);
	}
	
	@RequestMapping("/report/detail/testcoverage")
    public SonarQubeDetailReport getDetailTestCoverageReport(
            @RequestParam(value = "ciComponentId", required = true) String ciComponentId,
            @RequestParam(value = "version", required = true) String version) {
        return repositoryService.getDetailTestCoverageReport(ciComponentId, version);
    }
}
