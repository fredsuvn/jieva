package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgek.common.base.Gek;
import xyz.fsgek.common.collect.GekCollector;

import java.util.*;

public class CollTest {

    @Test
    public void testImmutable() {
        Assert.assertEquals(Gek.arrayOf(1, 2, 3), Arrays.asList(1, 2, 3).toArray(new Integer[0]));
        Assert.assertEquals(Gek.listOf(1, 2, 3), Arrays.asList(1, 2, 3));
        Assert.assertEquals(Gek.setOf(1, 2, 3), new HashSet<>(Arrays.asList(1, 2, 3)));
        Map<Integer, Integer> map = new LinkedHashMap<>();
        map.put(1, 2);
        Assert.assertEquals(Gek.mapOf(1, 2, 3), map);
        map.put(3, 4);
        Assert.assertEquals(Gek.mapOf(1, 2, 3, 4), map);
        Assert.expectThrows(UnsupportedOperationException.class, () -> Gek.listOf(1, 2, 3).set(0, 5));
    }

    @Test
    public void testCollector() {
        GekCollector collector = Gek.collector();
        Assert.assertEquals(
            collector.clear().initialElements(1, 2, 3).toList(),
            Arrays.asList(1, 2, 3)
        );
        Assert.assertEquals(
            collector.clear().initialElements(1, 2, 3).toSet(),
            new HashSet<>(Arrays.asList(1, 2, 3))
        );
        Map<Integer, Integer> map = new LinkedHashMap<>();
        map.put(1, 2);
        Assert.assertEquals(
            collector.clear().initialElements(1, 2, 3).toMap(),
            map
        );
        map.put(3, 4);
        Assert.assertEquals(
            collector.clear().initialElements(1, 2, 3, 4).toMap(),
            map
        );
        Assert.assertTrue(collector.clear().initialElements(1, 2, 3).toList().add(4));
        Assert.expectThrows(UnsupportedOperationException.class, () ->
            collector.clear().initialElements(1, 2, 3).immutable(true).toList().add(4));
        List<Integer> list = collector.clear().initialCapacity(100).toList();
        list.add(2);
        list.add(3);
        Assert.assertEquals(list, Arrays.asList(2, 3));
        Assert.assertEquals(collector.clear().toArrayList().getClass(), ArrayList.class);
        Assert.assertEquals(collector.clear().initialSize(3).initialFunction(i -> i).toList(), Arrays.asList(0, 1, 2));
        Assert.assertEquals(collector.clear().initialSize(3).initialFunction(i -> i).toSet(), new HashSet<>(Arrays.asList(0, 1, 2)));
        Assert.assertEquals(
            collector.clear().initialElements(1, 2, 3).initialSize(3).initialFunction(i -> i).toList(),
            Arrays.asList(1, 2, 3)
        );
    }
}
