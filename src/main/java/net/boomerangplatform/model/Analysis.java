package net.boomerangplatform.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Analysis {

	@JsonProperty("key")
	private String key;
	
	@JsonProperty("date")
	private Date date;
	
	@JsonProperty("events")
	private List<Event> events;
	
	public Analysis() {
		
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<Event> getEvents() {
		if (events == null) {
			events = new ArrayList<Event>();
		}
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}
}
