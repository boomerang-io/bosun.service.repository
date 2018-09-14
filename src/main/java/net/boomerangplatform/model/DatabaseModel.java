package net.boomerangplatform.model;

public class DatabaseModel {

	private String name;
	private String version;
	private String startDate;
	private String duration;
	private String status;
	private String versionId;
	private String environment;
	private String applicationProcessId;
	
	public DatabaseModel(){
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	
	public String getApplicationProcessId() {
		return applicationProcessId;
	}

	public void setApplicationProcessId(String applicationProcessId) {
		this.applicationProcessId = applicationProcessId;
	}
	
	
	
}
