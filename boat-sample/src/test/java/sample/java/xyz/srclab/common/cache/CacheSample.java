package sample.java.xyz.srclab.common.cache;

import org.testng.annotations.Test;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.test.TestLogger;

public class CacheSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testCache() {
        Cache<String, String> cache = Cache.newFastCache();
        cache.getOrLoad("1", k -> k);
        //1
        logger.log("1: {}", cache.get("1"));
        //null
        logger.log("2: {}", cache.getOrNull("2"));
    }
}
