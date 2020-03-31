package test.xyz.srclab.common.collection

import com.google.common.base.Stopwatch
import org.testng.annotations.Test
import xyz.srclab.common.collection.CollectionHelper
import xyz.srclab.common.test.asserts.AssertHelper
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

object MapTest {

    private val size = 1000

    @Test
    fun testFastFixedMap() {
        val keys = createKeys()
        val fastMap: MutableMap<Int, Int> = CollectionHelper.newFastFixedKeysMap(keys)
        val hashMap = HashMap<Int, Int>()
        val concurrentMap = ConcurrentHashMap<Int, Int>()
        for (key in keys) {
            val value = Random.nextInt()
            fastMap[key] = value
            hashMap[key] = value
            concurrentMap[key] = value
        }
        for (key in keys) {
            val fastValue = fastMap[key]
            val hashValue = hashMap[key]
            val concurrentValue = concurrentMap[key]
            AssertHelper.printAssert(fastValue, hashValue)
        }

        AssertHelper.printAssertThrows(UnsupportedOperationException::class.java) {
            fastMap[size * 2] = 0
        }

        // Performance:
        val times = 1000;
        val stopwatch = Stopwatch.createStarted()
        for (i in 1..times) {
            for (i in 1..size) {
                fastMap[i]
            }
        }
        println("fastMap cost: ${stopwatch.elapsed().toMillis()}")
        val stopwatch2 = Stopwatch.createStarted()
        for (i in 1..times) {
            for (i in 1..size) {
                hashMap[i]
            }
        }
        println("hashMap cost: ${stopwatch2.elapsed().toMillis()}")
        val stopwatch3 = Stopwatch.createStarted()
        for (i in 1..times) {
            for (i in 1..size) {
                concurrentMap[i]
            }
        }
        println("concurrentMap cost: ${stopwatch3.elapsed().toMillis()}")

        var start = false
        var testMap: Map<Int, Int> = fastMap
        val threadNumber = 100
        var threadOkCount = AtomicInteger(0)
        for (i in 1..threadNumber) {
            Thread {
                while (true) {
                    if (start) {
                        for (i in 1..times) {
                            for (i in 1..size) {
                                testMap[i]
                            }
                        }
                        threadOkCount.incrementAndGet()
                        return@Thread
                    }
                    Thread.sleep(1)
                }
            }.start()
        }
        Thread.sleep(500)
        val stopwatch4 = Stopwatch.createStarted()
        start = true;
        while (threadOkCount.get() < 100) {
            Thread.sleep(1)
        }
        println("fastMap thread cost: ${stopwatch4.elapsed().toMillis()}")
        start = false
        threadOkCount.set(0)
        testMap = concurrentMap
        for (i in 1..threadNumber) {
            Thread {
                while (true) {
                    if (start) {
                        for (i in 1..times) {
                            for (i in 1..size) {
                                testMap[i]
                            }
                        }
                        threadOkCount.incrementAndGet()
                        return@Thread
                    }
                    Thread.sleep(1)
                }
            }.start()
        }
        Thread.sleep(500)
        val stopwatch5 = Stopwatch.createStarted()
        start = true;
        while (threadOkCount.get() < 100) {
            Thread.sleep(1)
        }
        println("concurrentMap thread cost: ${stopwatch5.elapsed().toMillis()}")
    }

    private fun createKeys(): MutableSet<Int> {
        val set = HashSet<Int>()
        for (i in 1..size) {
            set.add(i)
        }
        return set
    }
}