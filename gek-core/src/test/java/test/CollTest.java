package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.collect.GekColl;
import xyz.fslabo.common.collect.GekCollector;

import java.util.*;

public class CollTest {

    @Test
    public void testImmutable() {
        Assert.assertEquals(Jie.asArray(1, 2, 3), Arrays.asList(1, 2, 3).toArray(new Integer[0]));
        Assert.assertEquals(Jie.asList(1, 2, 3), Arrays.asList(1, 2, 3));
        Assert.assertEquals(Jie.asSet(1, 2, 3), new HashSet<>(Arrays.asList(1, 2, 3)));
        Assert.assertEquals(Jie.asSet(1, 2, 2), new HashSet<>(Arrays.asList(1, 2)));
        Assert.assertEquals(Jie.asSet(1, 2, 2).size(), new HashSet<>(Arrays.asList(1, 2)).size());
        Map<Integer, Integer> map = new LinkedHashMap<>();
        map.put(1, 2);
        Assert.assertEquals(Jie.mapOf(1, 2, 3), map);
        map.put(3, 4);
        Assert.assertEquals(Jie.mapOf(1, 2, 3, 4), map);
        Assert.expectThrows(UnsupportedOperationException.class, () -> Jie.asList(1, 2, 3).set(0, 5));
    }

    @Test
    public void testCollector() {
        GekCollector collector = Jie.collector();
        Assert.assertEquals(
            collector.reset().initialElements(1, 2, 3).toList(),
            Arrays.asList(1, 2, 3)
        );
        Assert.assertEquals(
            collector.reset().initialElements(1, 2, 3).immutable().toList(),
            Arrays.asList(1, 2, 3)
        );
        Assert.assertEquals(
            collector.reset().initialSize(3).initialFunction(i -> i).toList(),
            Arrays.asList(0, 1, 2)
        );
        Assert.assertEquals(
            collector.reset().initialSize(3).initialFunction(i -> i).immutable().toList(),
            Arrays.asList(0, 1, 2)
        );
        Assert.assertEquals(
            collector.reset().initialSize(3).initialFunction(i -> 1).toList(),
            Arrays.asList(1, 1, 1)
        );
        Assert.assertEquals(
            collector.reset().initialSize(3).initialFunction(i -> 1).immutable().toList(),
            Arrays.asList(1, 1, 1)
        );
        Assert.assertEquals(
            collector.reset().initialElements(Arrays.asList(1, 2, 3)).toSet(),
            new HashSet<>(Arrays.asList(1, 2, 3))
        );
        Assert.assertEquals(
            collector.reset().initialElements(Arrays.asList(1, 2, 3)).immutable().toSet(),
            new HashSet<>(Arrays.asList(1, 2, 3))
        );
        Assert.assertEquals(
            collector.reset().initialElements(Arrays.asList(1, 2, 2)).toSet(),
            new HashSet<>(Arrays.asList(1, 2))
        );
        Assert.assertEquals(
            collector.reset().initialElements(Arrays.asList(1, 2, 2)).immutable().toSet(),
            new HashSet<>(Arrays.asList(1, 2))
        );
        Assert.assertEquals(
            collector.reset().initialSize(3).initialFunction(i -> i).toSet(),
            new HashSet<>(Arrays.asList(0, 1, 2))
        );
        Assert.assertEquals(
            collector.reset().initialSize(3).initialFunction(i -> i).immutable().toSet(),
            new HashSet<>(Arrays.asList(0, 1, 2))
        );
        Assert.assertEquals(
            collector.reset().initialSize(3).initialFunction(i -> 1).toSet(),
            new HashSet<>(Collections.singletonList(1))
        );
        Assert.assertEquals(
            collector.reset().initialSize(3).initialFunction(i -> 1).immutable().toSet(),
            new HashSet<>(Collections.singletonList(1))
        );
        Assert.assertEquals(
            collector.reset().initialElements(Arrays.asList(1, 2, 2)).toSet().size(),
            new HashSet<>(Arrays.asList(1, 2)).size()
        );
        Assert.assertEquals(
            collector.reset().initialElements(Arrays.asList(1, 2, 2)).immutable().toSet().size(),
            new HashSet<>(Arrays.asList(1, 2)).size()
        );
        Map<Integer, Integer> map = new LinkedHashMap<>();
        map.put(1, 2);
        Assert.assertEquals(
            collector.reset().initialElements(1, 2, 3).toMap(),
            map
        );
        Assert.assertEquals(
            collector.reset().initialElements(1, 2, 3).immutable().toMap(),
            map
        );
        map.put(3, 4);
        Assert.assertEquals(
            collector.reset().initialElements(1, 2, 3, 4).toMap(),
            map
        );
        Assert.assertEquals(
            collector.reset().initialElements(1, 2, 3, 4).immutable().toMap(),
            map
        );
        Assert.assertEquals(
            collector.reset().initialFunction(i -> new AbstractMap.SimpleImmutableEntry<>(i, i)).toMap(),
            Collections.emptyMap()
        );
        Assert.assertEquals(
            collector.reset().initialFunction(i -> new AbstractMap.SimpleImmutableEntry<>(i, i)).immutable().toMap(),
            Collections.emptyMap()
        );
        Assert.assertEquals(
            collector.reset().initialFunction(i -> new AbstractMap.SimpleImmutableEntry<>(i, i)).initialSize(4).toMap(),
            Jie.mapOf(0, 0, 1, 1, 2, 2, 3, 3)
        );
        Assert.assertEquals(
            collector.reset().initialFunction(i -> new AbstractMap.SimpleImmutableEntry<>(i, i)).initialSize(4).immutable().toMap(),
            Jie.mapOf(0, 0, 1, 1, 2, 2, 3, 3)
        );
        Assert.assertEquals(
            collector.reset().initialFunction(i -> new AbstractMap.SimpleImmutableEntry<>(1, i)).initialSize(4).toMap(),
            Jie.mapOf(1, 3)
        );
        Assert.assertEquals(
            collector.reset().initialFunction(i -> new AbstractMap.SimpleImmutableEntry<>(1, i)).initialSize(4).immutable().toMap(),
            Jie.mapOf(1, 3)
        );
        map.remove(3);
        Assert.assertEquals(collector.reset().initialElements(1, 2, 3).toMap(), map);
        Assert.assertEquals(collector.reset().initialElements(1, 2, 3).immutable().toMap(), map);
        map.put(3, 4);
        Assert.assertEquals(collector.reset().initialElements(1, 2, 3, 4).toMap(), map);
        Assert.assertEquals(collector.reset().initialElements(1, 2, 3, 4).immutable().toMap(), map);

        Assert.assertTrue(collector.reset().initialElements(1, 2, 3).toList().add(4));
        Assert.expectThrows(UnsupportedOperationException.class, () ->
            collector.reset().initialElements(1, 2, 3).immutable().toList().add(4));
        List<Integer> list = collector.reset().initialCapacity(100).toList();
        list.add(2);
        list.add(3);
        Assert.assertEquals(list, Arrays.asList(2, 3));
        Assert.assertEquals(collector.reset().toArrayList().getClass(), ArrayList.class);
    }

    @Test
    public void testGetCompute() {
        List<Integer> list = Jie.asList(0, 1, 2);
        Assert.assertEquals(GekColl.get(list, 1), 1);
        Assert.assertEquals(GekColl.get(list, 1, 9), 1);
        Assert.assertEquals(GekColl.get(list, 8, 9), 9);
        Assert.assertEquals(GekColl.compute(list, 1, i -> i * 2), 1);
        Assert.assertEquals(GekColl.compute(list, 8, i -> i * 2), 16);
        Set<Integer> set = Jie.asSet(0, 1, 2);
        Assert.assertEquals(GekColl.get(set, 1), 1);
        Assert.assertEquals(GekColl.get(set, 1, 9), 1);
        Assert.assertEquals(GekColl.get(set, 8, 9), 9);
        Assert.assertEquals(GekColl.compute(set, 1, i -> i * 2), 1);
        Assert.assertEquals(GekColl.compute(set, 8, i -> i * 2), 16);
        Map<Integer, Integer> map = Jie.mapOf(1, 2, 3, 4);
        Assert.assertEquals(GekColl.get(map, 1), 2);
        Assert.assertEquals(GekColl.get(map, 1, 9), 2);
        Assert.assertEquals(GekColl.get(map, 8, 9), 9);
        Assert.assertEquals(GekColl.compute(map, 1, i -> i * 20), 2);
        Assert.assertEquals(GekColl.compute(map, 8, i -> i * 20), 160);
    }

    @Test
    public void testCollect() {
        Assert.assertEquals(
            GekColl.toMap(Jie.asList(1, 2, 3, 4)),
            Jie.mapOf(1, 2, 3, 4)
        );
        Assert.assertEquals(
            GekColl.toMap(Jie.asList(1, 2, 3, 4), String::valueOf, String::valueOf),
            Jie.mapOf("1", "1", "2", "2", "3", "3", "4", "4")
        );
        Assert.assertEquals(
            GekColl.toMap(Jie.mapOf(1, 2, 3, 4), String::valueOf, String::valueOf),
            Jie.mapOf("1", "2", "3", "4")
        );
        Assert.assertEquals(
            GekColl.collect(new LinkedHashMap<>(), 1, 2, 3),
            Jie.mapOf(1, 2, 3)
        );
        Assert.assertEquals(
            GekColl.collect(new LinkedHashMap<>(), 1, 2, 3, 4),
            Jie.mapOf(1, 2, 3, 4)
        );
        Assert.assertEquals(
            GekColl.collect(new LinkedHashMap<>(), Jie.asList(1, 2, 3)),
            Jie.mapOf(1, 2, 3)
        );
        Assert.assertEquals(
            GekColl.collect(new LinkedHashMap<>(), Jie.asList(1, 2, 3, 4)),
            Jie.mapOf(1, 2, 3, 4)
        );
        Assert.assertEquals(
            GekColl.collect(new LinkedHashMap<>(), Jie.asList(1, 2, 3, 4), String::valueOf),
            Jie.mapOf("1", "2", "3", "4")
        );
        Assert.assertEquals(
            GekColl.collect(new LinkedHashMap<>(), Jie.mapOf(1, 2, 3, 4), String::valueOf, String::valueOf),
            Jie.mapOf("1", "2", "3", "4")
        );
        Assert.assertEquals(
            GekColl.toList(Jie.asList(1, 2, 3, 4)),
            Jie.asList(1, 2, 3, 4)
        );
        Assert.assertEquals(
            GekColl.toSet(Jie.asList(1, 1, 3, 3)),
            Jie.asSet(1, 1, 3, 3)
        );
        Assert.assertEquals(
            GekColl.toSet(Jie.asList(1, 1, 3, 3)),
            new HashSet<>(Jie.asList(1, 1, 3, 3))
        );
    }
}
