package net.boomerangplatform.service;

import java.util.List;

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

import net.boomerangplatform.model.Analysis;
import net.boomerangplatform.model.Event;
import net.boomerangplatform.model.History;
import net.boomerangplatform.model.IssueComponent;
import net.boomerangplatform.model.Issues;
import net.boomerangplatform.model.Measure;
import net.boomerangplatform.model.Measures;
import net.boomerangplatform.model.SonarQubeIssue;
import net.boomerangplatform.model.SonarQubeIssuesReport;
import net.boomerangplatform.model.SonarQubeMeasuresReport;
import net.boomerangplatform.model.SonarQubeProjectVersions;
import net.boomerangplatform.model.SonarQubeReport;
import net.boomerangplatform.mongo.entity.CiComponentEntity;
import net.boomerangplatform.mongo.entity.CiComponentVersionEntity;
import net.boomerangplatform.mongo.service.CiComponentService;
import net.boomerangplatform.mongo.service.CiComponentVersionService;

@Service
public class SonarQubeRepositoryServiceImpl implements SonarQubeRepositoryService {

	Logger logger = LogManager.getLogger();

	@Value("${sonarqube.url.api.base}")
	private String sonarqubeUrlApiBase;
	
	@Value("${sonarqube.url.api.metrics.violations}")
	private String sonarqubeUrlApiMetricsViolations;
	
	@Value("${sonarqube.url.api.metrics.tests}")
	private String sonarqubeUrlApiMetricsTests;
	
	@Value("${sonarqube.url.api.metrics.coverage}")
	private String sonarqubeUrlApiMetricsCoverage;
	
	@Value("${sonarqube.url.api.project.versions}")
	private String sonarqubeUrlApiProjectVersions;

	@Value("${sonarqube.url.api.issues.version}")
	private String sonarqubeUrlApiIssuesVersion;	
	
	@Value("${sonarqube.url.api.measures.version}")
	private String sonarqubeUrlApiMeasuresVersion;	
	
	@Value("${sonarqube.url.api.issues.latest}")
	private String sonarqubeUrlApiIssuesLatest;	
	
	@Value("${sonarqube.url.api.measures.latest}")
	private String sonarqubeUrlApiMeasuresLatest;	
	
	@Value("${sonarqube.boomerang.apitoken}")
	private String sonarqubeBoomerangApitoken;	

	@Autowired
	@Qualifier("internalRestTemplate")
	private RestTemplate internalRestTemplate;
	
	@Autowired
	private CiComponentService componentService;
	
	@Autowired
	private CiComponentVersionService versionService;
	
//	@Autowired
//	private SettingsService settingsService;

	@Override
	public SonarQubeReport getReport(String ciComponentId, String version) {
		
		CiComponentVersionEntity componentVersionEntity = versionService.findVersionWithNameForComponentId(version, ciComponentId);
		
		if (componentVersionEntity == null) {
			return new SonarQubeReport();
		}
		
//		CiComponentEntity componentEntity = componentService.findById(componentVersionEntity.getCiComponentId());
		
//		Temporary workaround as cannot use componentService.findById() as it requires new isActive flag which does not yet exist in ci_components collection
		CiComponentEntity componentEntity = null;
		
		List<CiComponentEntity> componentEntityList = componentService.getAllComponentEntity();
		for (CiComponentEntity entity : componentEntityList) {
			if (entity.getId().equalsIgnoreCase(ciComponentId)) {
				componentEntity = entity;
				break;
			}
		}
		
		if (componentEntity == null) {
			return new SonarQubeReport();
		}
		
		logger.info("ciComponentName=" + componentEntity.getName() + ", ciComponentVersionId=" + componentVersionEntity.getId() + ", ciTeamId=" + componentEntity.getCiTeamId());
		
//		-------------------
		
		String date = getSonarQubeDateForVersion(componentEntity.getUcdComponentId(), version);
		
		if (date == null) {
			return new SonarQubeReport();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(sonarqubeUrlApiBase).append(sonarqubeUrlApiMeasuresVersion).append(sonarqubeUrlApiMetricsViolations);
		
		String url = sb.toString()
				.replace("{component}", componentEntity.getUcdComponentId())
				.replace("{from}", date)
				.replace("{to}", date);
		
		final HttpEntity<?> request = new HttpEntity<>(getHeaders());
		
		final ResponseEntity<SonarQubeMeasuresReport> sonarQubeMeasuresReportResponse = internalRestTemplate.exchange(url, HttpMethod.GET, request, SonarQubeMeasuresReport.class);
		SonarQubeMeasuresReport sonarQubeMeasuresReport = (SonarQubeMeasuresReport) sonarQubeMeasuresReportResponse.getBody();	
		
		Measures measures = getMeasures(sonarQubeMeasuresReport.getMeasures());	
		
//		-------------------		
		
		sb = new StringBuilder();
		sb.append(sonarqubeUrlApiBase).append(sonarqubeUrlApiIssuesVersion);
		
		url = sb.toString()
				.replace("{componentKeys}", componentEntity.getUcdComponentId())
				.replace("{createdBefore}", date);

		final ResponseEntity<SonarQubeIssuesReport> sonarQubeReportResponse = internalRestTemplate.exchange(url, HttpMethod.GET, request, SonarQubeIssuesReport.class);
		SonarQubeIssuesReport sonarQubeIssuesReport = (SonarQubeIssuesReport) sonarQubeReportResponse.getBody();
		
		Issues issues = getIssues(sonarQubeIssuesReport.getIssues(), sonarQubeIssuesReport.getComponents());
			
		SonarQubeReport sonarQubeReport = new SonarQubeReport();
		sonarQubeReport.setIssues(issues);
		sonarQubeReport.setMeasures(measures);
		
		return sonarQubeReport;
	}
	
	@Override
	public SonarQubeReport getTestReport(String ciComponentId, String version) {
		
		CiComponentVersionEntity componentVersionEntity = versionService.findVersionWithNameForComponentId(version, ciComponentId);
		
		if (componentVersionEntity == null) {
			logger.info("getTestReport-componentVersionEntity == null");
			return new SonarQubeReport();
		}
		
//		CiComponentEntity componentEntity = componentService.findById(componentVersionEntity.getCiComponentId());
		
//		Temporary workaround as cannot use componentService.findById() as it requires new isActive flag which does not yet exist in ci_components collection
		CiComponentEntity componentEntity = null;
		
		List<CiComponentEntity> componentEntityList = componentService.getAllComponentEntity();
		for (CiComponentEntity entity : componentEntityList) {
			if (entity.getId().equalsIgnoreCase(ciComponentId)) {
				componentEntity = entity;
				break;
			}
		}
		
		if (componentEntity == null) {
			return new SonarQubeReport();
		}
		
		logger.info("ciComponentName=" + componentEntity.getName() + ", ciComponentVersionId=" + componentVersionEntity.getId() + ", ciTeamId=" + componentEntity.getCiTeamId());
		
//		-------------------
		
		String date = getSonarQubeDateForVersion(componentEntity.getUcdComponentId(), version);
		
		StringBuilder sb = new StringBuilder();
		sb.append(sonarqubeUrlApiBase).append(sonarqubeUrlApiMeasuresVersion).append(sonarqubeUrlApiMetricsTests);
		
		String url = sb.toString()
				.replace("{component}", componentEntity.getUcdComponentId())
				.replace("{from}", date)
				.replace("{to}", date);
		
		final HttpEntity<?> request = new HttpEntity<>(getHeaders());
		
		final ResponseEntity<SonarQubeMeasuresReport> sonarQubeMeasuresReportResponse = internalRestTemplate.exchange(url, HttpMethod.GET, request, SonarQubeMeasuresReport.class);
		SonarQubeMeasuresReport sonarQubeMeasuresReport = (SonarQubeMeasuresReport) sonarQubeMeasuresReportResponse.getBody();	
		
		Measures measures = getMeasures(sonarQubeMeasuresReport.getMeasures());			
		
//		-------------------	
		
		SonarQubeReport sonarQubeReport = new SonarQubeReport();
		sonarQubeReport.setIssues(null);
		sonarQubeReport.setMeasures(measures);
		
		return sonarQubeReport;
	}
	
	@Override
	public SonarQubeReport getCoverageReport(String ciComponentId, String version) {
		
		CiComponentVersionEntity componentVersionEntity = versionService.findVersionWithNameForComponentId(version, ciComponentId);
		
		if (componentVersionEntity == null) {
			return new SonarQubeReport();
		}
		
//		CiComponentEntity componentEntity = componentService.findById(componentVersionEntity.getCiComponentId());
		
//		Temporary workaround as cannot use componentService.findById() as it requires new isActive flag which does not yet exist in ci_components collection
		CiComponentEntity componentEntity = null;
		
		List<CiComponentEntity> componentEntityList = componentService.getAllComponentEntity();
		for (CiComponentEntity entity : componentEntityList) {
			if (entity.getId().equalsIgnoreCase(ciComponentId)) {
				componentEntity = entity;
				break;
			}
		}
		
		if (componentEntity == null) {
			return new SonarQubeReport();
		}
		
		logger.info("ciComponentName=" + componentEntity.getName() + ", ciComponentVersionId=" + componentVersionEntity.getId() + ", ciTeamId=" + componentEntity.getCiTeamId());
		
//		-------------------
		
		String date = getSonarQubeDateForVersion(componentEntity.getUcdComponentId(), version);
		
		if (date == null) {
			return new SonarQubeReport();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(sonarqubeUrlApiBase).append(sonarqubeUrlApiMeasuresVersion).append(sonarqubeUrlApiMetricsCoverage);
		
		String url = sb.toString()
				.replace("{component}", componentEntity.getUcdComponentId())
				.replace("{from}", date)
				.replace("{to}", date);
		
		final HttpEntity<?> request = new HttpEntity<>(getHeaders());
		
		final ResponseEntity<SonarQubeMeasuresReport> sonarQubeMeasuresReportResponse = internalRestTemplate.exchange(url, HttpMethod.GET, request, SonarQubeMeasuresReport.class);
		SonarQubeMeasuresReport sonarQubeMeasuresReport = (SonarQubeMeasuresReport) sonarQubeMeasuresReportResponse.getBody();	
		
		Measures measures = getMeasures(sonarQubeMeasuresReport.getMeasures());	
		
//		-------------------	
		
		SonarQubeReport sonarQubeReport = new SonarQubeReport();
		sonarQubeReport.setIssues(null);
		sonarQubeReport.setMeasures(measures);
		
		return sonarQubeReport;
	}

	private HttpHeaders getHeaders() {
        final String plainCreds = sonarqubeBoomerangApitoken + ":";
        final byte[] plainCredsBytes = plainCreds.getBytes();
        final byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        final String base64Creds = new String(base64CredsBytes);

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Authorization", "Basic " + base64Creds);
        
        return headers;
	}
	
	private String getSonarQubeDateForVersion(String project, String version) {
		
		final HttpEntity<?> request = new HttpEntity<>(getHeaders());
		
		StringBuilder sb = new StringBuilder();
		sb.append(sonarqubeUrlApiBase).append(sonarqubeUrlApiProjectVersions);
		
		String url = sb.toString().replace("{project}", project);

		final ResponseEntity<SonarQubeProjectVersions> SonarQubeProjectVersionsResponse = internalRestTemplate.exchange(url, HttpMethod.GET, request, SonarQubeProjectVersions.class);
		SonarQubeProjectVersions sonarQubeProjectVersions = (SonarQubeProjectVersions) SonarQubeProjectVersionsResponse.getBody();
		
		for (Analysis analysis : sonarQubeProjectVersions.getAnalyses()) {
			for (Event event : analysis.getEvents()) {
				if (event.getName().equalsIgnoreCase(version)) {
					return analysis.getDate();
				}
			}
		}
		
		return null;
	}
	
	private Measures getMeasures(List<Measure> measureList) {		
		Measures measures = new Measures();
		for (Measure measure : measureList) {			
			for (History history : measure.getHistory()) {
				switch (measure.getMetric()) {
				case "ncloc":
					measures.setNcloc(history.getValue() != null ? Integer.valueOf(history.getValue()) : 0);
					break;
				case "complexity":
					measures.setComplexity(history.getValue() != null ? Integer.valueOf(history.getValue()) : 0);
					break;
				case "violations":
					measures.setViolations(history.getValue() != null ? Integer.valueOf(history.getValue()) : 0);
					break;					
				case "tests":
					measures.setTests(history.getValue() != null ? Integer.valueOf(history.getValue()) : 0);
					break;
				case "test_errors":
					measures.setTestErrors(history.getValue() != null ? Integer.valueOf(history.getValue()) : 0);
					break;
				case "test_failures":
					measures.setTestFailures(history.getValue() != null ? Integer.valueOf(history.getValue()) : 0);
					break;
				case "skipped_tests":
					measures.setSkippedTests(history.getValue() != null ? Integer.valueOf(history.getValue()) : 0);
					break;	
				case "test_success_density":
					measures.setTestSuccessDensity(history.getValue() != null ? Double.valueOf(history.getValue()) : 0);
					break;	
				case "test_execution_time":
					measures.setTestExecutionTime(history.getValue() != null ? Integer.valueOf(history.getValue()) : 0);
					break;					
				case "coverage":
					measures.setCoverage(history.getValue() != null ? Double.valueOf(history.getValue()) : 0);
					break;
				case "lines_to_cover":
					measures.setLinesToCover(history.getValue() != null ? Integer.valueOf(history.getValue()) : 0);
					break;
				case "uncovered_lines":
					measures.setUncoveredLines(history.getValue() != null ? Integer.valueOf(history.getValue()) : 0);
					break;
				case "line_coverage":
					measures.setLineCoverage(history.getValue() != null ? Double.valueOf(history.getValue()) : 0);
					break;				
				}				
			}
		}		
		return measures;
	}
	
	private Issues getIssues(List<SonarQubeIssue> sonarQubeIssues, List<IssueComponent> issueComponents) {
		int total = 0;
		int blocker = 0;
		int critical = 0;
		int major = 0;
		int minor = 0;
		int info = 0;
		int filesAnalyzed = 0;
		
		for (SonarQubeIssue sonarQubeIssue : sonarQubeIssues) {
			if (sonarQubeIssue.getStatus().equalsIgnoreCase("open")) {
				switch (sonarQubeIssue.getSeverity()) {
                case "BLOCKER":
                	blocker++;
                	break;
                case "CRITICAL":
                	critical++; 
                	break;
                case "MAJOR": 
                	major++; 
                	break;
                case "MINOR": 
                	minor++; 
                	break;
                case "INFO": 
                	info++; 
                	break;
				}				
				total++;
			}			
		}
		
		for (IssueComponent issueComponent : issueComponents) {
			switch (issueComponent.getQualifier()) {
			case "FIL": 
				filesAnalyzed++;
				break;
			}
		}
		
		Issues issues = new Issues();
		issues.setTotal(total);
		issues.setBlocker(blocker);
		issues.setCritical(critical);
		issues.setMajor(major);
		issues.setMinor(minor);
		issues.setInfo(info);
		issues.setFilesAnalyzed(filesAnalyzed);
		
		return issues;
	}
}
