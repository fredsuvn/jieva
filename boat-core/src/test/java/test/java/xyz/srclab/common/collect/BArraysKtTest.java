package test.java.xyz.srclab.common.collect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.collect.Collects;
import xyz.srclab.common.collect.CopyOnWriteMap;
import xyz.srclab.common.logging.Logs;

import java.util.*;

/**
 * @author sunqian
 */
public class BArraysKtTest {

    @Test
    public void testArray() {
        String[] stringArray = Collects.asArray("1", "2", "3");
        Logs.info(Collects.joinToString(stringArray));
        Assert.assertEquals(
            Collects.joinToString(stringArray),
            "1, 2, 3"
        );
    }

    @Test
    public void testJoinToString() {
        String[] strings = Collects.asArray("1", "2", "3");
        Collects.asList(strings).set(0, "111");
        Collects.asList(strings).set(1, "222");
        Collects.asList(strings).set(2, "333");
        Assert.assertEquals(
            Collects.joinToString(strings),
            "111, 222, 333"
        );
        Assert.assertEquals(
            Collects.joinToString(Collects.asList(strings)),
            "111, 222, 333"
        );

        int[] ints = {1, 2, 3};
        Collects.asList(ints).set(0, 111);
        Collects.asList(ints).set(1, 222);
        Collects.asList(ints).set(2, 333);
        Assert.assertEquals(
            Collects.joinToString(ints),
            "111, 222, 333"
        );
        Assert.assertEquals(
            Collects.joinToString(Collects.asList(ints)),
            "111, 222, 333"
        );
    }

    @Test
    public void testPlus() {
        List<String> list = Arrays.asList("1", "2", "3");
        Assert.assertEquals(
            Collects.plusBefore(list, 1, "99"),
            Arrays.asList("1", "99", "2", "3")
        );
        Assert.assertEquals(
            Collects.plusAfter(list, 1, "99"),
            Arrays.asList("1", "2", "99", "3")
        );
        Assert.assertEquals(
            Collects.minusAt(list, 1, 2),
            Collections.singletonList("1")
        );
    }

    @Test
    public void testEnumeration() {
        List<String> list = Arrays.asList("1", "2", "3");
        Enumeration<String> enumeration = Collects.asEnumeration(list);
        Iterable<String> iterable = Collects.asIterable(enumeration);
        Set<String> set = Collects.toSet(iterable);
        Assert.assertEquals(set, Collects.newSet("1", "2", "3"));
    }

    @Test
    public void testCopyOnWriteMap() {
        Map<String, Object> map = new CopyOnWriteMap<>();
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
