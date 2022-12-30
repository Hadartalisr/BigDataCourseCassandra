package bigdatacourse.hw2.studentcode.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {
	@JsonProperty(required = true)
	public String asin;

	public String title;
	public String imUrl;
	public String[][] categories = { {} };
	public String description;
}
