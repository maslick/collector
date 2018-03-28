package io.maslick.arrayer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class JavaHelper {

    public static <T> List<List<T>> groupDataInWindows(List<T> data, long window, Function<T, Long> lambda) {
        List<List<T>> ret = new ArrayList<>();
        List<T> bucket = new ArrayList<>();
        for (T d : data) {
            if (bucket.size() == 0 || lambda.apply(d) - lambda.apply(bucket.get(0)) < window) {
                bucket.add(d);
            } else {
                ret.add(bucket);
                bucket = new ArrayList<>();
                bucket.add(d);
            }
        }
        if (!bucket.isEmpty()) ret.add(bucket);
        return ret;
    }
}