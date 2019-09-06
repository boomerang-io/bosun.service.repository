package net.boomerangplatform.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.boomerangplatform.model.ArtifactoryFile;
import net.boomerangplatform.model.ArtifactoryFileListContainer;
import net.boomerangplatform.model.VersionArtifact;
import net.boomerangplatform.mongo.service.SettingsService;

@Service
public class ArtifactoryRepositoryServiceImpl implements ArtifactoryRepositoryService {

  private static final String ARTIFACTORY_URL = "artifactory.url";

  private static final Logger LOGGER = LogManager.getLogger();

  @Value("${artifactory.url.boomerang.repos}")
  private String boomerangRepoPath;

  @Autowired
  @Qualifier("requestFactoryRestTemplate")
  private RestTemplate requestFactoryRestTemplate;

  @Autowired
  private SettingsService settingsService;

  @Override
  public byte[] getArtifactBytes(String ciTeamName, String ciComponentName, String version,
      String artifact) {
    final String artifactoryBaseURL =
        this.settingsService.getConfiguration("cicd", ARTIFACTORY_URL).getValue();
    final String url = String.format("%s%s/%s/%s/%s/%s", artifactoryBaseURL, boomerangRepoPath,
        ciTeamName, ciComponentName, version, artifact);

    byte[] repositoryArtifact = "".getBytes(StandardCharsets.UTF_8);

    try {
      repositoryArtifact = invokeArtifactory(url);
    } catch (RestClientException e) {
      LOGGER.error("getArtifactBytes() - failure in call to Artifactory for artifact list", e);
      return repositoryArtifact;
    }

    return repositoryArtifact;

  }

  @Override
  public List<VersionArtifact> getArtifactList(String ciTeamName, String ciComponentName,
      String version) {

    final ArrayList<VersionArtifact> versionArtifacts = new ArrayList<>();
    final String artifactoryBaseURL =
        this.settingsService.getConfiguration("cicd", ARTIFACTORY_URL).getValue();
    final String url = String.format("%s/api/storage%s/%s/%s/%s", artifactoryBaseURL,
        boomerangRepoPath, ciTeamName, ciComponentName, version);

    byte[] artifactListJsonBytes;
    try {
      artifactListJsonBytes = invokeArtifactory(url);
    } catch (RestClientException e) {
      LOGGER.error("getArtifactList() - failure in call to Artifactory for artifact list", e);
      return versionArtifacts;
    }

    // gets byte array for file that contains a list of artifacts for the given
    // version.
    // Note: for some reason the json contained in the byte array received from
    // response.getbody() is different from the json from the file that would be
    // downloaded through the browser
    ArtifactoryFileListContainer artifactFileContainer;
    try {
      artifactFileContainer = artifactListJsonBytes != null && artifactListJsonBytes.length != 0
          ? new ObjectMapper().readValue(artifactListJsonBytes, ArtifactoryFileListContainer.class)
          : new ArtifactoryFileListContainer();
    } catch (final IOException e) {
      LOGGER.error("getArtifactList() - failure to parse Artifactory file list", e);
      return versionArtifacts;
    }

    // loop through the list of files found and extract the filename from the uris
    for (final ArtifactoryFile file : artifactFileContainer.getChildren()) {
      // removes the / from the beginning of the uri, leaving only the filename (the
      // json in the byte array has uris that have / in front of all filenames)
      final String fileName = file.getUri().substring(1);

      LOGGER.info("getArtifactList() - fileName: " + fileName);

      final VersionArtifact artifact = new VersionArtifact();
      artifact.setName(fileName);
      versionArtifacts.add(artifact);
    }

    return versionArtifacts;
  }

  @Override
  public String getArtifactVersionCreatedDate(String ciTeamName, String ciComponentName,
      String version) {

    byte[] artifactListJsonBytes = "".getBytes(StandardCharsets.UTF_8);
    final String artifactoryBaseURL =
        this.settingsService.getConfiguration("cicd", ARTIFACTORY_URL).getValue();
    final String url = String.format("%s/api/storage%s/%s/%s/%s", artifactoryBaseURL,
        boomerangRepoPath, ciTeamName, ciComponentName, version);

    String versionCreated = null;

    // gets byte array for file that contains a list of artifacts for the given
    // version.
    // Note: for some reason the json contained in the byte array received from
    // response.getbody() is different from the json from the file that would be
    // downloaded through the browser
    try {
      artifactListJsonBytes = invokeArtifactory(url);
      if (artifactListJsonBytes != null && artifactListJsonBytes.length != 0) {
        final ArtifactoryFileListContainer artifactFileContainer =
            new ObjectMapper().readValue(artifactListJsonBytes, ArtifactoryFileListContainer.class);
        versionCreated = artifactFileContainer.getCreated();
      }
    } catch (RestClientException | IOException e) {
      LOGGER.error(
          "getArtifactVersionCreatedDate() - failure in call to Artifactory for artifact list", e);
      return versionCreated;
    }

    return versionCreated;
  }

  @Override
  public void uploadFile(MultipartFile file, String fileName, String folderPath) {
    final HttpHeaders headers = new HttpHeaders();
    final String artifactoryAPIKey =
        this.settingsService.getConfiguration("cicd", "artifactory.apikey").getValue();
    headers.add("X-JFrog-Art-Api", artifactoryAPIKey);

    HttpEntity<byte[]> entity;
    try {
      entity = new HttpEntity<>(file.getBytes(), headers);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
    final String artifactoryBaseURL =
        this.settingsService.getConfiguration("cicd", ARTIFACTORY_URL).getValue();
    String serverUrl = artifactoryBaseURL + "/boomerang/" + folderPath + "/" + fileName;

    ResponseEntity<String> result =
        requestFactoryRestTemplate.exchange(serverUrl, HttpMethod.PUT, entity, String.class);

    Assert.notNull(result, "The upload file failed!");
  }

  private byte[] invokeArtifactory(String url) {
    final HttpHeaders headers = new HttpHeaders();
    final String artifactoryAPIKey =
        this.settingsService.getConfiguration("cicd", "artifactory.apikey").getValue();
    headers.add("X-JFrog-Art-Api", artifactoryAPIKey);
    headers.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

    final HttpEntity<String> request = new HttpEntity<>(headers);

    final ResponseEntity<byte[]> response = requestFactoryRestTemplate.exchange(url, HttpMethod.GET,
        request, new ParameterizedTypeReference<byte[]>() {});
    return response.getBody();
  }

}
