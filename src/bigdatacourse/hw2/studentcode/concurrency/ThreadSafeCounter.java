package bigdatacourse.hw2.studentcode.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadSafeCounter {
	  private String name;
	  private AtomicInteger count = new AtomicInteger();

	  public ThreadSafeCounter(String name) {
	    this.name = name;
	  }

	  public int incrementAndGet() {
	    int value = count.incrementAndGet();
	    return value;
	  }

	  public int decrementAndGet() {
	    int value = count.decrementAndGet();
	    return value;
	  }

	  public int get() {
	    int value = count.get();
	    return value;
	  }
	  
	  public void print() {
	    System.out.println(name + ": " + get());
	  }
}