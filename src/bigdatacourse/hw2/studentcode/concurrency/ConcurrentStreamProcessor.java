package bigdatacourse.hw2.studentcode.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import bigdatacourse.hw2.studentcode.Processor;

public class ConcurrentStreamProcessor {

	/**
	 * The process method is used to process a stream of objects 
	 * in parallel using multiple threads.
	 * @param stream
	 * @param numberOfThreads
	 * @param processor
	 * @return long - number of processed objects
	 * @throws InterruptedException
	 */
	public static <T> long process(
			Stream<T> stream, 
			int numberOfThreads,
			Processor<T> processor) throws InterruptedException {
		
		ThreadSafeStreamIterator<T> streamIterator = new ThreadSafeStreamIterator<T>(stream);
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
	    ThreadSafeCounter totalCounter = new ThreadSafeCounter("updates");
		
	    IntStream.range(0, numberOfThreads).forEach(i -> executor.submit(() -> {
            while (true) {
                T obj = streamIterator.next();
                if (obj == null) {
                    break;
                }
                try {
                    processor.process(obj);
                } catch (Exception e) {
					System.out.println(e);
                }
                totalCounter.incrementAndGet();
            }
        }));
		
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.HOURS);
		return totalCounter.get();
	}
}
