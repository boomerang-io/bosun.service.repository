package net.boomerangplatform.service;

import net.boomerangplatform.model.ArtifactSummary;
import net.boomerangplatform.model.DependencyGraph;

public interface XrayRepositoryService {

	DependencyGraph getArtifactDependencygraph(String ciComponentId, String version);
	ArtifactSummary getArtifactSummary(String ciComponentId, String version);
}
