package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgek.common.base.GekLog;
import xyz.fsgek.common.base.GekObject;
import xyz.fsgek.common.base.ref.GekRef;
import xyz.fsgek.common.base.ref.IntRef;
import xyz.fsgek.common.cache.GekCache;

import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CacheTest {

    @Test
    public void testCache() {
        final int[] detected = {0};
        final long[] collected = {0};
        testCache(GekCache.softCache((k, v, c) -> {
            detected[0]++;
            if (Objects.equals(c, GekCache.RemovalListener.Cause.COLLECTED)) {
                collected[0]++;
            }
        }), detected, "soft-cache");
        testCache(GekCache.weakCache((k, v, c) -> {
            detected[0]++;
            if (Objects.equals(c, GekCache.RemovalListener.Cause.COLLECTED)) {
                collected[0]++;
            }
        }), detected, "weak-cache");
        GekLog.getInstance().info("Collected: ", collected[0]);
    }

    private void testCache(GekCache<Integer, String> cache, int[] detected, String name) {
        detected[0] = 0;
        //Map<Integer, String> map = new HashMap<>();
        int times = 1000 * 3;
        String value = TestUtil.buildRandomString(1024 * 100, 1024 * 100);
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
        GekLog.getInstance().info(name + "---> total: " + times +
            ", removed: " + removed +
            ", detected: " + detected[0] +
            ", cached: " + (times - removed) +
            ", size: " + cache.size()
        );
        //Assert.assertEquals(removed, detected[0]);
    }

    @Test
    public void testCacheLoader() {
        testCacheLoader(GekCache.softCache());
    }

    private void testCacheLoader(GekCache<Integer, String> gekCache) {
        Assert.assertNull(gekCache.get(1));
        Assert.assertEquals(gekCache.get(1, String::valueOf), "1");
        Assert.assertEquals(gekCache.get(1, String::valueOf), "1");
        GekLog.getInstance().info("cacheLoader: 1=", gekCache.get(1));
        gekCache.get(1, k -> null);
        Assert.assertEquals(gekCache.get(1), "1");
        gekCache.remove(1);
        gekCache.get(1, k -> null);
        Assert.assertNull(gekCache.get(1));
//        Assert.assertEquals(gekCache.getWrapper(1).get(), GekObject.empty().get());
        gekCache.remove(1);
//        gekCache.getWrapper(1, k -> null);
        Assert.assertNull(gekCache.get(1));
//        Assert.assertNull(gekCache.getWrapper(1));
        gekCache.put(2, "2");
        Assert.assertEquals(gekCache.get(2), "2");
        Assert.assertEquals(gekCache.get(2, k -> "4"), "2");
//        Assert.assertEquals(gekCache.getWrapper(2, k -> GekCache.Value.of("8", null, null)).get(), "2");
    }

    @Test
    public void testCleanUp() {
        GekCache<Integer, Integer> gekCache = GekCache.softCache((k, v, c) -> {
        });
        for (int i = 0; i < 10000; i++) {
            gekCache.put(i, i);
        }
        gekCache.cleanUp();
    }

    @Test
    public void testRemove() {
        IntRef intRef = GekRef.ofInt(0);
        GekCache<Integer, Integer> gekCache = GekCache.softCache((k, v, c) -> intRef.incrementAndGet());
        gekCache.put(1, 1);
        gekCache.remove(1);
        Assert.assertEquals(intRef.get(), 1);
    }

    @Test
    public void testClear() {
        IntRef intRef = GekRef.ofInt(0);
        GekCache<Integer, Integer> gekCache = GekCache.softCache((k, v, c) -> intRef.incrementAndGet());
        for (int i = 0; i < 10000; i++) {
            gekCache.put(i, i);
        }
        gekCache.clear();
        Assert.assertEquals(intRef.get(), 10000);
    }

    @Test
    public void testRemoveIf() {
        IntRef intRef = GekRef.ofInt(0);
        GekCache<Integer, Integer> gekCache = GekCache.softCache((k, v, c) -> intRef.incrementAndGet());
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
        GekCache<Integer, Integer> gekCache = GekCache.softCache((k, v, c) -> {
            intRef.incrementAndGet();
            set.remove(k);
        });
        for (int i = 0; i < 10000; i++) {
            gekCache.put(i, (Integer) null);
        }
        for (int i = 0; i < 10000; i++) {
//            GekObject<Integer> w = gekCache.getWrapper(i);
//            if (w != null) {
//                Assert.assertNull(w.get());
//                set.add(i);
//            }
        }
        Assert.assertEquals(set.size() + intRef.get(), 10000);
        gekCache.clear();
        Assert.assertEquals(intRef.get(), 10000);
    }

    @Test
    public void testExpiration() throws InterruptedException {
        int[] l = {0};
        GekCache.RemovalListener.Cause[] cause = {null};
        GekCache<Integer, Integer> cache = GekCache.softCache((k, v, c) -> {
            l[0]++;
            cause[0] = c;
        });
        cache.put(1, 1, 1000);
        Assert.assertEquals(cache.get(1), 1);
        Assert.assertEquals(l[0], 0);
        Thread.sleep(1001);
        Assert.assertNull(cache.get(1));
        Assert.assertEquals(l[0], 1);
        Assert.assertEquals(cause[0], GekCache.RemovalListener.Cause.EXPIRED);
        cache.put(1, 1, 1000);
        cache.put(1, 1, 1000);
        Assert.assertEquals(cause[0], GekCache.RemovalListener.Cause.REPLACED);
        cache.remove(1);
        Assert.assertEquals(cause[0], GekCache.RemovalListener.Cause.EXPLICIT);

        l[0] = 1;
        cache.put(2, 2, 1000);
        cache.expire(2, 1500);
        Thread.sleep(1111);
        Assert.assertEquals(cache.get(2), 2);
        Assert.assertEquals(l[0], 1);
        Thread.sleep(1000);
        Assert.assertNull(cache.get(2));
        Assert.assertEquals(l[0], 2);
        cache.put(3, GekCache.Value.of(3, 1000, 1000));
        Thread.sleep(500);
        Assert.assertEquals(cache.get(3), 3);
        Thread.sleep(500);
        Assert.assertEquals(cache.get(3), 3);
        Thread.sleep(1001);
        Assert.assertNull(cache.get(3));

        GekCache<Integer, Integer> cache2 = GekCache.newBuilder().expireAfterWrite(Duration.ofMillis(1000)).build();
        cache2.put(1, 1);
        Assert.assertEquals(cache2.get(1), 1);
        Thread.sleep(1001);
        Assert.assertNull(cache2.get(1));
        cache2.put(2, 2);
        cache2.expire(2, 1500);
        Assert.assertEquals(cache2.get(2), 2);
        Thread.sleep(1001);
        Assert.assertEquals(cache2.get(2), 2);
        Thread.sleep(501);
        Assert.assertNull(cache2.get(2));
    }
}
