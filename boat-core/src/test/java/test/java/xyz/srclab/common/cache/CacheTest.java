package test.java.xyz.srclab.common.cache;

import kotlin.Pair;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.collect.Collects;

import java.util.*;

/**
 * @author sunqian
 */
public class CacheTest {

    @Test
    public void testWeakCache() {
        doTestCache(Cache.weakCache());
    }

    @Test
    public void testMapCache() {
        doTestCache(Cache.ofMap(new HashMap<>()));
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

        cache.put("x1", "x1");
        cache.put("x2", "x2");
        cache.put("x3", "x3");
        Map<String, String> resultPresent = cache.getPresent(Arrays.asList("x1", "x2", "x3", "x4"));
        Assert.assertEquals(
            resultPresent,
            Collects.putEntries(new LinkedHashMap<>(), "x1", "x1", "x2", "x2", "x3", "x3")
        );
        Map<String, String> resultAll = cache.getAll(
            Arrays.asList("x1", "x2", "x3", "x4"),
            keys -> Collects.associate(keys, (key) -> new Pair<String, String>(key, key)));
        Assert.assertEquals(
            resultAll,
            Collects.putEntries(new LinkedHashMap<>(), "x1", "x1", "x2", "x2", "x3", "x3", "x4", "x4")
        );
    }
}
