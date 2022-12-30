package bigdatacourse.hw2.studentcode.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Review {
	@JsonProperty(required = true)
	public long unixReviewTime;

	@JsonProperty(required = true)
	public String asin;

	@JsonProperty(required = true)
	public String reviewerID;
	public String reviewerName;
	public float overall;
	public String summary;
	public String reviewText;
}
