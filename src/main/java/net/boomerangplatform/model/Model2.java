package net.boomerangplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Model2 {
    
    private String requestId;

    public String getRequestId () {
        return requestId;
    }

    public void setRequestId (String requestId) {
        this.requestId = requestId;
    }

}
			
			