package test.java.xyz.srclab.common.collect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BId;
import xyz.srclab.common.collect.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        MutableSetMap<String, String> setMap = BMap.mutableSetMap();
        setMap.add("1", "1");
        setMap.add("2", "2");
        setMap.add("1", "2");
        setMap.add("1", "2");
        Assert.assertEquals(setMap.get("1"), BSet.newSet("1", "2"));
        Assert.assertEquals(setMap.get("2"), BSet.newSet("2"));
        Assert.assertEquals(setMap.getFirst("1"), "1");
        Assert.assertEquals(setMap.getFirst("2"), "2");

        Map<String, Set<String>> ms = BMap.newMap(
            "1", BSet.newSet("1", "1"),
            "2", BSet.newSet("1", "2")
        );
        SetMap<String, String> sms = BMap.toSetMap(ms);
        Assert.assertEquals(sms.get("1"), BSet.newSet("1"));
        Assert.assertEquals(sms.get("2"), BSet.newSet("1", "2"));
        Assert.assertEquals(sms.getFirst("1"), "1");
        Assert.assertEquals(sms.getFirst("2"), "1");

        MutableListMap<String, String> listMap = BMap.mutableListMap();
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

        Map<String, List<String>> ml = BMap.newMap(
            "1", BList.newList("1", "1"),
            "2", BList.newList("1", "2")
        );
        ListMap<String, String> sml = BMap.toListMap(ml);
        Assert.assertEquals(sml.get("1"), BList.newList("1", "1"));
        Assert.assertEquals(sml.get("2"), BList.newList("1", "2"));
        Assert.assertEquals(sml.getFirst("1"), "1");
        Assert.assertEquals(sml.getFirst("2"), "1");
        Assert.assertEquals(sml.getLast("1"), "1");
        Assert.assertEquals(sml.getLast("2"), "2");
        Assert.assertEquals(sml.get("1", 0), "1");
        Assert.assertEquals(sml.get("1", 1), "1");
    }
}
