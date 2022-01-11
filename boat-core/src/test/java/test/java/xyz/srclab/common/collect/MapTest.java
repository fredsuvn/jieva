package test.java.xyz.srclab.common.collect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BId;
import xyz.srclab.common.collect.*;

import java.util.HashMap;
import java.util.Map;

public class MapTest {

    @Test
    public void testCopyOnWriteMap() {
        Map<String, Object> map = BMap.copyOnWriteMap();
        Map<String, Object> hashMap = new HashMap<>();
        int size = 10086;
        for (int i = 0; i <= size; i++) {
            String key = String.valueOf(i);
            Object value = BId.uuid();
            map.put(key, value);
            hashMap.put(key, value);
        }
        for (int i = 0; i <= size; i++) {
            String key = String.valueOf(i);
            Object value = map.get(key);
            Object hashValue = hashMap.get(key);
            Assert.assertEquals(hashValue, value);
        }
    }

    @Test
    public void testMultiMap() {
        SetMap<String, Object> setMap = BMap.setMap();
        setMap.add("1", "1");
        setMap.add("2", "2");
        setMap.add("1", "2");
        setMap.add("1", "2");
        Assert.assertEquals(setMap.get("1"), BSet.newSet("1", "2"));
        Assert.assertEquals(setMap.get("2"), BSet.newSet("2"));
        Assert.assertEquals(setMap.getFirst("1"), "1");
        Assert.assertEquals(setMap.getFirst("2"), "2");

        ListMap<String, Object> listMap = BMap.listMap();
        listMap.add("1", "1");
        listMap.add("2", "2");
        listMap.add("1", "2");
        listMap.add("1", "2");
        Assert.assertEquals(listMap.get("1"), BList.newList("1", "2", "2"));
        Assert.assertEquals(listMap.get("2"), BList.newList("2"));
        Assert.assertEquals(listMap.getFirst("1"), "1");
        Assert.assertEquals(listMap.getFirst("2"), "2");
        Assert.assertEquals(listMap.getLast("1"), "2");
        Assert.assertEquals(listMap.get("1", 0), "1");
        Assert.assertEquals(listMap.get("1", 1), "2");
        Assert.assertEquals(listMap.get("1", 2), "2");
    }
}
