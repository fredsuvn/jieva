package test.java.xyz.srclab.common.cache;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.cache.MapCache;
import xyz.srclab.common.cache.ThreadLocalCache;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author sunqian
 */
public class CacheTest {

    @Test
    public void testFastCache() {
        doTestCache(Cache.newFastCache());
    }

    @Test
    public void testMapCache() {
        doTestCache(new MapCache<>(new HashMap<>()));
    }

    @Test
    public void testThreadLocalCache() {
        doTestCache(new ThreadLocalCache<>(() -> new MapCache<>(new HashMap<>())));
    }

    @Test
    public void testCaffeineCache() {
        Cache.Builder<String, String> builder = Cache.newBuilder();
        doTestCache(builder.useGuava(false).build());
    }

    @Test
    public void testGuavaCache() {
        Cache.Builder<String, String> builder = Cache.newBuilder();
        doTestCache(builder.useGuava(true).build());
    }

    private void doTestCache(Cache<String, String> cache) {
        cache.cleanUp();
        Assert.expectThrows(NoSuchElementException.class, () -> cache.get("1"));
        Assert.assertEquals(cache.getOrElse("1", k -> k + k), "11");
        Assert.assertNull(cache.getOrNull("1"));
        Assert.assertEquals(cache.getOrLoad("1", k -> "111"), "111");
        Assert.assertEquals(cache.getOrNull("1"), "111");
        Assert.assertEquals(cache.getOrLoad("2", k -> "222"), "222");
        Assert.assertEquals(cache.getOrLoad("3", k -> "333"), "333");
        Assert.assertEquals(cache.getOrLoad("4", k -> "444"), "444");

        Map<String, String> map = new HashMap<>();
        map.put("1", "111");
        map.put("2", "222");
        map.put("5", "55");
        Assert.assertEquals(cache.getAll(Arrays.asList("1", "2", "5"), it -> {
            Map<String, String> result = new HashMap<>();
            for (String s : it) {
                result.put(s, s + s);
            }
            return result;
        }), map);
        map.remove("5");
        Assert.assertEquals(cache.getPresent(Arrays.asList("1", "2", "6")), map);

        cache.invalidate("1");
        Assert.assertNull(cache.getOrNull("1"));
    }
}
