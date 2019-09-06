package net.boomerangplatform.model;

import static net.boomerangplatform.util.DateUtil.sanityNullDate;
import static net.boomerangplatform.util.ListUtil.sanityEmptyList;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Analysis {

  private String key;

  private Date date;

  private List<Event> events;

  public Analysis() {
    // Do nothing
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Date getDate() {
    return sanityNullDate(date);
  }

  public void setDate(Date date) {
    this.date = sanityNullDate(date);
  }

  public List<Event> getEvents() {
    return sanityEmptyList(events);
  }

  public void setEvents(List<Event> events) {
    this.events = sanityEmptyList(events);
  }
}
