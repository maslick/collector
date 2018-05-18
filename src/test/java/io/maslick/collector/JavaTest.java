package io.maslick.collector;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.maslick.collector.KotlinTestKt.parseDataArrayFromFile;

public class JavaTest {

    private Long window10s = 10000L;

    @Test
    public void testJava() {
        List<String> fromFile = parseDataArrayFromFile("30sec1Hz.txt");
        List<Data> l = fromFile.stream().map(it -> new Data(Double.parseDouble(it.split(" ")[1]), Long.parseLong(it.split(" ")[0]))).collect(Collectors.toList());
        List<List<Data>> ref = Arrays.asList(l.subList(0, 10), l.subList(10, 20), l.subList(20, 30));
        List<List<Data>> underTest = JavaHelper.toListWhile(l, (bucket, i) -> bucket.size() == 0 || i.getTimestamp() - bucket.get(0).getTimestamp() < window10s);
        Assert.assertEquals(ref, underTest);
    }

    @Test
    public void testJavaLazy() {
        List<String> fromFile = parseDataArrayFromFile("30sec1Hz.txt");
        List<Data> l = fromFile.stream()
                .map(str -> new Data(Double.parseDouble(str.split(" ")[1]), Long.parseLong(str.split(" ")[0])))
                .collect(Collectors.toList());
        List<List<Data>> ref = Arrays.asList(l.subList(0, 10), l.subList(10, 20), l.subList(20, 30));
        List<List<Data>> underTest = JavaHelper.toListWhileLazy(l, (bucket, i) -> i.getTimestamp() - bucket.get(0).getTimestamp() < window10s);
        Assert.assertEquals(ref, underTest);
    }
}
