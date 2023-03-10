package bigdatacourse.hw2.studentcode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.core.metadata.schema.TableMetadata;
import com.datastax.oss.driver.shaded.guava.common.util.concurrent.RateLimiter;

import bigdatacourse.hw2.HW2API;
import bigdatacourse.hw2.studentcode.concurrency.ConcurrentStreamProcessor;
import bigdatacourse.hw2.studentcode.helpers.Consts;
import bigdatacourse.hw2.studentcode.helpers.IOHelpers;
import bigdatacourse.hw2.studentcode.helpers.JsonHelpers;
import bigdatacourse.hw2.studentcode.models.Item;
import bigdatacourse.hw2.studentcode.models.Review;

public class HW2StudentAnswer implements HW2API {


	// Cassandra session
	private CqlSession session;
	private String keyspace;

	// prepared statements
	PreparedStatement pstmtInsertTableItems;
	PreparedStatement pstmtSelectTableItems;
	PreparedStatement pstmtInsertTableItemReviews;
	PreparedStatement pstmtSelectTableItemReviews;
	PreparedStatement pstmtInsertTableUserReviews;
	PreparedStatement pstmtSelectTableUserReviews;

	// internals
	private final int conccurentExecutions = 250; 
	private final JsonHelpers jsonHelpers = new JsonHelpers();
	private RateLimiter rateLimiter;

	@Override
	public void connect(String pathAstraDBBundleFile, String username, String password, String keyspace) {
		if (session != null) {
			System.out.println("ERROR - cassandra is already connected");
			return;
		}

		System.out.println("Initializing connection to Cassandra...");

		this.session = CqlSession.builder().withCloudSecureConnectBundle(Paths.get(pathAstraDBBundleFile))
				.withAuthCredentials(username, password).withKeyspace(keyspace).build();

		System.out.println("Initializing connection to Cassandra... Done");
		this.keyspace = keyspace;
	}

	@Override
	public void close() {
		if (session == null) {
			System.out.println("Cassandra connection is already closed");
			return;
		}

		System.out.println("Closing Cassandra connection...");
		session.close();
		System.out.println("Closing Cassandra connection... Done");
	}

	@Override
	public void createTables() {
		if (!tableExists(Consts.TABLE_ITEMS)) {
			session.execute(Consts.CQL_CREATE_ITEMS_TABLE);
			System.out.println("created table: " + Consts.TABLE_ITEMS);	
		}
		if (!tableExists(Consts.TABLE_ITEM_REVIEWS)) {
			session.execute(Consts.CQL_CREATE_ITEM_REVIEWS_TABLE);
			System.out.println("created table: " + Consts.TABLE_ITEM_REVIEWS);	
		}
		if (!tableExists(Consts.TABLE_USER_REVIEWS)) {
			session.execute(Consts.CQL_CREATE_USER_REVIEWS_TABLE);
			System.out.println("created table: " + Consts.TABLE_USER_REVIEWS);	
		}
	}
	
	private boolean tableExists(String tableName) {
	    KeyspaceMetadata keyspaceMetadata = session.getMetadata().getKeyspace(keyspace).orElse(null);
	    if (keyspaceMetadata == null) {
	      return false;
	    }
	    TableMetadata tableMetadata = keyspaceMetadata.getTable(tableName).orElse(null);
	    return tableMetadata != null;
	  }

	@Override
	public void initialize() {
		pstmtInsertTableItems = session.prepare(Consts.CQL_INSERT_TABLE_ITEMS);
		pstmtSelectTableItems = session.prepare(Consts.CQL_SELECT_TABLE_ITEM);
		pstmtInsertTableItemReviews = session.prepare(Consts.CQL_INSERT_TABLE_ITEM_REVIEWS);
		pstmtSelectTableItemReviews = session.prepare(Consts.CQL_SELECT_TABLE_ITEM_REVIEWS);
		pstmtInsertTableUserReviews = session.prepare(Consts.CQL_INSERT_TABLE_USER_REVIEWS);
		pstmtSelectTableUserReviews = session.prepare(Consts.CQL_SELECT_TABLE_USER_REVIEWS);
		System.out.println("Successfully initialized the logic");
	}

	@Override
	public void loadItems(String pathItemsFile) throws Exception {
		//TODO use na as null
		Stream<String> itemsAsJsonStringsStream;
		try {
			itemsAsJsonStringsStream = IOHelpers.getFileStream(pathItemsFile);
		} catch (FileNotFoundException fileNotFoundException) {
			return;
		}
		
		long processedItems = ConcurrentStreamProcessor.process(itemsAsJsonStringsStream, conccurentExecutions,
				new Processor<String>() {

					@Override
					public void process(String itemAsJsonString) {
						Item item;
						try {
							item = jsonHelpers.deserialize(itemAsJsonString, Item.class);
						} catch (IOException e) {
							System.out.println(e);
							return;
						}
						AlignItem(item);
						InsertItem(item);					
					}
				
			});
		
		System.out.println("processedItems : "+ processedItems);
	}
	
	private void AlignItem(Item item) {
		item.asin = (item.asin != null) ? item.asin : Consts.NOT_AVAILABLE_VALUE ;
		item.title = (item.title != null) ? item.title : Consts.NOT_AVAILABLE_VALUE ;
		item.imUrl = (item.imUrl != null) ? item.imUrl : Consts.NOT_AVAILABLE_VALUE ;
		item.description = (item.description != null) ? item.description : Consts.NOT_AVAILABLE_VALUE ;
	}

	private void InsertItem(Item item) {
		HashSet<String> categoriesSet = new HashSet<>();
		for (String[] categoriesArr : item.categories) {
			for (String category : categoriesArr) {
				categoriesSet.add(category);				
			}
		}

		try {
			BoundStatement bstmt = pstmtInsertTableItems.bind().setString(0, item.asin).setString(1, item.title)
				.setString(2, item.imUrl).setSet(3, categoriesSet, String.class).setString(4, item.title); // NOTE - for

			session.execute(bstmt);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	


	@Override
	public void loadReviews(String pathReviewsFile) throws Exception {
		Stream<String> reviewsAsJsonStringsStream;
		try {
			reviewsAsJsonStringsStream = IOHelpers.getFileStream(pathReviewsFile);
		} catch (FileNotFoundException fileNotFoundException) {
			return;
		}

		ConcurrentStreamProcessor.process(reviewsAsJsonStringsStream, conccurentExecutions,
			new Processor<String>() {

				@Override
				public void process(String reviewAsJsonString) {
					Review review;
					try {
						review = jsonHelpers.deserialize(reviewAsJsonString, Review.class);
					} catch (IOException e) {
						System.out.println(e);
						return;
					}
					AlignReview(review);
					try {
						InsertReview(review, true);		
					} catch (Exception e) {
						System.out.println(e);
					}
				}
			
		});
	}

	private void AlignReview(Review review) {
		review.reviewerName = (review.reviewerName != null) ? review.reviewerName : Consts.NOT_AVAILABLE_VALUE ;
		review.summary = (review.summary != null) ? review.summary : Consts.NOT_AVAILABLE_VALUE ;
		review.reviewText = (review.reviewText != null) ? review.reviewText : Consts.NOT_AVAILABLE_VALUE ;
	}

	private void InsertReview(Review review, boolean async) {
		BoundStatement bstmtInsertItemReview = pstmtInsertTableItemReviews.bind()
			.setString(0, review.asin)
			.setInstant(1,Instant.ofEpochMilli(review.unixReviewTime))
			.setString(2, review.reviewerID)
			.setString(3, review.reviewerName)
			.setFloat(4, review.overall)
			.setString(5, review.summary)
			.setString(6, review.reviewText);
		
		session.execute(bstmtInsertItemReview);

		
		BoundStatement bstmtInsertUserReview = pstmtInsertTableUserReviews.bind()
				.setString(0, review.asin)
				.setInstant(1,Instant.ofEpochMilli(review.unixReviewTime))
				.setString(2, review.reviewerID)
				.setString(3, review.reviewerName)
				.setFloat(4, review.overall)
				.setString(5, review.summary)
				.setString(6, review.reviewText);
 
		session.execute(bstmtInsertUserReview);
	}
	


	@Override
	public void item(String asin) {
		BoundStatement bstmt = pstmtSelectTableItems.bind().setString(0, asin);

		ResultSet rs = session.execute(bstmt);
		Row row = rs.one();

		if (row == null) {
			// required format - if the asin does not exists return this value
			System.out.println("not exists");
			return;
		}

		// required format - example for asin B005QB09TU
		System.out.println("asin: " + row.getString("asin"));
		System.out.println("title: " + row.getString("title"));
		System.out.println("image: " + row.getString("imUrl"));
		System.out.println("categories: " + row.getSet("categories", String.class));
		System.out.println("description: " + row.getString("description"));
	}

	@Override
	public void userReviews(String reviewerID) {
		BoundStatement bstmt = pstmtSelectTableUserReviews.bind().setString(0, reviewerID);
		ResultSet rs = session.execute(bstmt);
		printResults(rs);
	}

	@Override
	public void itemReviews(String asin) {
		BoundStatement bstmt = pstmtSelectTableItemReviews.bind().setString(0, asin);
		ResultSet rs = session.execute(bstmt);
		printResults(rs);

	}
	
	private void printResults(ResultSet rs) {
		List<Row> results = rs.all();
		int resultsSize = results.size() ;
		results.forEach(row -> {
			System.out.println(
				"time: " + row.getInstant("ts") + ", " +
				"asin: " + row.getString("asin") + ", " +
				"reviewerID: " + row.getString("reviewer_id")  + ", " +
				"reviewerName: " + row.getString("reviewer_name") + ", " +
				"rating: " + (int)row.getFloat("rating") + ", " +
				"summary: " + row.getString("summary") + ", " +
				"reviewText: " + row.getString("review_text")
			);
		});

		System.out.println("total reviews: " + resultsSize);
	}

}
