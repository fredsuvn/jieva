package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsLogger;
import xyz.srclab.common.cache.FsCache;

import java.util.HashMap;
import java.util.Map;

public class CacheTest {

    @Test
    public void cache() {
        final int[] detected = {0};
        FsCache<String> fsCache = FsCache.newCache(key -> detected[0]++);
        Map<Integer, String> map = new HashMap<>();
        int times = 1000000 * 3;
        for (int i = 0; i < times; i++) {
            fsCache.set(i, String.valueOf(i * 10086));
            //map.put(i, String.valueOf(i * 10086));
        }
        int removed = 0;
        for (int i = 0; i < times; i++) {
            String fsValue = fsCache.get(i);
            if (fsValue == null) {
                removed++;
            } else {
                //Assert.assertEquals(map.get(i), fsValue);
            }
        }
        FsLogger.system().info("total: " + times +
            ", removed: " + removed +
            ", detected: " + detected[0] +
            ", cached: " + (times - removed));
        Assert.assertEquals(removed, detected[0]);
        //fill map:
        //2023-05-21 22:27:25.006[INFO]test.CacheTest.cache(32)[Test worker]:total: 3000000, removed: 2582629, detected: 2582629, cached: 417371
        //remove map:
        //2023-05-21 22:29:00.384[INFO]test.CacheTest.cache(32)[Test worker]:total: 3000000, removed: 177071, detected: 177071, cached: 2822929
    }

    @Test
    public void cacheNull() {
        FsCache<String> fsCache = FsCache.newCache();
        fsCache.set(1, null);
        Assert.assertNull(fsCache.get(1));
        Assert.assertNotNull(fsCache.getOptional(1));
        fsCache.remove(1);
        Assert.assertNull(fsCache.get(1));
        Assert.assertNull(fsCache.getOptional(1));
    }

    @Test
    public void cacheLoader() {
        FsCache<String> fsCache = FsCache.newCache();
        Assert.assertNull(fsCache.get(1));
        Assert.assertEquals(fsCache.get(1, String::valueOf), "1");
        Assert.assertEquals(fsCache.get(1, String::valueOf), "1");
        FsLogger.system().info("cacheLoader: 1=", fsCache.get(1));
    }
}
