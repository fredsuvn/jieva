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
//        testCache(
//            GekCache.newBuilder().removeListener((cache, key) -> {
//                detected[0]++;
//                cache.cleanUp();
//            }).build(),
//            detected,
//            "Full-soft"
//        );
        testCache(
            GekCache.newBuilder().removeListener((cache, key) -> {
                detected[0]++;
                cache.cleanUp();
            }).weakReference().build(),
            detected,
            "Weak-soft"
        );
    }

    private void testCache(GekCache<Integer, String> cache, int[] detected, String name) {
        detected[0] = 0;
        //Map<Integer, String> map = new HashMap<>();
        int times = 1000 * 3;
        String value = TestUtil.buildRandomString(1024 * 100, 0);
        for (int i = 0; i < times; i++) {
            cache.put(i, value + "i");
            //map.put(i, String.valueOf(i * 10086));
        }
        int removed = 0;
        for (int i = 0; i < times; i++) {
            String fsValue = cache.get(i);
            if (fsValue == null) {
                removed++;
            } else {
                //Assert.assertEquals(map.get(i), fsValue);
            }
        }
        GekLogger.defaultLogger().info(name + "---> total: " + times +
            ", removed: " + removed +
            ", detected: " + detected[0] +
            ", cached: " + (times - removed) +
            ", size: " + cache.size()
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
        Assert.assertEquals(gekCache.getWrapper(2, k -> GekCache.ValueInfo.of("8", null)).get(), "2");
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
