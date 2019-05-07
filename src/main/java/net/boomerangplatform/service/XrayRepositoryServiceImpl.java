package net.boomerangplatform.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import net.boomerangplatform.model.Artifact;
import net.boomerangplatform.model.Component;
import net.boomerangplatform.model.DependencyGraph;
import net.boomerangplatform.model.Path;
import net.boomerangplatform.mongo.entity.CiComponentEntity;
import net.boomerangplatform.mongo.entity.CiComponentVersionEntity;
import net.boomerangplatform.mongo.entity.CiTeamEntity;
import net.boomerangplatform.mongo.model.CoreProperty;
import net.boomerangplatform.mongo.service.CiComponentService;
import net.boomerangplatform.mongo.service.CiComponentVersionService;
import net.boomerangplatform.mongo.service.CiTeamService;

@Service
public class XrayRepositoryServiceImpl implements XrayRepositoryService {

	@Value("${xray.url.api.base}")
	private String xrayBase;	
	
	@Value("${xray.url.api.dependencygraph}")
	private String xrayDependencyGraph;
	
	@Value("${xray.boomerang.user}")
	private String xrayBoomerangUser;	

	@Value("${xray.boomerang.apitoken}")
	private String xrayBoomerangApitoken;
	
	@Value("${xray.boomerang.artifactory.id}")
	private String boomerangArtifactoryId;	

	@Value("${xray.boomerang.artifactory.repo.docker}")
	private String boomerangRepoDocker;
	
	@Value("${xray.boomerang.artifactory.repo.maven}")
	private String boomerangRepoMaven;
	
	@Value("${xray.boomerang.artifactory.repo.npm}")
	private String boomerangRepoNpm;

	Logger logger = LogManager.getLogger();

	@Autowired
	@Qualifier("internalRestTemplate")
	private RestTemplate internalRestTemplate;

	@Autowired
	private CiTeamService teamService;

	@Autowired
	private CiComponentService componentService;
	
	@Autowired
	private CiComponentVersionService versionService;
	
//	@Autowired
//	private SettingsService settingsService;

	@Override
	public DependencyGraph getArtifactDependencygraph(String ciComponentId, String version) {
		
		CiComponentVersionEntity componentVersionEntity = versionService.findVersionWithNameForComponentId(version, ciComponentId);
		
		if (componentVersionEntity == null) {
			return new DependencyGraph();
		}
		
		String mode = null;
		boolean isDocker = false;
		String dockerImageName = null;
		
		for (CoreProperty property : componentVersionEntity.getProperties()) {
			
			logger.info("property=" + property.getKey() + ", value=" + property.getValue());
			
			switch (property.getKey()) {
			case "mode":
				mode = property.getValue();
				break;	
			case "docker.enable":
				isDocker = Boolean.valueOf(property.getValue());
				break;
			case "docker.image.name":
				dockerImageName = property.getValue();
				break;			
			}
		}
		
		// Currently only supports Java with Docker
		if (!mode.equalsIgnoreCase("java") || !isDocker) {
			return new DependencyGraph();
		}
		
		logger.info("ciComponentId=" + componentVersionEntity.getCiComponentId() + ", ciComponentVersionId=" + componentVersionEntity.getId());
		
//		CiComponentEntity componentEntity = componentService.findById(componentVersionEntity.getCiComponentId());
		
//		Temporary workaround as cannot use componentService.findById() as it requires new isActive flag which does not yet exist in ci_components collection
		CiComponentEntity componentEntity = null;
		
		List<CiComponentEntity> componentEntityList = componentService.getAllComponentEntity();
		for (CiComponentEntity entity : componentEntityList) {
			if (entity.getId().equalsIgnoreCase(componentVersionEntity.getCiComponentId())) {
				componentEntity = entity;
				break;
			}
		}
		
		if (componentEntity == null) {
			return new DependencyGraph();
		}
		
		logger.info("ciComponentName=" + componentEntity.getName() + ", ciTeamId=" + componentEntity.getCiTeamId());
		
		CiTeamEntity teamEntity = teamService.findById(componentEntity.getCiTeamId());
		
		if (teamEntity == null) {
			return new DependencyGraph();
		}
		
		logger.info("ciTeamName=" + teamEntity.getName());
		
		String org = teamEntity.getName().toLowerCase().replaceAll("\\s+", "-");
		
		StringBuilder sb = new StringBuilder();
		sb.append(boomerangArtifactoryId).append("/").append(boomerangRepoDocker).append("/").append(org).append("/").append(dockerImageName).append("/").append(version);
		
		Path path = new Path();
		path.setPath(sb.toString());
		
		logger.info("path=" + path.getPath());
		
		final HttpEntity<?> request = new HttpEntity<>(path, getHeaders());
		
		sb = new StringBuilder();
		sb.append(xrayBase).append(xrayDependencyGraph);
		
		String url = sb.toString();

		final ResponseEntity<DependencyGraph> response = internalRestTemplate.exchange(url, HttpMethod.POST, request, DependencyGraph.class);
		DependencyGraph rawDependencyGraph = (DependencyGraph) response.getBody();
		
		Artifact artifact = new Artifact();
		artifact.setName(rawDependencyGraph.getArtifact().getName());
		artifact.setComponentId(rawDependencyGraph.getArtifact().getComponentId());
		artifact.setPath(rawDependencyGraph.getArtifact().getPath());
		artifact.setPkgType(rawDependencyGraph.getArtifact().getPkgType());
		artifact.setSha256(rawDependencyGraph.getArtifact().getSha256());		
		
		Map<String, Component> componentMap = new HashMap<String, Component>();						
		for (Component artifactComponent : rawDependencyGraph.getComponents()) {			
			logger.info("artifactComponent=" + artifactComponent.getComponentName() + ", type=" + artifactComponent.getPackageType());
			
			if (artifactComponent.getComponents() != null) {
				for (Component packageComponent : artifactComponent.getComponents()) {

					// Skip all non-maven dependencies
					if (packageComponent.getPackageType().equalsIgnoreCase("maven")) {					
						logger.info("packageComponent=" + packageComponent.getComponentName() + ", type=" + packageComponent.getPackageType());						
						
						Component component = new Component();					
						if (componentMap.containsKey(packageComponent.getComponentName())) {							
							component = componentMap.get(packageComponent.getComponentName());
							
							for (Component dependentComponent : packageComponent.getComponents()) {
								String dependentComponentName = dependentComponent.getComponentName();
								boolean existingDependentComponentFound = false;
								for (Component existingDependentComponent : component.getComponents()) {
									if (existingDependentComponent.getComponentName().equals(dependentComponentName)) {
										existingDependentComponentFound = true;
										break;
									}
								}
								if (!existingDependentComponentFound) {
									component.getComponents().add(dependentComponent);
								}
							}
						}
						else {
							component.setComponentId(packageComponent.getComponentId());;
							component.setComponentName(packageComponent.getComponentName());
							component.getComponents().addAll(packageComponent.getComponents());
							component.setCreated(packageComponent.getCreated());
							component.setPackageType(packageComponent.getPackageType());
						}
						
						componentMap.put(packageComponent.getComponentName(), component);		
					}									
				}	
			}
		}
		
		DependencyGraph dependencyGraph = new DependencyGraph();
		dependencyGraph.setArtifact(artifact);
		dependencyGraph.getComponents().addAll(componentMap.values());
		
		return dependencyGraph;
	}
	
	private HttpHeaders getHeaders() {
        final String plainCreds = xrayBoomerangUser + ":" + xrayBoomerangApitoken;
        final byte[] plainCredsBytes = plainCreds.getBytes();
        final byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        final String base64Creds = new String(base64CredsBytes);

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Authorization", "Basic " + base64Creds);
        
        return headers;
	}
}
