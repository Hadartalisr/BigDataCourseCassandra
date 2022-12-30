package bigdatacourse.hw2.studentcode.helpers;

public class Consts {

	// general 
	public static final String NOT_AVAILABLE_VALUE = "na";

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

	
	// TABLE_ITEM_REVIEWS
	public static final String TABLE_ITEM_REVIEWS = "item_reviews";

	public static final String CQL_CREATE_ITEM_REVIEWS_TABLE = 
			"CREATE TABLE " + TABLE_ITEM_REVIEWS + "(" + 
				"asin text," + 
				"ts timestamp," + 
				"reviewer_id text," + 
				"reviewer_name text," + 
				"rating float," + 
				"summary text," + 
				"review_text text,"
			+ "PRIMARY KEY ((asin), ts, reviewer_id))" + 
			"WITH CLUSTERING ORDER BY (ts DESC, reviewer_id ASC) ";

	public static final String CQL_INSERT_TABLE_ITEM_REVIEWS = 
			"INSERT INTO " + TABLE_ITEM_REVIEWS
			+ "(asin, ts, reviewer_id, reviewer_name, rating, summary, review_text) "
			+ "VALUES(?, ?, ?, ?, ?, ?, ?) ";
	
	public static final String CQL_SELECT_TABLE_ITEM_REVIEWS = 
			"SELECT * FROM " + TABLE_ITEM_REVIEWS	+ 
			" WHERE asin = ? " +
			" ORDER BY ts DESC";
	
	//TABLE_USER_REVIEWS
	public static final String TABLE_USER_REVIEWS = "user_reviews";

	public static final String CQL_CREATE_USER_REVIEWS_TABLE = 
			"CREATE TABLE " + TABLE_USER_REVIEWS + "(" + 
				"asin text," + 
				"ts timestamp," + 
				"reviewer_id text," + 
				"reviewer_name text," + 
				"rating float," + 
				"summary text," + 
				"review_text text,"
			+ "PRIMARY KEY ((reviewer_id), ts, asin))" + 
			"WITH CLUSTERING ORDER BY (ts DESC, asin ASC) ";

	public static final String CQL_INSERT_TABLE_USER_REVIEWS = 
			"INSERT INTO " + TABLE_USER_REVIEWS
			+ "(asin, ts, reviewer_id, reviewer_name, rating, summary, review_text) "
			+ "VALUES(?, ?, ?, ?, ?, ?, ?) ";
	
	public static final String CQL_SELECT_TABLE_USER_REVIEWS = 
			"SELECT * FROM " + TABLE_USER_REVIEWS	+ 
			" WHERE reviewer_id = ? " +
			" ORDER BY ts DESC";
	
}
