package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgik.common.base.FsLogger;
import xyz.fsgik.common.base.FsWrapper;
import xyz.fsgik.common.base.ref.FsRef;
import xyz.fsgik.common.base.ref.IntRef;
import xyz.fsgik.common.cache.FsCache;

import java.util.HashSet;
import java.util.Set;

public class CacheTest {

    @Test
    public void testCache() {
        final int[] detected = {0};
        FsCache<Integer, String> softCache = FsCache.softCache((cache, key) -> {
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
        FsLogger.defaultLogger().info("Soft---> total: " + times +
            ", removed: " + removed +
            ", detected: " + detected[0] +
            ", cached: " + (times - removed) +
            ", size: " + softCache.size()
        );
        Assert.assertEquals(removed, detected[0]);

        detected[0] = 0;
        FsCache<Integer, String> weakCache = FsCache.weakCache((cache, key) -> {
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
        FsLogger.defaultLogger().info("Weak---> total: " + times +
            ", removed: " + removed +
            ", detected: " + detected[0] +
            ", cached: " + (times - removed) +
            ", size: " + weakCache.size()
        );
        Assert.assertEquals(removed, detected[0]);
    }

    @Test
    public void testCacheLoader() {
        FsCache<Integer, String> fsCache = FsCache.softCache();
        Assert.assertNull(fsCache.get(1));
        Assert.assertEquals(fsCache.get(1, String::valueOf), "1");
        Assert.assertEquals(fsCache.get(1, String::valueOf), "1");
        FsLogger.defaultLogger().info("cacheLoader: 1=", fsCache.get(1));
        fsCache.get(1, k -> null);
        Assert.assertEquals(fsCache.get(1), "1");
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
    public void testRemove() {
        IntRef intRef = FsRef.ofInt(0);
        FsCache<Integer, Integer> fsCache = FsCache.softCache((c, k) -> intRef.incrementAndGet());
        fsCache.put(1, 1);
        fsCache.remove(1);
        Assert.assertEquals(intRef.get(), 1);
    }

    @Test
    public void testClear() {
        IntRef intRef = FsRef.ofInt(0);
        FsCache<Integer, Integer> fsCache = FsCache.softCache((c, k) -> intRef.incrementAndGet());
        for (int i = 0; i < 10000; i++) {
            fsCache.put(i, i);
        }
        fsCache.clear();
        Assert.assertEquals(intRef.get(), 10000);
    }

    @Test
    public void testRemoveIf() {
        IntRef intRef = FsRef.ofInt(0);
        FsCache<Integer, Integer> fsCache = FsCache.softCache((c, k) -> intRef.incrementAndGet());
        for (int i = 0; i < 10; i++) {
            fsCache.put(i, i);
        }
        fsCache.removeIf((k, v) -> k > 5);
        Assert.assertEquals(intRef.get(), 4);
        Assert.assertEquals(fsCache.size(), 6);
    }

    @Test
    public void testNull() {
        IntRef intRef = FsRef.ofInt(0);
        Set<Integer> set = new HashSet<>();
        FsCache<Integer, Integer> fsCache = FsCache.softCache((c, k) -> {
            intRef.incrementAndGet();
            set.remove(k);
        });
        for (int i = 0; i < 10000; i++) {
            fsCache.put(i, null);
        }
        for (int i = 0; i < 10000; i++) {
            FsWrapper<Integer> w = fsCache.getWrapper(i);
            if (w != null) {
                Assert.assertNull(w.get());
                set.add(i);
            }
        }
        Assert.assertEquals(set.size() + intRef.get(), 10000);
        fsCache.clear();
        Assert.assertEquals(intRef.get(), 10000);
    }
}
