package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsLogger;
import xyz.srclab.common.base.ref.IntRef;
import xyz.srclab.common.cache.FsCache;

import java.util.HashMap;
import java.util.Map;

public class CacheTest {

    @Test
    public void testCache() {
        final int[] detected = {0};
        FsCache<Integer, String> softCache = FsCache.softCache((cache, key) -> {
            detected[0]++;
            cache.cleanUp();
        });
        Map<Integer, String> map = new HashMap<>();
        int times = 1000000 * 3;
        for (int i = 0; i < times; i++) {
            softCache.put(i, String.valueOf(i * 10086));
            //map.put(i, String.valueOf(i * 10086));
        }
        int removed = 0;
        for (int i = 0; i < times; i++) {
            String fsValue = softCache.get(i);
            if (fsValue == null) {
                removed++;
            } else {
                //Assert.assertEquals(map.get(i), fsValue);
            }
        }
        FsLogger.system().info("Soft---> total: " + times +
            ", removed: " + removed +
            ", detected: " + detected[0] +
            ", cached: " + (times - removed) +
            ", size: " + softCache.size()
        );
        Assert.assertEquals(removed, detected[0]);
        //fill map:
        //2023-05-21 22:27:25.006[INFO]test.CacheTest.cache(32)[Test worker]:total: 3000000, removed: 2582629, detected: 2582629, cached: 417371
        //remove map:
        //2023-05-21 22:29:00.384[INFO]test.CacheTest.cache(32)[Test worker]:total: 3000000, removed: 177071, detected: 177071, cached: 2822929

        detected[0] = 0;
        FsCache<Integer, String> weakCache = FsCache.weakCache((cache, key) -> {
            detected[0]++;
            cache.cleanUp();
        });
        for (int i = 0; i < times; i++) {
            weakCache.put(i, String.valueOf(i * 10086));
        }
        removed = 0;
        for (int i = 0; i < times; i++) {
            String fsValue = weakCache.get(i);
            if (fsValue == null) {
                removed++;
            }
        }
        FsLogger.system().info("Weak---> total: " + times +
            ", removed: " + removed +
            ", detected: " + detected[0] +
            ", cached: " + (times - removed) +
            ", size: " + weakCache.size()
        );
        Assert.assertEquals(removed, detected[0]);
    }

    @Test
    public void testCacheNull() {
        FsCache<Integer, String> fsCache = FsCache.softCache();
        fsCache.put(1, null);
        Assert.assertNull(fsCache.get(1));
        Assert.assertNotNull(fsCache.getOptional(1));
        fsCache.remove(1);
        Assert.assertNull(fsCache.get(1));
        Assert.assertNull(fsCache.getOptional(1));
    }

    @Test
    public void testCacheLoader() {
        FsCache<Integer, String> fsCache = FsCache.softCache();
        Assert.assertNull(fsCache.get(1));
        Assert.assertEquals(fsCache.get(1, String::valueOf), "1");
        Assert.assertEquals(fsCache.get(1, String::valueOf), "1");
        FsLogger.system().info("cacheLoader: 1=", fsCache.get(1));
    }

    @Test
    public void testCleanUp() {
        FsCache<Integer, Integer> fsCache = FsCache.softCache((c, k) -> c.cleanUp());
        for (int i = 0; i < 10000; i++) {
            fsCache.put(i, i);
        }
        fsCache.cleanUp();
    }

    @Test
    public void testClear() {
        IntRef intRef = new IntRef();
        FsCache<Integer, Integer> fsCache = FsCache.softCache((c, k) -> intRef.incrementAndGet());
        for (int i = 0; i < 10000; i++) {
            fsCache.put(i, i);
        }
        fsCache.clear();
        Assert.assertEquals(intRef.get(), 10000);
    }
}
