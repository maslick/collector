package io.maslick.collector

import com.github.davidmoten.rx.Transformers
import io.maslick.collector.Helper.randomInteger
import io.maslick.collector.KotlinHelper.toListWhile
import io.maslick.collector.KotlinHelper.toListWhileLazy
import org.junit.Assert
import org.junit.Test
import rx.Observable


class ExampleUnitTest {

    val window10s = 10000L

    @Test
    fun testKotlin() {
        val l = parseDataArrayFromFile("30sec1Hz.txt").map { Data(it.split(" ")[1].toDouble(), it.split(" ")[0].toLong()) }
        val ref = listOf(l.subList(0, 10), l.subList(10, 20), l.subList(20, 30))
        val underTest = l.toListWhile({ bucket, i ->  bucket.isEmpty() || i.timestamp!! - bucket[0].timestamp!! < window10s })
        Assert.assertEquals(ref, underTest)
    }

    @Test
    fun testJava() {
        val l = parseDataArrayFromFile("30sec1Hz.txt").map { Data(it.split(" ")[1].toDouble(), it.split(" ")[0].toLong()) }
        val ref = listOf(l.subList(0, 10), l.subList(10, 20), l.subList(20, 30))
        val underTest = JavaHelper.toListWhile(l, { bucket, i ->  bucket.isEmpty() || i.timestamp!! - bucket[0].timestamp!! < window10s })
        Assert.assertEquals(ref, underTest)
    }

    @Test
    fun compareAsyncWithSync() {
        val data = createTestDataArray(seconds = 60, freq = 1000)
        val sync = data.toListWhile({ bucket, i ->  bucket.isEmpty() || i.timestamp!! - bucket[0].timestamp!! < window10s })
        val async = Observable.from(data).compose(groupDataInWindows(window10s)).toList().toBlocking().single().toList()
        Assert.assertEquals(async, sync)
    }

    @Test
    fun testLazy() {
        data class Data(var id: Long? = null, var name: String? = null)
        val list = listOf(
                Data(id = 1, name = "Michael Jordan"),
                Data(id = 2, name = "Lebron James"),
                Data(id = 3, name = "Dwight Howard"),
                Data(id = 4, name = "Nate Robinson"),
                Data(id = 5, name = "Donovan Mitchel")
        )

        val result = list.toListWhileLazy { bucket, d -> d.id!! - bucket.first().id!! < 2 }

        val ref = listOf(
                listOf("Michael Jordan", "Lebron James"),
                listOf("Dwight Howard", "Nate Robinson"),
                listOf("Donovan Mitchel")
        )

        Assert.assertEquals(ref, result.map { it.map { it.name }})
    }

    private fun createTestDataArray(seconds: Int, freq: Int): List<Data> {
        val list = mutableListOf<Data>()
        val currentTime = System.currentTimeMillis()
        val delta = (1000.0/freq).toInt()

        for (i in 0 until seconds*freq)
            list.add(Data(randomInteger(2, 10) * 1.0 + randomInteger(1,10)/10.0, currentTime + i*delta))

        return list.toList()
    }

    private fun parseDataArrayFromFile(filename: String): List<String> {
        return ClassLoader.getSystemResourceAsStream(filename).bufferedReader().lineSequence().toList()
    }

    private fun groupDataInWindows(window: Long): Observable.Transformer<Data, List<Data>> {
        return Transformers.toListWhile<Data> { data, i -> data.isEmpty() || i.timestamp!! - data[0].timestamp!! < window }
    }
}

data class Data(var ee: Double? = null, var timestamp: Long? = null)
