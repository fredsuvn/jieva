package test.xyz.srclab.common.cache

import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import test.xyz.srclab.common.doAssertEquals
import test.xyz.srclab.common.doExpectThrowable
import xyz.srclab.common.cache.Cache
import xyz.srclab.common.cache.weak.WeakCache
import xyz.srclab.common.lang.Computed

/**
 * @author sunqian
 */
object CacheTest {

    @Test(dataProvider = "testCacheProvider")
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
        val v2 = cache.get("2", 1) {
            counter.refreshGet()
            counter
        }
        doAssertEquals(v2?.get(), 2L)
        Thread.sleep(2000)
        cache.get("2", 1) {
            counter.refreshGet()
            counter
        }
        doAssertEquals(v2?.get(), 3L)
        Thread.sleep(2000)
        doAssertEquals(cache.has("1"), true)
        doAssertEquals(cache.hasAll("1", "2"), false)
        doAssertEquals(cache.hasAll(listOf("1", "2")), false)
        doAssertEquals(cache.hasAny("1", "2"), true)
        doAssertEquals(cache.hasAny(listOf("1", "2")), true)
        doAssertEquals(cache.hasAll("1"), true)
        doAssertEquals(cache.hasAll(listOf("1")), true)

        val map1 = cache.getAll("1")
        doAssertEquals(map1, mapOf("1" to counter))
        val map1It = cache.getAll(listOf("1"))
        doAssertEquals(map1It, mapOf("1" to counter))

        val map1Present = cache.getPresent("1", "2")
        doAssertEquals(map1Present, mapOf("1" to counter))
        val map1ItPresent = cache.getPresent(listOf("1", "2"))
        doAssertEquals(map1ItPresent, mapOf("1" to counter))
    }

    @Test(dataProvider = "testCacheProvider")
    fun testPut(cache: Cache<String, Computed<Long>>) {

    }

    @Test(dataProvider = "testCacheProvider")
    fun testExpire(cache: Cache<String, Computed<Long>>) {

    }

    @Test(dataProvider = "testCacheProvider")
    fun testInvalidate(cache: Cache<String, Computed<Long>>) {

    }

    @DataProvider
    fun testCacheProvider(): Array<Array<Cache<String, Computed<Long>>>> {
        return arrayOf(
            arrayOf(WeakCache())
        )
    }
}