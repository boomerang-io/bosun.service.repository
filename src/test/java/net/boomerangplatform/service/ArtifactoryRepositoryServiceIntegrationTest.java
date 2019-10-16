package net.boomerangplatform.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import net.boomerangplatform.AbstractBoomerangTest;
import net.boomerangplatform.Application;
import net.boomerangplatform.MongoConfig;
import net.boomerangplatform.model.VersionArtifact;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class, MongoConfig.class})
@SpringBootTest
public class ArtifactoryRepositoryServiceIntegrationTest extends AbstractBoomerangTest {

  protected MockRestServiceServer mockServer;

  @Autowired
  private ArtifactoryRepositoryService artifactoryRepositoryService;

  @Autowired
  @Qualifier("requestFactoryRestTemplate")
  protected RestTemplate restTemplate;


  @Override
  protected String[] getCollections() {
    return new String[] {"core_settings"};
  }

  @Override
  protected Map<String, List<String>> getData() {
    LinkedHashMap<String, List<String>> data = new LinkedHashMap<>();
    data.put("core_settings", Arrays.asList("db/core_settings/cicd.json"));

    return data;
  }

  @Override
  @Before
  public void setUp() {
    super.setUp();
    mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
  }

  @Test
  public void testGetArtifactBytes() {
    mockServer.expect(requestTo(containsString("/boomerang/ci/repos")))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("{test : true}", MediaType.APPLICATION_JSON));

    assertNotNull(artifactoryRepositoryService.getArtifactBytes("ciTeamName", "ciComponentName",
        "version", "artifact"));

    mockServer.verify();
  }

  @Test
  public void testGetArtifactBytesWithBadRequest() {
    mockServer.expect(requestTo(containsString("/boomerang/ci/repos")))
        .andExpect(method(HttpMethod.GET)).andRespond(withBadRequest());

    assertNotNull(artifactoryRepositoryService.getArtifactBytes("ciTeamName", "ciComponentName",
        "version", "artifact"));

    mockServer.verify();
  }

  @Test
  public void testGetArtifactListWithBadRequest() {
    mockServer.expect(requestTo(containsString("/api/storage/boomerang/ci/repos")))
        .andExpect(method(HttpMethod.GET)).andRespond(withBadRequest());

    assertTrue(artifactoryRepositoryService
        .getArtifactList("ciTeamName", "ciComponentName", "version").isEmpty());

    mockServer.verify();
  }

  @Test
  public void testGetArtifactListWithEmptyResponse() {
    mockServer.expect(requestTo(containsString("/api/storage/boomerang/ci/repos")))
        .andExpect(method(HttpMethod.GET)).andRespond(withSuccess("", MediaType.APPLICATION_JSON));

    assertTrue(artifactoryRepositoryService
        .getArtifactList("ciTeamName", "ciComponentName", "version").isEmpty());

    mockServer.verify();
  }

  @Test
  public void testGetArtifactListWithBadJson() {
    mockServer.expect(requestTo(containsString("/api/storage/boomerang/ci/repos")))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("{ddads:", MediaType.APPLICATION_JSON));

    assertTrue(artifactoryRepositoryService
        .getArtifactList("ciTeamName", "ciComponentName", "version").isEmpty());

    mockServer.verify();
  }

  @Test
  public void testGetArtifactList() {
    mockServer.expect(requestTo(containsString("/api/storage/boomerang/ci/repos")))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("{\"children\": [{\"uri\": \"/test/uri\"}], \"created\": \"test\"}",
            MediaType.APPLICATION_JSON));

    List<VersionArtifact> versions =
        artifactoryRepositoryService.getArtifactList("ciTeamName", "ciComponentName", "version");
    assertEquals(1, versions.size());
    assertEquals("test/uri", versions.get(0).getName());

    mockServer.verify();
  }

  @Test
  public void testGetArtifactVersionCreatedDateWithBadRequest() {
    mockServer.expect(requestTo(containsString("/api/storage/boomerang/ci/repos")))
        .andExpect(method(HttpMethod.GET)).andRespond(withBadRequest());

    assertNull(artifactoryRepositoryService.getArtifactVersionCreatedDate("ciTeamName",
        "ciComponentName", "version"));

    mockServer.verify();
  }

  @Test
  public void testGetArtifactVersionCreatedDateWithEmptyResponse() {
    mockServer.expect(requestTo(containsString("/api/storage/boomerang/ci/repos")))
        .andExpect(method(HttpMethod.GET)).andRespond(withSuccess("", MediaType.APPLICATION_JSON));

    assertNull(artifactoryRepositoryService.getArtifactVersionCreatedDate("ciTeamName",
        "ciComponentName", "version"));

    mockServer.verify();
  }

  @Test
  public void testGetArtifactVersionCreatedDateWithBadJson() {
    mockServer.expect(requestTo(containsString("/api/storage/boomerang/ci/repos")))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("{ddads:", MediaType.APPLICATION_JSON));

    assertNull(artifactoryRepositoryService.getArtifactVersionCreatedDate("ciTeamName",
        "ciComponentName", "version"));

    mockServer.verify();
  }

  @Test
  public void testGetArtifactVersionCreatedDate() {
    mockServer.expect(requestTo(containsString("/api/storage/boomerang/ci/repos")))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("{\"children\": [{\"uri\": \"/test/uri\"}], \"created\": \"test\"}",
            MediaType.APPLICATION_JSON));

    assertEquals("test", artifactoryRepositoryService.getArtifactVersionCreatedDate("ciTeamName",
        "ciComponentName", "version"));

    mockServer.verify();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUploadFileNullResponse() {
    MockMultipartFile mockMultipartFile = new MockMultipartFile("files", "fileName.txt",
        "text/plain", "hello".getBytes(StandardCharsets.UTF_8));

    mockServer.expect(requestTo(containsString("/boomerang/folderPath/fileName")))
        .andExpect(method(HttpMethod.PUT)).andRespond(null);

    artifactoryRepositoryService.uploadFile(mockMultipartFile, "fileName", "folderPath");

    mockServer.verify();
  }

  @Test
  public void testUploadFile() throws IOException {
    MockMultipartFile mockMultipartFile = new MockMultipartFile("files", "fileName.txt",
        "text/plain", "hello".getBytes(StandardCharsets.UTF_8));

    mockServer.expect(requestTo(containsString("/boomerang/folderPath/fileName")))
        .andExpect(method(HttpMethod.PUT))
        .andRespond(withSuccess("{\"success\"}", MediaType.APPLICATION_JSON));

    artifactoryRepositoryService.uploadFile(mockMultipartFile, "fileName", "folderPath");

    mockServer.verify();
  }
}
