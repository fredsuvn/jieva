package test.xyz.srclab.common.cache

import org.testng.annotations.Test
import xyz.srclab.test.doAssertEquals
import xyz.srclab.common.cache.Cache
import xyz.srclab.common.cache.concurent.ConcurrentCache
import kotlin.random.Random

/**
 * @author sunqian
 */
object ConcurrentCacheTest {

    private val cache = ConcurrentCache<String, String>()

    @Test(
        enabled = false,
        invocationCount = 100000,
        threadPoolSize = 500
    )
    fun testConcurrent() {
        runTest(cache)
    }

    private fun runTest(cache: Cache<String, String>) {
        val value = Random.nextLong(100).toString()
        cache.put(value, value)
        val result = cache.getNonNull(value)
        doAssertEquals(result, value)
    }
}