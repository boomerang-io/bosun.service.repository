package net.boomerangplatform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.boomerangplatform.model.DependencyGraph;
import net.boomerangplatform.service.XrayRepositoryService;

@RequestMapping("/repository/xray")
@RestController
public class XrayRepositoryController {

	@Autowired
	private XrayRepositoryService repositoryService;

	@RequestMapping("/artifact/dependencygraph")
	public DependencyGraph getArtifactDependencygraph(
			@RequestParam(value = "ciComponentId", required = true) String ciComponentId,
			@RequestParam(value = "version", required = true) String version) {
		return repositoryService.getArtifactDependencygraph(ciComponentId, version);
	}
}
