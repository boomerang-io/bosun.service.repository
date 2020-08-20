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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import net.boomerangplatform.model.ArtifactPackage;
import net.boomerangplatform.model.ArtifactSummary;
import net.boomerangplatform.model.CICDSettingsEntity;
import net.boomerangplatform.model.Component;
import net.boomerangplatform.model.Config;
import net.boomerangplatform.model.DependencyGraph;
import net.boomerangplatform.model.Path;
import net.boomerangplatform.model.Paths;

@Service
public class XrayRepositoryServiceImpl implements XrayRepositoryService {

  private static final Logger LOGGER = LogManager.getLogger();

  private static final Pattern ORG_PATTERN = Pattern.compile("\\s+");

  @Value("${xray.url.api.dependencygraph}")
  private String xrayDependencyGraph;

  @Value("${xray.url.api.artifactsummary}")
  private String xrayArtifactSummary;

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

  @Value("${ci.rest.url.base}")
  private String ciUrlBase;

  @Value("${ci.rest.url.get.internal.setting}")
  private String internalSettingUrl;



  @Override
  public DependencyGraph getArtifactDependencygraph(String artifactPath, String artifactName,
      String artifactVersion) {

    DependencyGraph dependencyGraph = new DependencyGraph();

    Path path = new Path();
    path.setUri(getPath(artifactVersion, artifactName, artifactPath));

    LOGGER.info("path=" + path.getUri());

    final HttpEntity<?> request = new HttpEntity<>(path, getHeaders());

    StringBuilder sb = new StringBuilder();
    String settingUrl = ciUrlBase + internalSettingUrl + "repository";
    final HttpEntity<String> settingsRequest = new HttpEntity<>(buildHeaders());

    ResponseEntity<CICDSettingsEntity> responseEntity = internalRestTemplate.exchange(settingUrl,
        HttpMethod.GET, settingsRequest, new ParameterizedTypeReference<CICDSettingsEntity>() {});
    CICDSettingsEntity settings = responseEntity.getBody();

    String xrayUrlApiBase = null;
    if (settings != null) {
      for (Config config : settings.getConfig()) {
        if (config.getKey().equals("xray.url.api.base")) {
          xrayUrlApiBase = config.getValue();
        }
      }
    }
    sb.append(xrayUrlApiBase).append(xrayDependencyGraph);

    String url = sb.toString();

    final ResponseEntity<DependencyGraph> response =
        internalRestTemplate.exchange(url, HttpMethod.POST, request, DependencyGraph.class);
    DependencyGraph rawDependencyGraph = response.getBody();

    dependencyGraph.setArtifact(getArtifactPackage(rawDependencyGraph));
    List<Component> components = dependencyGraph.getComponents();
    components.addAll(getComonentMap(rawDependencyGraph).values());
    dependencyGraph.setComponents(components);

    return dependencyGraph;
  }

  @Override
  public ArtifactSummary getArtifactSummary(String artifactPath, String artifactName,
      String artifactVersion) {

    ArtifactSummary summary = new ArtifactSummary();

    List<String> pathList = new ArrayList<>();
    pathList.add(getPath(artifactVersion, artifactName, artifactPath));

    Paths paths = new Paths();
    paths.setUris(pathList);

    LOGGER.info("path=" + paths.getUris().get(0));

    final HttpEntity<?> request = new HttpEntity<>(paths, getHeaders());

    StringBuilder sb = new StringBuilder();
    
    String xrayUrlApiBase = getXrayBaseUrl();
    sb.append(xrayUrlApiBase).append(xrayArtifactSummary);

    String url = sb.toString();

    final ResponseEntity<ArtifactSummary> response =
        internalRestTemplate.exchange(url, HttpMethod.POST, request, ArtifactSummary.class);

    summary = response.getBody();

    return summary;
  }

  protected String getXrayBaseUrl() {
    String settingUrl = ciUrlBase + internalSettingUrl + "repository";

    final HttpEntity<String> settingRequest = new HttpEntity<>(buildHeaders());

    ResponseEntity<CICDSettingsEntity> responseEntity = internalRestTemplate.exchange(settingUrl,
        HttpMethod.GET, settingRequest, new ParameterizedTypeReference<CICDSettingsEntity>() {});
    CICDSettingsEntity settings = responseEntity.getBody();

    String xrayUrlApiBase = null;
    if (settings != null) {
      for (Config config : settings.getConfig()) {
        if (config.getKey().equals("xray.url.api.base")) {
          xrayUrlApiBase = config.getValue();
        }
      }
    }
    return xrayUrlApiBase;
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

  private String getPath(String artifactVersion, String artifactName, String artifactPath) {
    String org = ORG_PATTERN.matcher(StringUtils.lowerCase(artifactPath)).replaceAll("");

    StringBuilder sb = new StringBuilder();
    sb.append(boomerangArtifactoryId).append("/").append(boomerangRepoDocker).append("/")
        .append(org).append("/").append(artifactName).append("/").append(artifactVersion);
    return sb.toString();
  }

  private HttpHeaders getHeaders() {
    
    String settingUrl = ciUrlBase + internalSettingUrl + "repository";

    final HttpEntity<String> settingRequest = new HttpEntity<>(buildHeaders());

    ResponseEntity<CICDSettingsEntity> responseEntity = internalRestTemplate.exchange(settingUrl,
        HttpMethod.GET, settingRequest, new ParameterizedTypeReference<CICDSettingsEntity>() {});
    CICDSettingsEntity settings = responseEntity.getBody();

    String xrayBoomerangUser = null;
    String xrayBoomerangApitoken = null;
    if (settings != null) {
      for (Config config : settings.getConfig()) {
        if (config.getKey().equals("xray.boomerang.user")) {
          xrayBoomerangUser = config.getValue();
        }
        
        if (config.getKey().equals("xray.boomerang.apitoken")) {
          xrayBoomerangApitoken = config.getValue();
        }
      }
    }
    
    final String plainCreds = xrayBoomerangUser + ":" + xrayBoomerangApitoken;

    final byte[] plainCredsBytes = plainCreds.getBytes(StandardCharsets.UTF_8);
    final byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
    final String base64Creds = new String(base64CredsBytes, StandardCharsets.UTF_8);

    final HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", "application/json");
    headers.add("Authorization", "Basic " + base64Creds);

    return headers;
  }
  
  private HttpHeaders buildHeaders() {
    final HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", "application/json");
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  // private Docker getDocker(CiComponentVersionEntity componentVersionEntity) {
  // boolean isDocker = false;
  // String dockerImageName = null;
  // for (CoreProperty property : componentVersionEntity.getProperties()) {
  //
  // LOGGER.info("property=" + property.getKey() + ", value=" + property.getValue());
  //
  // if ("docker.enable".equals(property.getKey())) {
  // isDocker = Boolean.valueOf(property.getValue());
  // } else if ("docker.image.name".equals(property.getKey())) {
  // dockerImageName = property.getValue();
  // }
  // }
  //
  // return new Docker(isDocker, dockerImageName);
  // }

  // static class Docker {
  // private boolean isDocker;
  // private String dockerImageName;
  //
  // public Docker(boolean isDocker, String dockerImageName) {
  // this.dockerImageName = dockerImageName;
  // this.isDocker = isDocker;
  // }
  // }
}
