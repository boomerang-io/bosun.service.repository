package net.boomerangplatform.service;

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
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.boomerangplatform.model.ArtifactoryFile;
import net.boomerangplatform.model.ArtifactoryFileListContainer;
import net.boomerangplatform.model.VersionArtifact;

@Service
public class ArtifactoryRepositoryServiceImpl implements ArtifactoryRepositoryService {

    @Value("${artifactory.apikey}")
    private String artifactoryAPIKey;

    @Value("${artifactory.url}")
    private String artifactoryBaseURL;

    @Value("${artifactory.url.boomerang.repos}")
    private String boomerangRepoPath;

    Logger logger = LogManager.getLogger();

    @Autowired
    @Qualifier("requestFactoryRestTemplate")
    private RestTemplate requestFactoryRestTemplate;

    @Override
    public byte[] getArtifactBytes(String ciTeamName, String ciComponentName, String version, String artifact) {

        final String url = String.format("%s%s/%s/%s/%s/%s", artifactoryBaseURL, boomerangRepoPath, ciTeamName,
                ciComponentName, version, artifact);

        byte[] repositoryArtifact = "".getBytes();

        try {
            repositoryArtifact = invokeArtifactory(url);
        } catch (final Exception e) {
            logger.error("getArtifactBytes() - failure in call to Artifactory for artifact list");
            logger.error(e.getMessage(), e);
            return repositoryArtifact;
        }

        return repositoryArtifact;

    }

    @Override
    public List<VersionArtifact> getArtifactList(String ciTeamName, String ciComponentName, String version) {

        final ArrayList<VersionArtifact> versionArtifacts = new ArrayList<VersionArtifact>();

        final String url = String.format("%s/api/storage%s/%s/%s/%s", artifactoryBaseURL, boomerangRepoPath, ciTeamName,
                ciComponentName, version);

        byte[] artifactListJsonBytes;
        try {
            artifactListJsonBytes = invokeArtifactory(url);
        } catch (final Exception e) {
            logger.error("getArtifactList() - failure in call to Artifactory for artifact list");
            logger.error(e.getMessage(), e);
            return versionArtifacts;
        }

        // gets byte array for file that contains a list of artifacts for the given
        // version.
        // Note: for some reason the json contained in the byte array received from
        // response.getbody() is different from the json from the file that would be
        // downloaded through the browser
        ArtifactoryFileListContainer artifactFileContainer;
        try {
            artifactFileContainer = artifactListJsonBytes.length != 0
                    ? new ObjectMapper().readValue(artifactListJsonBytes, ArtifactoryFileListContainer.class)
                    : new ArtifactoryFileListContainer();
        } catch (final Exception e) {
            logger.error("getArtifactList() - failure to parse Artifactory file list");
            logger.error(e.getMessage(), e);
            return versionArtifacts;
        }

        // loop through the list of files found and extract the filename from the uris
        try {
            for (final ArtifactoryFile file : artifactFileContainer.getChildren()) {
                // removes the / from the beginning of the uri, leaving only the filename (the
                // json in the byte array has uris that have / in front of all filenames)
                final String fileName = file.getUri().substring(1);

                logger.info("getArtifactList() - fileName: " + fileName);

                final VersionArtifact artifact = new VersionArtifact();
                artifact.setName(fileName);
                versionArtifacts.add(artifact);
            }
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }

        return versionArtifacts;
    }

    @Override
    public String getArtifactVersionCreatedDate(String ciTeamName, String ciComponentName, String version) {

        byte[] artifactListJsonBytes = "".getBytes();

        final String url = String.format("%s/api/storage%s/%s/%s/%s", artifactoryBaseURL, boomerangRepoPath, ciTeamName,
                ciComponentName, version);

        String versionCreated = null;

        // gets byte array for file that contains a list of artifacts for the given
        // version.
        // Note: for some reason the json contained in the byte array received from
        // response.getbody() is different from the json from the file that would be
        // downloaded through the browser
        try {
            artifactListJsonBytes = invokeArtifactory(url);
            if (artifactListJsonBytes.length != 0) {
                final ObjectMapper mapper = new ObjectMapper();
                final ArtifactoryFileListContainer artifactFileContainer = mapper.readValue(artifactListJsonBytes,
                        ArtifactoryFileListContainer.class);
                versionCreated = artifactFileContainer.getCreated();
            }
        } catch (final Exception e) {
            logger.error("getArtifactVersionCreatedDate() - failure in call to Artifactory for artifact list");
            logger.error(e.getMessage(), e);
            return versionCreated;
        }

        return versionCreated;
    }

    private byte[] invokeArtifactory(String url) throws Exception {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("X-JFrog-Art-Api", artifactoryAPIKey);
        headers.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

        final HttpEntity<String> request = new HttpEntity<String>(headers);

        final ResponseEntity<byte[]> response = requestFactoryRestTemplate.exchange(url, HttpMethod.GET, request,
                new ParameterizedTypeReference<byte[]>() {
                });
        return response.getBody();
    }

}
