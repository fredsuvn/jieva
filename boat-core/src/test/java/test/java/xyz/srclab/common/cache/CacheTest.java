package test.java.xyz.srclab.common.cache;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.cache.Cache;

import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * @author sunqian
 */
public class CacheTest {

    @Test
    public void testWeakCache() {
        doTestCache(Cache.ofWeak());
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
        Assert.assertNull(cache.getVal("1"));
        Assert.assertEquals(cache.get("1", k -> "111"), "111");
        Assert.assertEquals(cache.get("1"), "111");
        Assert.assertEquals(cache.get("2", k -> "222"), "222");
        Assert.assertEquals(cache.get("3", k -> "333"), "333");
        Assert.assertEquals(cache.get("4", k -> "444"), "444");

        cache.remove("1");
        Assert.assertNull(cache.getVal("1"));
        Assert.assertNull(cache.getPresentVal("1"));
        Assert.assertEquals(cache.get("1", k -> "1234"), "1234");
        Assert.assertEquals(cache.getPresent("1"), "1234");

        cache.put("9", "9");
        Assert.assertEquals(cache.getPresent("9"), "9");

        cache.clear();
        Assert.assertNull(cache.getVal("9"));
    }
}
