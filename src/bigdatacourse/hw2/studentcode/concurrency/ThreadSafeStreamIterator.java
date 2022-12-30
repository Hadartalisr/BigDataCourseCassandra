package bigdatacourse.hw2.studentcode.concurrency;

import java.util.Iterator;
import java.util.stream.Stream;

public class ThreadSafeStreamIterator<T>{
    private final Iterator<T> iterator;

    public ThreadSafeStreamIterator(Stream<T> stream) {
        this.iterator = stream.iterator();
    }

    public synchronized T next() {
        return iterator.hasNext() ? iterator.next() : null;
    }
}