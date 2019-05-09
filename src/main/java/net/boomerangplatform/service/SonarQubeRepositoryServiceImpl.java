package net.boomerangplatform.service;

import java.util.ArrayList;
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

import net.boomerangplatform.model.ArtifactPackage;
import net.boomerangplatform.model.ArtifactSummary;
import net.boomerangplatform.model.Component;
import net.boomerangplatform.model.DependencyGraph;
import net.boomerangplatform.model.Path;
import net.boomerangplatform.model.Paths;
import net.boomerangplatform.model.SonarQubeReport;
import net.boomerangplatform.mongo.entity.CiComponentEntity;
import net.boomerangplatform.mongo.entity.CiComponentVersionEntity;
import net.boomerangplatform.mongo.entity.CiTeamEntity;
import net.boomerangplatform.mongo.model.CoreProperty;
import net.boomerangplatform.mongo.service.CiComponentService;
import net.boomerangplatform.mongo.service.CiComponentVersionService;
import net.boomerangplatform.mongo.service.CiTeamService;

@Service
public class SonarQubeRepositoryServiceImpl implements SonarQubeRepositoryService {

	Logger logger = LogManager.getLogger();

	@Value("${sonarqube.url.api.base}")
	private String sonarqubeUrlApiBase;
	
	@Value("${sonarqube.url.api.issues}")
	private String sonarqubeUrlApiIssues;	
	
	@Value("${sonarqube.url.api.measures}")
	private String sonarqubeUrlApiMeasures;	
	
	@Value("${sonarqube.boomerang.apitoken}")
	private String sonarqubeBoomerangApitoken;	

	@Autowired
	@Qualifier("internalRestTemplate")
	private RestTemplate internalRestTemplate;
	
	@Autowired
	private CiComponentService componentService;
	
//	@Autowired
//	private SettingsService settingsService;

	@Override
	public SonarQubeReport getIssuesSummary(String ciComponentId) {
		
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
		
		logger.info("ciComponentName=" + componentEntity.getName() + ", ciTeamId=" + componentEntity.getCiTeamId());
		
		final HttpEntity<?> request = new HttpEntity<>(getHeaders());
		
		StringBuilder sb = new StringBuilder();
		sb.append(sonarqubeUrlApiBase).append(sonarqubeUrlApiIssues);
		
		String url = sb.toString().replace("{componentKeys}", componentEntity.getUcdComponentId());

		final ResponseEntity<SonarQubeReport> response = internalRestTemplate.exchange(url, HttpMethod.GET, request, SonarQubeReport.class);
		SonarQubeReport sonarQubeReport = (SonarQubeReport) response.getBody();
		
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
}
