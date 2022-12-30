package bigdatacourse.hw2.studentcode.helpers;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadSafeCounter {
	  private String name;
	  private AtomicInteger count = new AtomicInteger();

	  public ThreadSafeCounter(String name) {
	    this.name = name;
	  }

	  public int incrementAndGet() {
	    int value = count.incrementAndGet();
	    System.out.println(name + ": " + value);
	    return value;
	  }

	  public int decrementAndGet() {
	    int value = count.decrementAndGet();
	    System.out.println(name + ": " + value);
	    return value;
	  }

	  public int get() {
	    int value = count.get();
	    System.out.println(name + ": " + value);
	    return value;
	  }
}