package io.maslick.arrayer

object KotlinHelper {

    fun <T> List<T>.groupInWindows(window: Long, lambda: (T) -> Long): List<List<T>> {
        val ret = mutableListOf<List<T>>()
        var bucket = mutableListOf<T>()
        this.forEach { i ->
            if (bucket.isEmpty() || lambda.invoke(i) - lambda.invoke(bucket[0]) < window)
                bucket.add(i)
            else {
                ret.add(bucket)
                bucket = mutableListOf()
                bucket.add(i)
            }
        }
        if (bucket.isNotEmpty()) ret.add(bucket)
        return ret.toList()
    }
}