package net.boomerangplatform.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import net.boomerangplatform.AbstractBoomerangTest;
import net.boomerangplatform.Application;
import net.boomerangplatform.MongoConfig;
import net.boomerangplatform.model.Artifact;
import net.boomerangplatform.model.ArtifactSummary;
import net.boomerangplatform.model.Cfe;
import net.boomerangplatform.model.DependencyGraph;
import net.boomerangplatform.model.General;
import net.boomerangplatform.model.Issue;
import net.boomerangplatform.model.License;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class, MongoConfig.class})
@SpringBootTest
public class XrayRepositoryServiceTest extends AbstractBoomerangTest {

  protected MockRestServiceServer mockServer;

  @Autowired
  private XrayRepositoryService xrayRepositoryService;

  @Autowired
  @Qualifier("internalRestTemplate")
  protected RestTemplate restTemplate;


  @Override
  protected String[] getCollections() {
    return new String[] {"ci_teams", "ci_components", "ci_components_versions", "core_settings"};
  }

  @Override
  protected Map<String, List<String>> getData() {
    LinkedHashMap<String, List<String>> data = new LinkedHashMap<>();
    data.put("ci_components",
        Arrays.asList("db/ci_components/ci_component.json", "db/ci_components/ci_component2.json"));
    data.put("ci_components_versions",
        Arrays.asList("db/ci_components_versions/version1.json",
            "db/ci_components_versions/version2.json", "db/ci_components_versions/version3.json",
            "db/ci_components_versions/version4.json"));

    return data;
  }

  @Override
  @Before
  public void setUp() {
    super.setUp();
    mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
  }

  @Test
  public void testGetArtifactDependencygraphWitoutVersion() {
    DependencyGraph report =
        xrayRepositoryService.getArtifactDependencygraph("5c93e5494b8d3c00019577ab", "version");
    assertNull(report.getArtifact());
    assertTrue(report.getComponents().isEmpty());
  }

  @Test
  public void testGetArtifactDependencygraphWitoutComponent() {
    DependencyGraph report =
        xrayRepositoryService.getArtifactDependencygraph("5c93e5494b8d3c00019577aa", "nextgen-0");
    assertNull(report.getArtifact());
    assertTrue(report.getComponents().isEmpty());
  }

  @Test
  public void testGetArtifactDependencygraphOtherThanDocker() {
    DependencyGraph report =
        xrayRepositoryService.getArtifactDependencygraph("5c93e5494b8d3c00019577ab", "master-1");
    assertNull(report.getArtifact());
    assertTrue(report.getComponents().isEmpty());
  }

  @Test
  public void testGetArtifactDependencygraphWithoutTeam() {
    DependencyGraph report =
        xrayRepositoryService.getArtifactDependencygraph("5c93e5494b8d3c00019577ac", "nextgen-1");
    assertNull(report.getArtifact());
    assertTrue(report.getComponents().isEmpty());
  }

  @Test
  public void testGetArtifactDependencygraph() {

    mockServer.expect(requestTo(containsString("/api/v1/dependencyGraph/artifact")))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess(
            "{\"artifact\" : {\"name\": \"name\", \"path\": \"path\", \"pkg_type\": \"pkg_type\", \"component_id\": \"component_id\"},"
                + "\"components\": ["
                + "{\"componentName\": \"componentName1\", \"component_id\": \"component_id1\", \"package_type\": \"package_type1\", \"created\": \"created\""
                + ", \"components\": [{\"component_name\": \"component_name2\", \"component_id\": \"component_id2\", \"package_type\": \"package_type2\"}]},"

                + "{\"componentName\": \"componentName1\", \"component_id\": \"component_id1\", \"package_type\": \"package_type1\", \"created\": \"created\""
                + ", \"components\": [{\"component_name\": \"component_name2\", \"component_id\": \"component_id2\", \"package_type\": \"package_type2\", \"components\": [{\"component_name\": \"componentName1\"}]}"
                + "]}" + "]}",
            MediaType.APPLICATION_JSON));

    DependencyGraph report =
        xrayRepositoryService.getArtifactDependencygraph("5c93e5494b8d3c00019577ab", "master-0");

    assertNull(report.getArtifact());
    assertTrue(report.getComponents().isEmpty());

  }

  @Test
  public void testGetArtifactSummaryWitoutVersion() {
    ArtifactSummary report =
        xrayRepositoryService.getArtifactSummary("5c93e5494b8d3c00019577ab", "version");
    assertTrue(report.getArtifacts().isEmpty());
  }

  @Test
  public void testGetArtifactSummaryWitoutComponent() {
    ArtifactSummary report =
        xrayRepositoryService.getArtifactSummary("5c93e5494b8d3c00019577aa", "nextgen-0");
    assertTrue(report.getArtifacts().isEmpty());
  }

  @Test
  public void testGetArtifactSummaryOtherThanDocker() {
    ArtifactSummary report =
        xrayRepositoryService.getArtifactSummary("5c93e5494b8d3c00019577ab", "master-1");
    assertTrue(report.getArtifacts().isEmpty());
  }

  @Test
  public void testGetArtifactSummaryWithoutTeam() {
    ArtifactSummary report =
        xrayRepositoryService.getArtifactSummary("5c93e5494b8d3c00019577ac", "nextgen-1");
    assertTrue(report.getArtifacts().isEmpty());
  }

  @Test
  public void testGetArtifactSummary() {

    mockServer.expect(requestTo(containsString("/api/v1/summary/artifact")))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess("{\"artifacts\" : [{"
            + "\"general\": {\"name\": \"name\", \"path\": \"path\", \"pkg_type\": \"pkg_type\", \"sha256\": \"sha256\", \"component_id\": \"component_id\"}, "
            + "\"issues\": [{\"summary\": \"summary\", \"description\": \"description\", \"issueType\": \"issueType\", \"severity\": \"severity\", \"provider\": \"provider\", \"created\": \"created\", \"impactPath\": [\"impactPath\"], "
            + "\"cves\": [{\"cve\": \"cve\", \"cvss_v2\": \"cvss_v2\", \"cvss_v3\": \"cvss_v3\", \"cwe\": [\"cwe\"]}]"
            + "}],"
            + "\"licenses\": [{\"name\": \"name\", \"full_name\": \"full_name\", \"components\": [\"component\"], \"more_info_url\": [\"more_info_url\"]}]"
            + " }]}", MediaType.APPLICATION_JSON));

    ArtifactSummary report =
        xrayRepositoryService.getArtifactSummary("5c93e5494b8d3c00019577ab", "master-0");

    assertTrue(report.getArtifacts().isEmpty());

  }

  private void validateArtifact(Artifact a) {
    General general = a.getGeneral();

    assertEquals("name", general.getName());
    assertEquals("pkg_type", general.getPkgType());
    assertEquals("sha256", general.getSha256());
    assertEquals("component_id", general.getComponentId());

    List<Issue> issues = a.getIssues();
    assertFalse(issues.isEmpty());

    Issue issue = issues.get(0);
    assertEquals("summary", issue.getSummary());
    assertEquals("description", issue.getDescription());
    assertEquals("issueType", issue.getIssueType());
    assertEquals("severity", issue.getSeverity());
    assertEquals("provider", issue.getProvider());
    assertEquals("created", issue.getCreated());
    assertFalse(issue.getImpactPath().isEmpty());
    assertEquals("impactPath", issue.getImpactPath().get(0));
    assertFalse(issue.getCves().isEmpty());

    Cfe cfe = issue.getCves().get(0);
    assertEquals("cve", cfe.getCve());
    assertEquals("cvss_v2", cfe.getCvssV2());
    assertEquals("cvss_v3", cfe.getCvssV3());
    assertFalse(cfe.getCwe().isEmpty());
    assertEquals("cwe", cfe.getCwe().get(0));

    List<License> licenses = a.getLicenses();
    assertFalse(licenses.isEmpty());

    License license = licenses.get(0);
    assertEquals("name", license.getName());
    assertEquals("full_name", license.getFullName());
    assertFalse(license.getComponents().isEmpty());
    assertEquals("component", license.getComponents().get(0));
    assertFalse(license.getMoreInfoUrl().isEmpty());
    assertEquals("more_info_url", license.getMoreInfoUrl().get(0));
  }
}
