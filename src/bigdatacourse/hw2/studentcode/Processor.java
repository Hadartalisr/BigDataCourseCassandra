package bigdatacourse.hw2.studentcode;

@FunctionalInterface
public interface Processor<T> {
    void process(T t);
}