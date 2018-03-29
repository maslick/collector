package io.maslick.collector;

public interface Function1<T1, T2, R> {
    R apply(T1 t1, T2 t2);
}
