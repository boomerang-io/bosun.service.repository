package net.boomerangplatform.service;

import net.boomerangplatform.model.ArtifactSummary;
import net.boomerangplatform.model.DependencyGraph;

public interface XrayRepositoryService {

	DependencyGraph getArtifactDependencygraph(String artifactPath, String artifactName, String artifactVersion);

	ArtifactSummary getArtifactSummary(String artifactPath, String artifactName, String artifactVersion);
}
