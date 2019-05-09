package net.boomerangplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TextRange {

	@JsonProperty("startLine")
	private Integer startLine;
	
	@JsonProperty("endLine")
	private Integer endLine;
	
	@JsonProperty("startOffset")
	private Integer startOffset;
	
	@JsonProperty("endOffset")
	private Integer endOffset;
	
	public TextRange() {
		
	}

	public Integer getStartLine() {
		return startLine;
	}

	public void setStartLine(Integer startLine) {
		this.startLine = startLine;
	}

	public Integer getEndLine() {
		return endLine;
	}

	public void setEndLine(Integer endLine) {
		this.endLine = endLine;
	}

	public Integer getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(Integer startOffset) {
		this.startOffset = startOffset;
	}

	public Integer getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(Integer endOffset) {
		this.endOffset = endOffset;
	}
}
