package io.maslick.arrayer

import java.text.SimpleDateFormat
import java.util.*


object KotlinHelper {

    fun <T> List<T>.groupInWindows(window: Long, lambda: (T) -> Long): List<List<T>> {
        val ret = mutableListOf<List<T>>()
        var bucket = mutableListOf<T>()
        this.forEach { d ->
            if (bucket.isEmpty() || lambda.invoke(d) - lambda.invoke(bucket[0]) < window)
                bucket.add(d)
            else {
                ret.add(bucket)
                bucket = mutableListOf()
                bucket.add(d)
            }
        }
        if (bucket.isNotEmpty()) ret.add(bucket)
        return ret.toList()
    }

    fun <T> List<T>.toListWhile(condition: (List<T>, T) -> Boolean): List<List<T>> {
        val result = mutableListOf<List<T>>()
        var bucket = mutableListOf<T>()
        this.forEach { d ->
            if (condition.invoke(bucket, d))
                bucket.add(d)
            else {
                result.add(bucket)
                bucket = mutableListOf()
                bucket.add(d)
            }
        }
        if (bucket.isNotEmpty()) result.add(bucket)
        return result.toList()
    }

    fun formatDate(timestamp: Long?): String {
        val formatter = SimpleDateFormat("HH:mm:ss")
        return formatter.format(Date(timestamp!!))
    }
}