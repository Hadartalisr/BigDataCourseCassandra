package bigdatacourse.hw2.studentcode.helpers;

import java.util.concurrent.Semaphore;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;

public class RateLimiter {
  private Semaphore semaphore;
  private CqlSession session;
  private ThreadSafeCounter counter = new ThreadSafeCounter("RateLimiter");

  public RateLimiter(CqlSession session, int maxRate) {
    this.semaphore = new Semaphore(maxRate, true);
    this.session = session;
  }

  public void execute(BoundStatement bstmt, boolean async) throws InterruptedException {
    semaphore.acquire();
    counter.incrementAndGet();
    try {
		if (async)
			session.executeAsync(bstmt);
		else
			session.execute(bstmt);
    } finally {
      semaphore.release();
      counter.decrementAndGet();
    }
  }
}