package net.boomerangplatform.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
import net.boomerangplatform.model.BaseComponent;
import net.boomerangplatform.model.History;
import net.boomerangplatform.model.Issues;
import net.boomerangplatform.model.Measure;
import net.boomerangplatform.model.Measures;
import net.boomerangplatform.model.SonarComponent;
import net.boomerangplatform.model.SonarQubeDetailReport;
import net.boomerangplatform.model.SonarQubeReport;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class, MongoConfig.class})
@SpringBootTest
public class SonarQubeRepositoryServiceTest extends AbstractBoomerangTest {

  protected MockRestServiceServer mockServer;

  @Autowired
  private SonarQubeRepositoryService sonarQubeRepositoryService;

  @Autowired
  @Qualifier("internalRestTemplate")
  protected RestTemplate restTemplate;


  @Override
  protected String[] getCollections() {
    return new String[] {"ci_components", "ci_components_versions"};
  }

  @Override
  protected Map<String, List<String>> getData() {
    LinkedHashMap<String, List<String>> data = new LinkedHashMap<>();
    data.put("ci_components", Arrays.asList("db/ci_components/ci_component.json"));
    data.put("ci_components_versions", Arrays.asList("db/ci_components_versions/version1.json",
        "db/ci_components_versions/version2.json"));

    return data;
  }

  @Override
  @Before
  public void setUp() {
    super.setUp();
    mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
  }

  @Test
  public void testGetReportWitoutVersion() {
    SonarQubeReport report =
        sonarQubeRepositoryService.getReport("5c93e5494b8d3c00019577ab", "version");
    assertNull(report.getIssues());
    assertNull(report.getMeasures());
  }

  @Test
  public void testGetReportWitoutComponent() {
    SonarQubeReport report =
        sonarQubeRepositoryService.getReport("5c93e5494b8d3c00019577aa", "nextgen-0");
    assertNull(report.getIssues());
    assertNull(report.getMeasures());
  }

  @Test
  public void testGetReportWitoutDate() {
    mockServer.expect(requestTo(containsString("/api/project_analyses/search")))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("{\"analyses\" : [{}]}", MediaType.APPLICATION_JSON));

    SonarQubeReport report =
        sonarQubeRepositoryService.getReport("5c93e5494b8d3c00019577ab", "master-0");
    assertNull(report.getIssues());
    assertNull(report.getMeasures());

    mockServer.verify();
  }

  @Test
  public void testGetReport() {
    mockServer.expect(requestTo(containsString("/api/project_analyses/search")))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(
            "{\"analyses\" : [{\"key\" : \"test\", \"date\" : \"2019-01-31T16:07:30.499Z\", \"events\" : [{\"key\": \"\", \"name\": \"name\"}, {\"key\": \"\", \"category\": \"\", \"name\": \"master-0\", \"description\": \"\"}]}]}",
            MediaType.APPLICATION_JSON));

    mockServer.expect(requestTo(containsString("/api/measures/search_history")))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(
            "{\"measures\" : [{\"metric\": \"ncloc\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]}, "
                + "    {\"metric\": \"complexity\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]}, "
                + "    {\"metric\": \"violations\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]}, "
                + "    {\"metric\": \"tests\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]}, "
                + "    {\"metric\": \"test_errors\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]}, "
                + "    {\"metric\": \"test_failures\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
                + "    {\"metric\": \"skipped_tests\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
                + "    {\"metric\": \"test_success_density\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
                + "    {\"metric\": \"test_execution_time\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
                + "    {\"metric\": \"coverage\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
                + "    {\"metric\": \"lines_to_cover\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
                + "    {\"metric\": \"uncovered_lines\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": null}]},"
                + "    {\"metric\": \"line_coverage\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": null}]}]}",
            MediaType.APPLICATION_JSON));

    mockServer.expect(requestTo(containsString("/api/issues/search?componentKeys")))
        .andExpect(method(HttpMethod.GET)).andRespond(
            withSuccess("{\"total\": 0, \"p\": 0, \"ps\": 0, \"effortTotal\": 0, \"debtTotal\": 0, "
                + "\"issues\": " + "["
                + "{\"key\": \"test\", \"rule\": \"test\",\"severity\": \"BLOCKER\",\"component\": \"test\", \"project\": \"test\", \"hash\": \"test\",\"status\": \"open\", \"message\": \"test\",\"effort\": \"test\", "
                + "\"debt\": \"test\", \"author\": \"test\", \"creationDate\": \"test\", \"updateDate\": \"test\", \"type\": \"test\", \"organization\": \"test\", \"resolution\": \"test\", \"closeDate\": \"test\", \"line\": 0, "
                + "\"textRange\": {\"startLine\": 0, \"endLine\": 0, \"startOffset\": 0, \"endOffset\": 0}, "
                + "\"flows\": [{\"locations\" : [{\"component\": \"component\", \"msg\": \"msg\"}]} "
                + "], \"tags\": [\"tag\"], \"fromHotspot\": true}, "
                + "{\"key\": \"test2\", \"rule\": \"test2\",\"severity\": \"CRITICAL\",\"component\": \"test\", \"project\": \"test\", \"hash\": \"test\",\"status\": \"open\", \"message\": \"test\",\"effort\": \"test\", "
                + "\"debt\": \"test\", \"author\": \"test\", \"creationDate\": \"test\", \"updateDate\": \"test\", \"type\": \"test\", \"organization\": \"test\", \"resolution\": \"test\", \"closeDate\": \"test\", \"line\": 0, "
                + "\"textRange\": {\"startLine\": 0, \"endLine\": 0, \"startOffset\": 0, \"endOffset\": 0}, "
                + "\"flows\": [{\"locations\" : [{\"component\": \"component\", \"msg\": \"msg\"}]} "
                + "], \"tags\": [\"tag\"], \"fromHotspot\": true}, "
                + "{\"key\": \"test3\", \"rule\": \"test3\",\"severity\": \"MAJOR\",\"component\": \"test\", \"project\": \"test\", \"hash\": \"test\",\"status\": \"open\", \"message\": \"test\",\"effort\": \"test\", "
                + "\"debt\": \"test\", \"author\": \"test\", \"creationDate\": \"test\", \"updateDate\": \"test\", \"type\": \"test\", \"organization\": \"test\", \"resolution\": \"test\", \"closeDate\": \"test\", \"line\": 0, "
                + "\"textRange\": {\"startLine\": 0, \"endLine\": 0, \"startOffset\": 0, \"endOffset\": 0}, "
                + "\"flows\": [{\"locations\" : [{\"component\": \"component\", \"msg\": \"msg\"}]} "
                + "], \"tags\": [\"tag\"], \"fromHotspot\": true}, "
                + "{\"key\": \"test4\", \"rule\": \"test4\",\"severity\": \"MINOR\",\"component\": \"test\", \"project\": \"test\", \"hash\": \"test\",\"status\": \"open\", \"message\": \"test\",\"effort\": \"test\", "
                + "\"debt\": \"test\", \"author\": \"test\", \"creationDate\": \"test\", \"updateDate\": \"test\", \"type\": \"test\", \"organization\": \"test\", \"resolution\": \"test\", \"closeDate\": \"test\", \"line\": 0, "
                + "\"textRange\": {\"startLine\": 0, \"endLine\": 0, \"startOffset\": 0, \"endOffset\": 0}, "
                + "\"flows\": [{\"locations\" : [{\"component\": \"component\", \"msg\": \"msg\"}]} "
                + "], \"tags\": [\"tag\"], \"fromHotspot\": true}, "
                + "{\"key\": \"test5\", \"rule\": \"test5\",\"severity\": \"INFO\",\"component\": \"test\", \"project\": \"test\", \"hash\": \"test\",\"status\": \"open\", \"message\": \"test\",\"effort\": \"test\", "
                + "\"debt\": \"test\", \"author\": \"test\", \"creationDate\": \"test\", \"updateDate\": \"test\", \"type\": \"test\", \"organization\": \"test\", \"resolution\": \"test\", \"closeDate\": \"test\", \"line\": 0, "
                + "\"textRange\": {\"startLine\": 0, \"endLine\": 0, \"startOffset\": 0, \"endOffset\": 0}, "
                + "\"flows\": [{\"locations\" : [{\"component\": \"component\", \"msg\": \"msg\"}]} "
                + "], \"tags\": [\"tag\"], \"fromHotspot\": true}, "
                + "{\"key\": \"test5\", \"rule\": \"test5\",\"severity\": \"INFO\",\"component\": \"test\", \"project\": \"test\", \"hash\": \"test\",\"status\": \"closed\", \"message\": \"test\",\"effort\": \"test\", "
                + "\"debt\": \"test\", \"author\": \"test\", \"creationDate\": \"test\", \"updateDate\": \"test\", \"type\": \"test\", \"organization\": \"test\", \"resolution\": \"test\", \"closeDate\": \"test\", \"line\": 0, "
                + "\"textRange\": {\"startLine\": 0, \"endLine\": 0, \"startOffset\": 0, \"endOffset\": 0}, "
                + "\"flows\": [{\"locations\" : [{\"component\": \"component\", \"msg\": \"msg\"}]} "
                + "], \"tags\": [\"tag\"], \"fromHotspot\": true}, "
                + "{\"key\": \"test6\", \"rule\": \"test6\",\"severity\": \"\",\"component\": \"test\", \"project\": \"test\", \"hash\": \"test\",\"status\": \"open\", \"message\": \"test\",\"effort\": \"test\", "
                + "\"debt\": \"test\", \"author\": \"test\", \"creationDate\": \"test\", \"updateDate\": \"test\", \"type\": \"test\", \"organization\": \"test\", \"resolution\": \"test\", \"closeDate\": \"test\", \"line\": 0, "
                + "\"textRange\": {\"startLine\": 0, \"endLine\": 0, \"startOffset\": 0, \"endOffset\": 0}, "
                + "\"flows\": [{\"locations\" : [{\"component\": \"component\", \"msg\": \"msg\"}]} "
                + "], \"tags\": [\"tag\"], \"fromHotspot\": true}" + "]," + " \"components\": ["
                + "{\"organization\": \"organization\", \"key\": \"key\", \"uuid\": \"uuid\", \"enabled\": true, \"qualifier\": \"qualifier\", \"name\": \"name\", \"longName\": \"longName\", \"path\": \"path\"}, "
                + "{\"organization\": \"organization\", \"key\": \"key\", \"uuid\": \"uuid\", \"enabled\": true, \"qualifier\": \"FIL\", \"name\": \"name\", \"longName\": \"longName\", \"path\": \"path\"}"
                + "], \"facets\": [\"facet\"] }", MediaType.APPLICATION_JSON));

    SonarQubeReport report =
        sonarQubeRepositoryService.getReport("5c93e5494b8d3c00019577ab", "master-0");

    assertNotNull(report.getMeasures());
    validateMeasures(report.getMeasures());
    assertNotNull(report.getIssues());
    validateIssues(report.getIssues());

    mockServer.verify();
  }

  @Test
  public void testGetTestCoverageReportWitoutVersion() {
    SonarQubeReport report =
        sonarQubeRepositoryService.getTestCoverageReport("5c93e5494b8d3c00019577ab", "version");
    assertNull(report.getIssues());
    assertNull(report.getMeasures());
  }

  @Test
  public void testGetTestCoverageReportWitoutComponent() {
    SonarQubeReport report =
        sonarQubeRepositoryService.getTestCoverageReport("5c93e5494b8d3c00019577aa", "nextgen-0");
    assertNull(report.getIssues());
    assertNull(report.getMeasures());
  }

  @Test
  public void testGetTestCoverageReport() {
    mockServer.expect(requestTo(containsString("/api/project_analyses/search")))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(
            "{\"analyses\" : [{\"key\" : \"test\", \"date\" : \"2019-01-31T16:07:30.499Z\", \"events\" : [{\"key\": \"\", \"category\": \"\", \"name\": \"master-0\", \"description\": \"\"}]}]}",
            MediaType.APPLICATION_JSON));

    mockServer.expect(requestTo(containsString("/api/measures/search_history")))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess(
            "{\"measures\" : [{\"metric\": \"ncloc\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]}, "
                + "    {\"metric\": \"complexity\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]}, "
                + "    {\"metric\": \"violations\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]}, "
                + "    {\"metric\": \"tests\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]}, "
                + "    {\"metric\": \"test_errors\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]}, "
                + "    {\"metric\": \"test_failures\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
                + "    {\"metric\": \"skipped_tests\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
                + "    {\"metric\": \"test_success_density\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
                + "    {\"metric\": \"test_execution_time\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
                + "    {\"metric\": \"coverage\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
                + "    {\"metric\": \"lines_to_cover\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
                + "    {\"metric\": \"uncovered_lines\", \"value\": null, \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
                + "    {\"metric\": \"line_coverage\", \"value\": null, \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]}]}",
            MediaType.APPLICATION_JSON));

    SonarQubeReport report =
        sonarQubeRepositoryService.getTestCoverageReport("5c93e5494b8d3c00019577ab", "master-0");

    assertNotNull(report.getMeasures());
    assertNull(report.getIssues());

    mockServer.verify();
  }


  @Test
  public void testGetDetailTestCoverageReportWitoutVersion() {
    SonarQubeDetailReport report = sonarQubeRepositoryService
        .getDetailTestCoverageReport("5c93e5494b8d3c00019577ab", "version");
    assertNull(report.getBaseComponent());
    assertNull(report.getComponents());
    assertNull(report.getPaging());
  }

  @Test
  public void testGetDetailTestCoverageReportWitoutComponent() {
    SonarQubeDetailReport report = sonarQubeRepositoryService
        .getDetailTestCoverageReport("5c93e5494b8d3c00019577aa", "nextgen-0");
    assertNull(report.getBaseComponent());
    assertNull(report.getComponents());
    assertNull(report.getPaging());
  }

  @Test
  public void testGetDetailTestCoverage() {
    mockServer.expect(requestTo(containsString("/api/measures/component_tree")))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withSuccess("{\"paging\": {\"pageIndex\": 0, \"pageSize\": 1, \"total\": 1},"
            + "\"baseComponent\": {\"key\": \"key\", \"name\": \"name\", \"qualifier\": \"qualifier\", "
            + "\"measures\" : [{\"metric\": \"ncloc\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]}, "
            + "    {\"metric\": \"complexity\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]}, "
            + "    {\"metric\": \"violations\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]}, "
            + "    {\"metric\": \"tests\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]}, "
            + "    {\"metric\": \"test_errors\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]}, "
            + "    {\"metric\": \"test_failures\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
            + "    {\"metric\": \"skipped_tests\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
            + "    {\"metric\": \"test_success_density\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
            + "    {\"metric\": \"test_execution_time\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
            + "    {\"metric\": \"coverage\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
            + "    {\"metric\": \"lines_to_cover\", \"value\": \"0\", \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
            + "    {\"metric\": \"uncovered_lines\", \"value\": null, \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]},"
            + "    {\"metric\": \"line_coverage\", \"value\": null, \"bestValue\": true, \"history\": [{\"date\": null, \"value\": 0}]}]}, "
            + "\"components\": [{\"key\": \"key\", \"name\": \"name\", \"qualifier\": \"qualifier\", \"language\": \"language\", \"path\": \"path\"}] }",
            MediaType.APPLICATION_JSON));

    SonarQubeDetailReport report = sonarQubeRepositoryService
        .getDetailTestCoverageReport("5c93e5494b8d3c00019577ab", "master-0");

    assertNotNull(report.getBaseComponent());
    validateBaseComponent(report.getBaseComponent());

    assertNotNull(report.getComponents());
    validateSonarComponent(report.getComponents().get(0));
    mockServer.verify();
  }

  private void validateMeasures(Measures m) {
    assertEquals(0, m.getNcloc().intValue());
    assertEquals(0, m.getComplexity().intValue());
    assertEquals(0, m.getViolations().intValue());
    assertEquals(0, m.getTests().intValue());
    assertEquals(0, m.getTestErrors().intValue());
    assertEquals(0, m.getTestFailures().intValue());
    assertEquals(0, m.getSkippedTests().intValue());
    assertEquals(0d, m.getTestSuccessDensity().doubleValue(), 0.001);
    assertEquals(0, m.getTestExecutionTime().intValue());
    assertEquals(0d, m.getCoverage().doubleValue(), 0.001);
    assertEquals(0, m.getLinesToCover().intValue());
    assertEquals(0, m.getUncoveredLines().intValue());
    assertEquals(0d, m.getLineCoverage().doubleValue(), 0.001);
  }

  private void validateIssues(Issues i) {
    assertEquals(6, i.getTotal().intValue());
    assertEquals(1, i.getBlocker().intValue());
    assertEquals(1, i.getCritical().intValue());
    assertEquals(1, i.getMajor().intValue());
    assertEquals(1, i.getMinor().intValue());
    assertEquals(1, i.getInfo().intValue());
    assertEquals(1, i.getFilesAnalyzed().intValue());
  }

  private void validateBaseComponent(BaseComponent bc) {
    assertEquals("key", bc.getKey());
    assertEquals("name", bc.getName());
    assertEquals("qualifier", bc.getQualifier());
    assertFalse(bc.getMeasures().isEmpty());

    Measure measure = bc.getMeasures().get(0);
    assertEquals("ncloc", measure.getMetric());
    assertEquals("0", measure.getValue());
    assertTrue(measure.getBestValue());
    assertFalse(measure.getHistory().isEmpty());
    History history = measure.getHistory().get(0);
    assertNull(history.getDate());
    assertEquals("0", history.getValue());
  }

  private void validateSonarComponent(SonarComponent sc) {
    assertEquals("key", sc.getKey());
    assertEquals("name", sc.getName());
    assertEquals("qualifier", sc.getQualifier());
    assertEquals("language", sc.getLanguage());
    assertEquals("path", sc.getPath());
  }
}
