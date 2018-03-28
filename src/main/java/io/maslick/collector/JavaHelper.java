package io.maslick.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class JavaHelper {

    public static <T> List<List<T>> groupDataInWindows(List<T> data, long window, Function<T, Long> lambda) {
        List<List<T>> result = new ArrayList<>();
        List<T> bucket = new ArrayList<>();
        for (T d : data) {
            if (bucket.size() == 0 || lambda.apply(d) - lambda.apply(bucket.get(0)) < window) {
                bucket.add(d);
            } else {
                result.add(bucket);
                bucket = new ArrayList<>();
                bucket.add(d);
            }
        }
        if (!bucket.isEmpty()) result.add(bucket);
        return result;
    }

    public static <T> List<List<T>> toListWhile(List<T> data, Function1<List<T>, T, Boolean> condition) {
        List<List<T>> result = new ArrayList<>();
        List<T> bucket = new ArrayList<>();
        for (T d : data) {
            if (condition.apply(bucket, d)) {
                bucket.add(d);
            } else {
                result.add(bucket);
                bucket = new ArrayList<>();
                bucket.add(d);
            }
        }
        if (!bucket.isEmpty()) result.add(bucket);
        return result;
    }


    public interface Function1<T1, T2, R> {
        R apply(T1 t1, T2 t2);
    }
}
