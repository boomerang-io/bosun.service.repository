package net.boomerangplatform.service;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import net.boomerangplatform.model.*;

@Component
public class ExampleServiceImp implements ExampleService {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Value("${example.property}")
	private String property;
	
	@Value("${example.username}")
	private String username;
	
	@Value("${example.password}")
	private String password;
	
	/*Create a get request using a java model class to receive the returned information in the 
	 * required format*/
	@Override
	public List<Model1> getModel1(String token) {
		
		List<Model1> model = new ArrayList<Model1>();
		
		/*Create API specific headers*/
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json;version=2");
		headers.add("Content-Type", "application/json");
		headers.add("Authorization", "token " + token);
		headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "Content-Type");

		HttpEntity<String> request = new HttpEntity<String>(headers);
		/*endpoint is the url to access the API*/
		String endpoint ="/api/access";
		try{	
			String url = String.format("%s%s%s/information","","",endpoint);
			ResponseEntity<List<Model1>> response=restTemplate.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<List<Model1>>() {});
			model = (List<Model1>) response.getBody();

		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return model;		
	}

	/*Create a put request using a java model class to present the api with information in the 
	 * required format*/
	@Override
	public Model2 getModel2(String value1, String value2) {	
		
		String authenticate = username + ":" + password;
		byte[] plainCredsBytes = authenticate.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Accept", "application/json");
	    headers.add("Authorization", "Basic " + base64Creds);
		
	    HttpEntity<Model2Request> request = new HttpEntity<Model2Request>(headers);

	    Model2 model2Response  = new Model2();

        try {
        	 String url = String.format("%s%s%s", property, value1, value2);
            ResponseEntity<Model2> response = restTemplate.exchange(url, HttpMethod.PUT, request, new ParameterizedTypeReference<Model2>() {});
            model2Response = (Model2) response.getBody();
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        return model2Response;
		
	}
	/*Interact with a database through jdbc and store the query results in a model java class*/
	@Override
	public List<DatabaseModel> databaseInteraction(String value1, String value2, String value3) {
		
		List<DatabaseModel> result = new ArrayList<DatabaseModel>();
		try{
		result = jdbcTemplate.query(
				"SELECT	e.NAME AS component_name, "+
						"b.name AS environment, "+	
						"f.NAME AS version, "+
						"c.id AS app_proc_req_id, "+
						"e.id AS component_id, "+
						"f.id AS version_id, "
				+ " from_unixtime(c.submitted_time/1000) as start_date, TIMESTAMPDIFF(SECOND, from_unixtime(c.submitted_time/1000),from_unixtime(d.submitted_time/1000)) as duration, "
				+ "COALESCE(c.result, \"Executing\") as result from .rt_deployment_request deploy, .ds_application a, .ds_environment b, .rt_app_process_request c, .rt_comp_process_request d, .ds_component e, .ds_version f"
				+ " where deploy.app_process_request_id = c.id and "+ "(e.id ='" + value1 + "'" + " or e.name ='" + value2 + "')" + " and a.active = 'Y' " 
			    + "and b.active = 'Y' and b.application_id = a.id and c.application_id = a.id and TIMESTAMPDIFF(DAY, from_unixtime(d.submitted_time/1000),current_timestamp) <"+ value3 +" "
				+ "and d.result is not null and d.id in (select g.id from .rt_comp_process_request g where g.parent_request_id = c.id and g.environment_id = b.id and g.submitted_time = (select max(h.submitted_time) "
				+ "from .rt_comp_process_request h where h.parent_request_id = c.id and h.environment_id = b.id)) and e.id = d.component_id and f.id = d.version_id order by start_date desc"
			, new RowMapper<DatabaseModel>() {
		public DatabaseModel mapRow(ResultSet rs, int arg1) throws SQLException {
			DatabaseModel p = new DatabaseModel();
			p.setName(rs.getString("component_name"));
			p.setVersion(rs.getString("version"));
			p.setStartDate(rs.getString("start_date"));
			p.setDuration(rs.getString("duration"));
			p.setStatus(rs.getString("result"));
			p.setVersionId(rs.getString("version_id"));
			p.setEnvironment(rs.getString("environment"));
			p.setApplicationProcessId(rs.getString("app_proc_req_id"));
			return p;
		}
		});
		
		}catch (Exception e) {
			e.printStackTrace();
	       }
		return result;
	}
	
}
