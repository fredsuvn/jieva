package sample.xyz.srclab.common.cache

import org.testng.annotations.Test
import xyz.srclab.common.cache.Cache
import xyz.srclab.common.test.TestLogger

class CacheSampleKt {

    @Test
    fun testCache() {
        val cache = Cache.newFastCache<String, String>()
        cache.getOrLoad("1") { k: String -> k }
        //1
        logger.log("1: {}", cache.get("1"))
        //null
        logger.log("2: {}", cache.getOrNull("2"))
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}