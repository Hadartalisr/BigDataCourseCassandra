package bigdatacourse.hw2.studentcode.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {
	public String asin;
	public String title;
	public String imUrl;
	public String[][] categories;
	public String description;
}
