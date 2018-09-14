package net.boomerangplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Model1 {

	private String sha;
	private Model2 commit;
	private String url;
	private String html_url;
	private String comments_url;
	
	public Model1(){
		
	}
	
	public String getHtml_url() {
		return html_url;
	}

	public void setHtml_url(String html_url) {
		this.html_url = html_url;
	}

	public String getComments_url() {
		return comments_url;
	}

	public void setComments_url(String comments_url) {
		this.comments_url = comments_url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Model2 getCommit() {
		return commit;
	}

	public void setCommit(Model2 commit) {
		this.commit = commit;
	}

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}
	
	
}
