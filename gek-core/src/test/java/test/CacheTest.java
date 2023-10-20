package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgek.common.base.GekLogger;
import xyz.fsgek.common.base.GekWrapper;
import xyz.fsgek.common.base.ref.GekRef;
import xyz.fsgek.common.base.ref.IntRef;
import xyz.fsgek.common.cache.GekCache;

import java.util.HashSet;
import java.util.Set;

public class CacheTest {

    @Test
    public void testCache() {
        final int[] detected = {0};
        GekCache<Integer, String> softCache = GekCache.softCache((cache, key) -> {
            detected[0]++;
            cache.cleanUp();
        });
        //Map<Integer, String> map = new HashMap<>();
        int times = 1000 * 3;
        String value = TestUtil.buildRandomString(1024 * 100, 1024 * 100);
        for (int i = 0; i < times; i++) {
            softCache.put(i, value + "i");
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
        GekLogger.defaultLogger().info("Soft---> total: " + times +
            ", removed: " + removed +
            ", detected: " + detected[0] +
            ", cached: " + (times - removed) +
            ", size: " + softCache.size()
        );
        Assert.assertEquals(removed, detected[0]);

        detected[0] = 0;
        GekCache<Integer, String> weakCache = GekCache.weakCache((cache, key) -> {
            detected[0]++;
            cache.cleanUp();
        });
        for (int i = 0; i < times; i++) {
            weakCache.put(i, value + "i");
        }
        removed = 0;
        for (int i = 0; i < times; i++) {
            String fsValue = weakCache.get(i);
            if (fsValue == null) {
                removed++;
            }
        }
        GekLogger.defaultLogger().info("Weak---> total: " + times +
            ", removed: " + removed +
            ", detected: " + detected[0] +
            ", cached: " + (times - removed) +
            ", size: " + weakCache.size()
        );
        Assert.assertEquals(removed, detected[0]);
    }

    @Test
    public void testCacheLoader() {
        GekCache<Integer, String> gekCache = GekCache.softCache();
        Assert.assertNull(gekCache.get(1));
        Assert.assertEquals(gekCache.get(1, String::valueOf), "1");
        Assert.assertEquals(gekCache.get(1, String::valueOf), "1");
        GekLogger.defaultLogger().info("cacheLoader: 1=", gekCache.get(1));
        gekCache.get(1, k -> null);
        Assert.assertEquals(gekCache.get(1), "1");
        gekCache.remove(1);
        gekCache.get(1, k -> null);
        Assert.assertNull(gekCache.get(1));
        Assert.assertEquals(gekCache.getWrapper(1), GekWrapper.empty());
        gekCache.remove(1);
        gekCache.getWrapper(1, k -> null);
        Assert.assertNull(gekCache.get(1));
        Assert.assertNull(gekCache.getWrapper(1));
        gekCache.put(2, "2");
        Assert.assertEquals(gekCache.get(2), "2");
        Assert.assertEquals(gekCache.get(2, k -> "4"), "2");
        Assert.assertEquals(gekCache.getWrapper(2, k -> GekWrapper.wrap("8")).get(), "2");
    }

    @Test
    public void testCleanUp() {
        GekCache<Integer, Integer> gekCache = GekCache.softCache((c, k) -> c.cleanUp());
        for (int i = 0; i < 10000; i++) {
            gekCache.put(i, i);
        }
        gekCache.cleanUp();
    }

    @Test
    public void testRemove() {
        IntRef intRef = GekRef.ofInt(0);
        GekCache<Integer, Integer> gekCache = GekCache.softCache((c, k) -> intRef.incrementAndGet());
        gekCache.put(1, 1);
        gekCache.remove(1);
        Assert.assertEquals(intRef.get(), 1);
    }

    @Test
    public void testClear() {
        IntRef intRef = GekRef.ofInt(0);
        GekCache<Integer, Integer> gekCache = GekCache.softCache((c, k) -> intRef.incrementAndGet());
        for (int i = 0; i < 10000; i++) {
            gekCache.put(i, i);
        }
        gekCache.clear();
        Assert.assertEquals(intRef.get(), 10000);
    }

    @Test
    public void testRemoveIf() {
        IntRef intRef = GekRef.ofInt(0);
        GekCache<Integer, Integer> gekCache = GekCache.softCache((c, k) -> intRef.incrementAndGet());
        for (int i = 0; i < 10; i++) {
            gekCache.put(i, i);
        }
        gekCache.removeIf((k, v) -> k > 5);
        Assert.assertEquals(intRef.get(), 4);
        Assert.assertEquals(gekCache.size(), 6);
    }

    @Test
    public void testNull() {
        IntRef intRef = GekRef.ofInt(0);
        Set<Integer> set = new HashSet<>();
        GekCache<Integer, Integer> gekCache = GekCache.softCache((c, k) -> {
            intRef.incrementAndGet();
            set.remove(k);
        });
        for (int i = 0; i < 10000; i++) {
            gekCache.put(i, null);
        }
        for (int i = 0; i < 10000; i++) {
            GekWrapper<Integer> w = gekCache.getWrapper(i);
            if (w != null) {
                Assert.assertNull(w.get());
                set.add(i);
            }
        }
        Assert.assertEquals(set.size() + intRef.get(), 10000);
        gekCache.clear();
        Assert.assertEquals(intRef.get(), 10000);
    }
}
