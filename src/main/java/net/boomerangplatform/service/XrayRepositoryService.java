package net.boomerangplatform.service;

import net.boomerangplatform.model.DependencyGraph;

public interface XrayRepositoryService {

	DependencyGraph getArtifactDependencygraph(String ciComponentId, String version);
}
