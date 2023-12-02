package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fsgek.common.base.Gek;
import xyz.fsgek.common.collect.GekColl;
import xyz.fsgek.common.collect.GekCollector;

import java.util.*;

public class CollTest {

    @Test
    public void testImmutable() {
        Assert.assertEquals(Gek.arrayOf(1, 2, 3), Arrays.asList(1, 2, 3).toArray(new Integer[0]));
        Assert.assertEquals(Gek.listOf(1, 2, 3), Arrays.asList(1, 2, 3));
        Assert.assertEquals(Gek.setOf(1, 2, 3), new HashSet<>(Arrays.asList(1, 2, 3)));
        Assert.assertEquals(Gek.setOf(1, 2, 2), new HashSet<>(Arrays.asList(1, 2)));
        Assert.assertEquals(Gek.setOf(1, 2, 2).size(), new HashSet<>(Arrays.asList(1, 2)).size());
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
            collector.reset().initialElements(1, 2, 3).toList(),
            Arrays.asList(1, 2, 3)
        );
        Assert.assertEquals(
            collector.reset().initialElements(Arrays.asList(1, 2, 3)).toSet(),
            new HashSet<>(Arrays.asList(1, 2, 3))
        );
        Assert.assertEquals(
            collector.reset().initialElements(Arrays.asList(1, 2, 2)).toSet(),
            new HashSet<>(Arrays.asList(1, 2))
        );
        Assert.assertEquals(
            collector.reset().initialElements(Arrays.asList(1, 2, 2)).immutable(true).toSet(),
            new HashSet<>(Arrays.asList(1, 2))
        );
        Assert.assertEquals(
            collector.reset().initialElements(Arrays.asList(1, 2, 2)).toSet().size(),
            new HashSet<>(Arrays.asList(1, 2)).size()
        );
        Map<Integer, Integer> map = new LinkedHashMap<>();
        map.put(1, 2);
        Assert.assertEquals(
            collector.reset().initialElements(1, 2, 3).toMap(),
            map
        );
        map.put(3, 4);
        Assert.assertEquals(
            collector.reset().initialElements(1, 2, 3, 4).toMap(),
            map
        );
        map.remove(3);
        Assert.assertEquals(collector.reset().initialElements(1, 2, 3).toMap(), map);
        map.put(3, 4);
        Assert.assertEquals(collector.reset().initialElements(1, 2, 3, 4).toMap(), map);

        Assert.assertTrue(collector.reset().initialElements(1, 2, 3).toList().add(4));
        Assert.expectThrows(UnsupportedOperationException.class, () ->
            collector.reset().initialElements(1, 2, 3).immutable(true).toList().add(4));
        List<Integer> list = collector.reset().initialCapacity(100).toList();
        list.add(2);
        list.add(3);
        Assert.assertEquals(list, Arrays.asList(2, 3));
        Assert.assertEquals(collector.reset().toArrayList().getClass(), ArrayList.class);
        Assert.assertEquals(collector.reset().initialSize(3).initialFunction(i -> i).toList(), Arrays.asList(0, 1, 2));
        Assert.assertEquals(collector.reset().initialSize(3).initialFunction(i -> i).toSet(), new HashSet<>(Arrays.asList(0, 1, 2)));
        Assert.assertEquals(
            collector.reset().initialElements(1, 2, 3).initialSize(3).initialFunction(i -> i).toList(),
            Arrays.asList(1, 2, 3)
        );
    }

    @Test
    public void testGetCompute() {
        List<Integer> list = Gek.listOf(0, 1, 2);
        Assert.assertEquals(GekColl.get(list, 1), 1);
        Assert.assertEquals(GekColl.get(list, 1, 9), 1);
        Assert.assertEquals(GekColl.get(list, 8, 9), 9);
        Assert.assertEquals(GekColl.compute(list, 1, i -> i * 2), 1);
        Assert.assertEquals(GekColl.compute(list, 8, i -> i * 2), 16);
        Set<Integer> set = Gek.setOf(0, 1, 2);
        Assert.assertEquals(GekColl.get(set, 1), 1);
        Assert.assertEquals(GekColl.get(set, 1, 9), 1);
        Assert.assertEquals(GekColl.get(set, 8, 9), 9);
        Assert.assertEquals(GekColl.compute(set, 1, i -> i * 2), 1);
        Assert.assertEquals(GekColl.compute(set, 8, i -> i * 2), 16);
        Map<Integer, Integer> map = Gek.mapOf(1, 2, 3, 4);
        Assert.assertEquals(GekColl.get(map, 1), 2);
        Assert.assertEquals(GekColl.get(map, 1, 9), 2);
        Assert.assertEquals(GekColl.get(map, 8, 9), 9);
        Assert.assertEquals(GekColl.compute(map, 1, i -> i * 20), 2);
        Assert.assertEquals(GekColl.compute(map, 8, i -> i * 20), 160);
    }

    @Test
    public void testCollect() {
        Assert.assertEquals(
            GekColl.toMap(Gek.listOf(1, 2, 3, 4)),
            Gek.mapOf(1, 2, 3, 4)
        );
        Assert.assertEquals(
            GekColl.toMap(Gek.listOf(1, 2, 3, 4), String::valueOf, String::valueOf),
            Gek.mapOf("1", "1", "2", "2", "3", "3", "4", "4")
        );
        Assert.assertEquals(
            GekColl.toMap(Gek.mapOf(1, 2, 3, 4), String::valueOf, String::valueOf),
            Gek.mapOf("1", "2", "3", "4")
        );
        Assert.assertEquals(
            GekColl.collect(new LinkedHashMap<>(), 1, 2, 3),
            Gek.mapOf(1, 2, 3)
        );
        Assert.assertEquals(
            GekColl.collect(new LinkedHashMap<>(), 1, 2, 3, 4),
            Gek.mapOf(1, 2, 3, 4)
        );
        Assert.assertEquals(
            GekColl.collect(new LinkedHashMap<>(), Gek.listOf(1, 2, 3)),
            Gek.mapOf(1, 2, 3)
        );
        Assert.assertEquals(
            GekColl.collect(new LinkedHashMap<>(), Gek.listOf(1, 2, 3, 4)),
            Gek.mapOf(1, 2, 3, 4)
        );
        Assert.assertEquals(
            GekColl.collect(new LinkedHashMap<>(), Gek.listOf(1, 2, 3, 4), String::valueOf),
            Gek.mapOf("1", "2", "3", "4")
        );
        Assert.assertEquals(
            GekColl.collect(new LinkedHashMap<>(), Gek.mapOf(1, 2, 3, 4), String::valueOf, String::valueOf),
            Gek.mapOf("1", "2", "3", "4")
        );
        Assert.assertEquals(
            GekColl.toList(Gek.listOf(1, 2, 3, 4)),
            Gek.listOf(1, 2, 3, 4)
        );
        Assert.assertEquals(
            GekColl.toSet(Gek.listOf(1, 1, 3, 3)),
            Gek.setOf(1, 1, 3, 3)
        );
        Assert.assertEquals(
            GekColl.toSet(Gek.listOf(1, 1, 3, 3)),
            new HashSet<>(Gek.listOf(1, 1, 3, 3))
        );
    }
}
