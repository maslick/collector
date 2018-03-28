package si.ijs.heartman.iotool.arband

import com.github.davidmoten.rx.Transformers
import io.maslick.arrayer.Helper.randomInteger
import org.junit.Assert
import org.junit.Test
import rx.Observable
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class ExampleUnitTest {

    val window10s = 10000L

    @Test
    fun calcCalories() {
        val data = createTestDataArray(seconds = 60*60, freq = 100)
        println("data size: ${data.size}")

        val list = Observable
                .from(data)
                .compose(groupDataInWindows(window10s))
                .map { it.map { it.ee!! }.average() }
                .toList().toBlocking().single().toList()

        println("list size: ${list.size}")

        val mass = 70
        val calories = list.map { it!! * mass / (3600 / (window10s / 1000)) }.sum().roundToInt()

        println("Burnt: $calories kkal")
    }

    @Test
    fun saveToFile() {
        val data = createTestDataArray(seconds = 20, freq = 5)
        File("20sec5Hz.txt").printWriter().use { out ->
            data.forEach {
                out.println("${it.timestamp} ${it.ee}")
            }
        }
    }

    @Test
    fun readDataFromFile() {
        val l = parseDataArrayFromFile("30sec1Hz.txt").map { Data(it.split(" ")[1].toDouble(), it.split(" ")[0].toLong()) }
        l.forEach { println("${formatDate(it.timestamp)} : ${it.ee}") }
    }

    @Test
    fun testtt() {
        val l = parseDataArrayFromFile("30sec1Hz.txt").map { Data(it.split(" ")[1].toDouble(), it.split(" ")[0].toLong()) }
        val ethalon = listOf(l.subList(0, 10), l.subList(10, 20), l.subList(20, 30))
        val underTest = l.groupInWindows(window10s, { it.timestamp!! })
        Assert.assertEquals(ethalon[0].size, underTest[0].size)
        Assert.assertEquals(ethalon[1].size, underTest[1].size)
        Assert.assertEquals(ethalon[2].size, underTest[2].size)
        Assert.assertEquals(ethalon[0], underTest[0])
        Assert.assertEquals(ethalon[1], underTest[1])
        Assert.assertEquals(ethalon[2], underTest[2])
    }

    @Test
    fun compareAsyncWithSync() {
        val data = createTestDataArray(seconds = 60, freq = 1)
        val groupedSync = data.groupInWindows(window10s, {it.timestamp!!})

        val groupedAvg = Observable.from(groupedSync)
                .map { it.map { it.ee!! }.average() }
                .toList()
                .toBlocking()
                .single()
                .toList()

        val list = Observable.from(data)
                .compose(groupDataInWindows(window10s))
                .map { it.map { it.ee!! }.average() }
                .toList()
                .toBlocking()
                .single()
                .toList()


        Assert.assertEquals(list, groupedAvg)
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

    private fun List<Double>.average(): Double? {
        return this.sum()/this.size
    }

    private fun formatDate(timestamp: Long?): String {
        val formatter = SimpleDateFormat("HH:mm:ss")
        return formatter.format(Date(timestamp!!))
    }

    private fun List<Data>.groupInWindows(window: Long): List<List<Data>> {
        val ret = mutableListOf<List<Data>>()
        var bucket = mutableListOf<Data>()
        this.forEach { i:Data ->
            if (bucket.isEmpty() || i.timestamp!! - bucket[0].timestamp!! < window)
                bucket.add(i)
            else {
                ret.add(bucket)
                bucket = mutableListOf()
            }
        }
        if (bucket.isNotEmpty()) ret.add(bucket)
        return ret.toList()
    }

    private fun <T> List<T>.groupInWindows(window: Long, lambda: (T) -> Long): List<List<T>> {
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

data class Data(var ee: Double? = null, var timestamp: Long? = null)
