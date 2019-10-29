package net.boomerangplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import net.boomerangplatform.model.ArtifactSummary;
import net.boomerangplatform.model.DependencyGraph;
import net.boomerangplatform.service.XrayRepositoryService;

@RequestMapping("/repository/xray")
@RestController
public class XrayRepositoryController {

	@Autowired
	private XrayRepositoryService repositoryService;

	@GetMapping("/artifact/dependencygraph")
	public DependencyGraph getArtifactDependencygraph(
			@RequestParam(value = "artifactPath", required = true) String artifactPath,
			@RequestParam(value = "artifactName", required = true) String artifactName,
			@RequestParam(value = "artifactVersion", required = true) String artifactVersion) {
		return repositoryService.getArtifactDependencygraph(artifactPath, artifactName, artifactVersion);
	}

	@GetMapping("/artifact/summary")
	public ArtifactSummary getArtifactSummary(
			@RequestParam(value = "artifactPath", required = true) String artifactPath,
			@RequestParam(value = "artifactName", required = true) String artifactName,
			@RequestParam(value = "artifactVersion", required = true) String artifactVersion) {
		return repositoryService.getArtifactSummary(artifactPath, artifactName, artifactVersion);
	}
}
