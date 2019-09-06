package net.boomerangplatform.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
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
import net.boomerangplatform.model.ArtifactPackage;
import net.boomerangplatform.model.ArtifactSummary;
import net.boomerangplatform.model.Component;
import net.boomerangplatform.model.DependencyGraph;
import net.boomerangplatform.model.Path;
import net.boomerangplatform.model.Paths;
import net.boomerangplatform.mongo.entity.CiComponentEntity;
import net.boomerangplatform.mongo.entity.CiComponentVersionEntity;
import net.boomerangplatform.mongo.entity.CiTeamEntity;
import net.boomerangplatform.mongo.model.CoreProperty;
import net.boomerangplatform.mongo.service.CiComponentService;
import net.boomerangplatform.mongo.service.CiComponentVersionService;
import net.boomerangplatform.mongo.service.CiTeamService;

@Service
public class XrayRepositoryServiceImpl implements XrayRepositoryService {

  private static final Logger LOGGER = LogManager.getLogger();

  private static final Pattern ORG_PATTERN = Pattern.compile("\\s+");

  @Value("${xray.url.api.base}")
  private String xrayBase;

  @Value("${xray.url.api.dependencygraph}")
  private String xrayDependencyGraph;

  @Value("${xray.url.api.artifactsummary}")
  private String xrayArtifactSummary;

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

  @Autowired
  @Qualifier("internalRestTemplate")
  private RestTemplate internalRestTemplate;

  @Autowired
  private CiTeamService teamService;

  @Autowired
  private CiComponentService componentService;

  @Autowired
  private CiComponentVersionService versionService;

  @Override
  public DependencyGraph getArtifactDependencygraph(String ciComponentId, String version) {

    CiComponentVersionEntity componentVersionEntity =
        versionService.findVersionWithNameForComponentId(version, ciComponentId);

    DependencyGraph dependencyGraph = new DependencyGraph();
    if (componentVersionEntity != null) {

      Docker docker = getDocker(componentVersionEntity);

      // Currently only supports Docker
      if (docker.isDocker) {


        LOGGER.info("ciComponentId=" + componentVersionEntity.getCiComponentId()
            + ", ciComponentVersionId=" + componentVersionEntity.getId());

        CiComponentEntity componentEntity =
            componentService.findById(componentVersionEntity.getCiComponentId());

        if (componentEntity == null) {
          return new DependencyGraph();
        }

        LOGGER.info("ciComponentName=" + componentEntity.getName() + ", ciTeamId="
            + componentEntity.getCiTeamId());

        CiTeamEntity teamEntity = teamService.findById(componentEntity.getCiTeamId());

        if (teamEntity == null) {
          return new DependencyGraph();
        }

        LOGGER.info("ciTeamName=" + teamEntity.getName());

        Path path = new Path();
        path.setUri(getPath(version, docker.dockerImageName, teamEntity));

        LOGGER.info("path=" + path.getUri());

        final HttpEntity<?> request = new HttpEntity<>(path, getHeaders());

        StringBuilder sb = new StringBuilder();
        sb.append(xrayBase).append(xrayDependencyGraph);

        String url = sb.toString();

        final ResponseEntity<DependencyGraph> response =
            internalRestTemplate.exchange(url, HttpMethod.POST, request, DependencyGraph.class);
        DependencyGraph rawDependencyGraph = response.getBody();

        dependencyGraph.setArtifact(getArtifactPackage(rawDependencyGraph));
        List<Component> components = dependencyGraph.getComponents();
        components.addAll(getComonentMap(rawDependencyGraph).values());
        dependencyGraph.setComponents(components);
      }
    }
    return dependencyGraph;
  }

  @Override
  public ArtifactSummary getArtifactSummary(String ciComponentId, String version) {

    ArtifactSummary summary = new ArtifactSummary();

    CiComponentVersionEntity componentVersionEntity =
        versionService.findVersionWithNameForComponentId(version, ciComponentId);

    if (componentVersionEntity != null) {

      Docker docker = getDocker(componentVersionEntity);

      // Currently only supports Docker
      if (docker.isDocker) {

        LOGGER.info("ciComponentId=" + componentVersionEntity.getCiComponentId()
            + ", ciComponentVersionId=" + componentVersionEntity.getId());

        CiComponentEntity componentEntity =
            componentService.findById(componentVersionEntity.getCiComponentId());

        if (componentEntity == null) {
          return new ArtifactSummary();
        }

        LOGGER.info("ciComponentName=" + componentEntity.getName() + ", ciTeamId="
            + componentEntity.getCiTeamId());

        CiTeamEntity teamEntity = teamService.findById(componentEntity.getCiTeamId());

        if (teamEntity == null) {
          return new ArtifactSummary();
        }

        LOGGER.info("ciTeamName=" + teamEntity.getName());


        List<String> pathList = new ArrayList<>();
        pathList.add(getPath(version, docker.dockerImageName, teamEntity));

        Paths paths = new Paths();
        paths.setUris(pathList);

        LOGGER.info("path=" + paths.getUris().get(0));

        final HttpEntity<?> request = new HttpEntity<>(paths, getHeaders());

        StringBuilder sb = new StringBuilder();
        sb.append(xrayBase).append(xrayArtifactSummary);

        String url = sb.toString();

        final ResponseEntity<ArtifactSummary> response =
            internalRestTemplate.exchange(url, HttpMethod.POST, request, ArtifactSummary.class);

        summary = response.getBody();
      }
    }
    return summary;
  }

  private Map<String, Component> getComonentMap(DependencyGraph rawDependencyGraph) {
    Map<String, Component> componentMap = new HashMap<>();
    for (Component artifactComponent : rawDependencyGraph.getComponents()) {
      LOGGER.info("artifactComponent=" + artifactComponent.getComponentName() + ", type="
          + artifactComponent.getPackageType());

      if (artifactComponent.getComponents() != null) {
        for (Component packageComponent : artifactComponent.getComponents()) {
          LOGGER.info("packageComponent=" + packageComponent.getComponentName() + ", type="
              + packageComponent.getPackageType());

          componentMap.put(packageComponent.getComponentName(),
              getComponent(componentMap, packageComponent));
        }
      }
    }
    return componentMap;
  }

  private ArtifactPackage getArtifactPackage(DependencyGraph rawDependencyGraph) {
    ArtifactPackage artifactPackage = new ArtifactPackage();
    artifactPackage.setName(rawDependencyGraph.getArtifact().getName());
    artifactPackage.setComponentId(rawDependencyGraph.getArtifact().getComponentId());
    artifactPackage.setPath(rawDependencyGraph.getArtifact().getPath());
    artifactPackage.setPkgType(rawDependencyGraph.getArtifact().getPkgType());
    artifactPackage.setSha256(rawDependencyGraph.getArtifact().getSha256());
    return artifactPackage;
  }


  private Component getComponent(Map<String, Component> componentMap, Component packageComponent) {
    Component component = new Component();
    List<Component> components = component.getComponents();
    if (componentMap.containsKey(packageComponent.getComponentName())) {
      component = componentMap.get(packageComponent.getComponentName());

      for (Component dependentComponent : packageComponent.getComponents()) {
        if (!existingDependentComponent(component, dependentComponent.getComponentName())) {
          components.add(dependentComponent);
        }
      }
    } else {
      component.setComponentId(packageComponent.getComponentId());
      component.setComponentName(packageComponent.getComponentName());
      components.addAll(packageComponent.getComponents());
      component.setComponents(components);
      component.setCreated(packageComponent.getCreated());
      component.setPackageType(packageComponent.getPackageType());
    }
    return component;
  }


  private boolean existingDependentComponent(Component component, String dependentComponentName) {
    boolean existingDependentComponentFound = false;
    for (Component existingDependentComponent : component.getComponents()) {
      if (existingDependentComponent.getComponentName().equals(dependentComponentName)) {
        existingDependentComponentFound = true;
        break;
      }
    }
    return existingDependentComponentFound;
  }

  private String getPath(String version, String dockerImageName, CiTeamEntity teamEntity) {
    String org = ORG_PATTERN.matcher(StringUtils.lowerCase(teamEntity.getName())).replaceAll("");

    StringBuilder sb = new StringBuilder();
    sb.append(boomerangArtifactoryId).append("/").append(boomerangRepoDocker).append("/")
        .append(org).append("/").append(dockerImageName).append("/").append(version);
    return sb.toString();
  }


  private HttpHeaders getHeaders() {
    final String plainCreds = xrayBoomerangUser + ":" + xrayBoomerangApitoken;
    final byte[] plainCredsBytes = plainCreds.getBytes(StandardCharsets.UTF_8);
    final byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
    final String base64Creds = new String(base64CredsBytes, StandardCharsets.UTF_8);

    final HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", "application/json");
    headers.add("Authorization", "Basic " + base64Creds);

    return headers;
  }

  private Docker getDocker(CiComponentVersionEntity componentVersionEntity) {
    boolean isDocker = false;
    String dockerImageName = null;
    for (CoreProperty property : componentVersionEntity.getProperties()) {

      LOGGER.info("property=" + property.getKey() + ", value=" + property.getValue());

      if ("docker.enable".equals(property.getKey())) {
        isDocker = Boolean.valueOf(property.getValue());
      } else if ("docker.image.name".equals(property.getKey())) {
        dockerImageName = property.getValue();
      }
    }

    return new Docker(isDocker, dockerImageName);
  }

  static class Docker {
    private boolean isDocker;
    private String dockerImageName;

    public Docker(boolean isDocker, String dockerImageName) {
      this.dockerImageName = dockerImageName;
      this.isDocker = isDocker;
    }
  }
}
