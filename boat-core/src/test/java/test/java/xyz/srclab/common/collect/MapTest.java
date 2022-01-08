package test.java.xyz.srclab.common.collect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.collect.BMap;

import java.util.HashMap;
import java.util.Map;

public class MapTest {

    @Test
    public void testCopyOnWriteMap() {
        Map<String, Object> map = BMap.copyOnWriteMap();
        Map<String, Object> hashMap = new HashMap<>();
        for (int i = 0; i <= 10086; i++) {
            String key = String.valueOf(i);
            map.put(key, key);
            hashMap.put(key, key);
        }
        for (int i = 0; i <= 10086; i++) {
            String key = String.valueOf(i);
            Object value = map.get(key);
            Object hashValue = hashMap.get(key);
            Assert.assertEquals(hashValue, value);
        }
    }
}
