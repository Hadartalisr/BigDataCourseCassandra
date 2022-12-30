package bigdatacourse.hw2.studentcode.helpers;

public class Consts {

	public static final String TABLE_USER_REVIEWS = "user_reviews";

	public static final String TABLE_ITEM_REVIEWS = "item_reviews";

	// TABLE_ITEMS

	public static final String TABLE_ITEMS = "items";

	public static final String CQL_CREATE_ITEMS_TABLE = 
			"CREATE TABLE " + TABLE_ITEMS + "(" + 
					"asin text," + 
					"title text," + 
					"imUrl text," + 
					"categories set<text>," + 
					"description text," + 
				"PRIMARY KEY (asin)"
			+ ") ";

	public static final String CQL_INSERT_TABLE_ITEMS = 
			"INSERT INTO " + TABLE_ITEMS
			+ "(asin, title, imUrl, categories, description) "
			+ "VALUES(?, ?, ?, ?, ?) ";
	
	public static final String CQL_SELECT_TABLE_ITEM = 
			"SELECT * FROM " + TABLE_ITEMS	+ " WHERE asin = ?";

}
