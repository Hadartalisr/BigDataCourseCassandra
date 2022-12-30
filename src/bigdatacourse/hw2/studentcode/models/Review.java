package bigdatacourse.hw2.studentcode.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Review {
	@JsonProperty(required = true)
	private long unixReviewTime;

	@JsonProperty(required = true)
	private String asin;

	private String reviewerID;
	private String reviewerName;
	private double overall;
	private String summary;
	private String reviewText;
}
