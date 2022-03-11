package test.java.xyz.srclab.common.collect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.collect.*;

import java.util.*;

public class CollectTest {

    @Test
    public void testPlus() {
        List<String> list = Arrays.asList("1", "2", "3");
        Assert.assertEquals(
            BCollect.plusBefore(list, 1, "99"),
            Arrays.asList("1", "99", "2", "3")
        );
        Assert.assertEquals(
            BCollect.plusAfter(list, 1, "99"),
            Arrays.asList("1", "2", "99", "3")
        );
        Assert.assertEquals(
            BCollect.minusAt(list, 1, 2),
            Collections.singletonList("1")
        );
    }

    @Test
    public void testEnumeration() {
        List<String> list = Arrays.asList("1", "2", "3");
        Enumeration<String> enumeration = BCollect.asEnumeration(list);
        Iterable<String> iterable = BCollect.asIterable(enumeration);
        Set<String> set = BCollect.toSet(iterable);
        Assert.assertEquals(set, BSet.newSet("1", "2", "3"));
    }

    @Test
    public void testToMap() {
        int[] a = {0, 1, 2, 3};
        Map<Integer, Integer> map = BCollect.toMapWithNext(BArray.asList(a), BMap::newEntry);
        BLog.info("toMapWithNext: {}", map);
        Assert.assertEquals(map, BMap.newMap(0, 1, 2, 3));
    }

    @Test
    public void testGet() {
        List<Integer> list = BList.newList(0, 1, 2, 3, 4);
        Assert.assertEquals((Object) BCollect.get(list, 1), 1);
        Assert.assertNull(BCollect.getOrNull(list, 100));
        Assert.assertEquals((Object) BCollect.getOrDefault(list, 100, 66), 66);
        Assert.assertEquals((Object) BCollect.getOrElse(list, 100, (i) -> 88), 88);
        Assert.assertEquals(BCollect.get(list, 1, String.class), "1");
    }
}
