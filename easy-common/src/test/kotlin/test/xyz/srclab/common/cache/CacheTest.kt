package test.xyz.srclab.common.cache

import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import test.xyz.srclab.common.doAssertEquals
import test.xyz.srclab.common.doExpectThrowable
import xyz.srclab.common.cache.Cache
import xyz.srclab.common.cache.concurent.ConcurrentCache
import xyz.srclab.common.cache.threadlocal.ThreadLocalCache
import xyz.srclab.common.cache.weak.WeakCache
import xyz.srclab.common.lang.Computed
import java.time.Duration

/**
 * @author sunqian
 */
object CacheTest {

    private const val SLEEP_TIME = 1100L

    private fun sleep() {
        Thread.sleep(SLEEP_TIME)
    }

    @Test(dataProvider = "testCacheDataProvider")
    fun testGet(cache: Cache<String, Computed<Long>>) {
        doAssertEquals(cache.has("1"), false)
        doAssertEquals(cache.hasAll("1", "2"), false)
        doAssertEquals(cache.hasAll(listOf("1", "2")), false)
        doAssertEquals(cache.hasAny("1", "2"), false)
        doAssertEquals(cache.hasAny(listOf("1", "2")), false)
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("1")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.getAll("1", "2", "3")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.getAll(listOf("1", "2", "3"))
        }

        val counter = Computed.withCounter(100000)
        counter.get()

        val v1 = cache.get("1") {
            counter.refreshGet()
            counter
        }
        doAssertEquals(v1?.get(), 1L)
        val v1NonNull = cache.getNonNull("1")
        doAssertEquals(v1NonNull.get(), 1L)

        doAssertEquals(cache.hasAll("1", "2"), false)
        doAssertEquals(cache.hasAll(listOf("1", "2")), false)
        val map1Present = cache.getPresent("1", "2")
        doAssertEquals(map1Present, mapOf("1" to counter))
        val map1ItPresent = cache.getPresent(listOf("1", "2"))
        doAssertEquals(map1ItPresent, mapOf("1" to counter))

        val v2 = cache.get("2", 1) {
            counter.refreshGet()
            counter
        }
        doAssertEquals(v2?.get(), 2L)

        doExpectThrowable(NoSuchElementException::class.java) {
            cache.getNonNull("3")
        }
        doExpectThrowable(NullPointerException::class.java) {
            cache.getNonNull("3") {
                null
            }
        }
        doExpectThrowable(NullPointerException::class.java) {
            cache.getNonNull("3")
        }
        doExpectThrowable(NullPointerException::class.java) {
            cache.getNonNull("4", 1) {
                null
            }
        }
        doAssertEquals(cache.get("4"), null)

        sleep()
        cache.get("2", 1) {
            counter.refreshGet()
            counter
        }
        doAssertEquals(v2?.get(), 3L)
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("4")
        }

        doAssertEquals(cache.has("1"), true)
        doAssertEquals(cache.hasAny("1", "2"), true)
        doAssertEquals(cache.hasAny(listOf("1", "2")), true)
        doAssertEquals(cache.hasAll("1"), true)
        doAssertEquals(cache.hasAll(listOf("1")), true)

        val map1 = cache.getAll("1")
        doAssertEquals(map1, mapOf("1" to counter))
        val map1It = cache.getAll(listOf("1"))
        doAssertEquals(map1It, mapOf("1" to counter))
    }

    @Test(dataProvider = "testCacheDataProvider")
    fun testPut(cache: Cache<String, Computed<Long>>) {
        val counter = Computed.withCounter(100000)
        counter.get()

        cache.put("1", counter)
        cache.put("2", counter, 1)
        cache.putAll(mapOf("3" to counter))
        cache.putAll(mapOf("4" to counter), 1)
        cache.putAll(mapOf("5" to counter), Duration.ofSeconds(1))
        doAssertEquals(cache.get("1"), counter)
        doAssertEquals(cache.get("3"), counter)
        sleep()
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("2")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("4")
        }
    }

    @Test(dataProvider = "testCacheExpireDataProvider")
    fun testExpire(cache: Cache<String, Computed<Long>>) {
        val counter = Computed.withCounter(100000)
        counter.get()

        cache.put("1", counter, 100)
        cache.put("2", counter, Duration.ofSeconds(100))
        cache.put("3", counter, 100)
        cache.put("4", counter, Duration.ofSeconds(100))
        cache.put("5", counter, 100)
        cache.put("6", counter, Duration.ofSeconds(100))
        cache.put("7", counter, 100)
        cache.put("8", counter, Duration.ofSeconds(100))
        cache.put("9", counter, 100)
        cache.put("10", counter, Duration.ofSeconds(100))
        cache.put("11", counter, 100)
        cache.put("12", counter, Duration.ofSeconds(100))
        cache.put("13", counter, 100)
        cache.put("14", counter, Duration.ofSeconds(100))
        cache.put("15", counter, 100)
        cache.put("16", counter, Duration.ofSeconds(100))
        cache.put("17", counter, 100)
        cache.put("18", counter, Duration.ofSeconds(100))
        doAssertEquals(cache.get("1"), counter)
        doAssertEquals(cache.get("2"), counter)
        doAssertEquals(cache.get("3"), counter)
        doAssertEquals(cache.get("4"), counter)
        doAssertEquals(cache.get("5"), counter)
        doAssertEquals(cache.get("6"), counter)
        doAssertEquals(cache.get("7"), counter)
        doAssertEquals(cache.get("8"), counter)
        doAssertEquals(cache.get("9"), counter)
        doAssertEquals(cache.get("10"), counter)
        doAssertEquals(cache.get("11"), counter)
        doAssertEquals(cache.get("12"), counter)
        doAssertEquals(cache.get("13"), counter)
        doAssertEquals(cache.get("14"), counter)
        doAssertEquals(cache.get("15"), counter)
        doAssertEquals(cache.get("16"), counter)
        doAssertEquals(cache.get("17"), counter)
        doAssertEquals(cache.get("18"), counter)

        cache.expire("1")
        cache.expire("2", 1)
        cache.expireAll("3", "4")
        cache.expireAll(1, "5", "6")
        cache.expireAll(listOf("7", "8"), 1)
        cache.expireAll(listOf("9", "10"), Duration.ofSeconds(1))
        cache.expireAll(listOf("11", "12"))
        cache.expire("13", Duration.ofSeconds(1))
        cache.expireAll(Duration.ofSeconds(1), "14", "15", "16")
        cache.invalidateAll(listOf("17", "18"))

        sleep()

        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("1")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("2")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("3")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("4")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("5")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("6")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("7")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("8")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("9")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("10")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("11")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("12")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("13")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("14")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("15")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("16")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("17")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("18")
        }
    }

    @Test(dataProvider = "testCacheDataProvider")
    fun testInvalidate(cache: Cache<String, Computed<Long>>) {
        val counter = Computed.withCounter(100000)
        counter.get()

        cache.put("1", counter)
        doAssertEquals(cache.get("1"), counter)
        cache.invalidate("1")
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("1")
        }

        cache.put("1", counter)
        cache.put("2", counter)
        doAssertEquals(cache.get("1"), counter)
        doAssertEquals(cache.get("2"), counter)
        cache.invalidateAll("1", "2")
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("1")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("2")
        }

        cache.put("1", counter)
        cache.put("2", counter)
        doAssertEquals(cache.get("1"), counter)
        doAssertEquals(cache.get("2"), counter)
        cache.invalidateAll()
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("1")
        }
        doExpectThrowable(NoSuchElementException::class.java) {
            cache.get("2")
        }
    }

    @DataProvider
    fun testCacheDataProvider(): Array<Array<Cache<String, Computed<Long>>>> {
        return arrayOf(
            arrayOf(WeakCache()),
            arrayOf(WeakCache(true)),
            arrayOf(ThreadLocalCache()),
            arrayOf(ConcurrentCache()),
            arrayOf(ConcurrentCache(64))
        )
    }

    @DataProvider
    fun testCacheExpireDataProvider(): Array<Array<Cache<String, Computed<Long>>>> {
        return arrayOf(
            arrayOf(WeakCache(Duration.ofSeconds(1))),
            arrayOf(WeakCache(Duration.ofSeconds(1), true)),
            arrayOf(ThreadLocalCache(Duration.ofSeconds(1))),
            arrayOf(ConcurrentCache(Duration.ofSeconds(1))),
            arrayOf(ConcurrentCache(Duration.ofSeconds(1), 64))
        )
    }
}