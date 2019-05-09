package net.boomerangplatform.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SonarQubeIssue {

	@JsonProperty("key")
	private String key;
	@JsonProperty("rule")
	private String rule;
	@JsonProperty("severity")
	private String severity;
	@JsonProperty("component")
	private String component;
	@JsonProperty("project")
	private String project;
	@JsonProperty("line")
	private Integer line;
	@JsonProperty("hash")
	private String hash;
	@JsonProperty("textRange")
	private TextRange textRange;
	@JsonProperty("flows")
	private List<Flow> flows;
	@JsonProperty("status")
	private String status;
	@JsonProperty("message")
	private String message;
	@JsonProperty("effort")
	private String effort;
	@JsonProperty("debt")
	private String debt;
	@JsonProperty("author")
	private String author;
	@JsonProperty("tags")
	private List<String> tags;
	@JsonProperty("creationDate")
	private String creationDate;
	@JsonProperty("updateDate")
	private String updateDate;
	@JsonProperty("type")
	private String type;
	@JsonProperty("organization")
	private String organization;
	@JsonProperty("fromHotspot")
	private Boolean fromHotspot;
	@JsonProperty("resolution")
	private String resolution;
	@JsonProperty("closeDate")
	private String closeDate;
	
	public SonarQubeIssue() {
		
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public Integer getLine() {
		return line;
	}

	public void setLine(Integer line) {
		this.line = line;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public TextRange getTextRange() {
		return textRange;
	}

	public void setTextRange(TextRange textRange) {
		this.textRange = textRange;
	}

	public List<Flow> getFlows() {
		return flows;
	}

	public void setFlows(List<Flow> flows) {
		this.flows = flows;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getEffort() {
		return effort;
	}

	public void setEffort(String effort) {
		this.effort = effort;
	}

	public String getDebt() {
		return debt;
	}

	public void setDebt(String debt) {
		this.debt = debt;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public Boolean getFromHotspot() {
		return fromHotspot;
	}

	public void setFromHotspot(Boolean fromHotspot) {
		this.fromHotspot = fromHotspot;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(String closeDate) {
		this.closeDate = closeDate;
	}
}
