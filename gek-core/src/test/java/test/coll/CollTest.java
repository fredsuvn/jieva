package test.coll;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.coll.JieColl;

import java.util.*;

public class CollTest {

    @Test
    public void testEmpty() {
        // Testing with null
        Assert.assertTrue(JieColl.isEmpty((Iterable<?>) null));
        Assert.assertFalse(JieColl.isNotEmpty((Iterable<?>) null));
        // Testing with empty collection
        Assert.assertTrue(JieColl.isEmpty(Collections.emptyList()));
        Assert.assertFalse(JieColl.isNotEmpty(Collections.emptyList()));
        // Testing with non-empty collection
        Assert.assertFalse(JieColl.isEmpty(Jie.list(1, 2, 3)));
        Assert.assertTrue(JieColl.isNotEmpty(Jie.list(1, 2, 3)));
        // Testing with empty iterable
        Assert.assertTrue(JieColl.isEmpty(iterable()));
        Assert.assertFalse(JieColl.isNotEmpty(iterable()));
        Assert.assertTrue(JieColl.isNotEmpty(iterable(new Object[10])));
        // Testing with non-empty iterable
        Assert.assertFalse(JieColl.isEmpty(Collections.singletonList(1)));
        Assert.assertTrue(JieColl.isNotEmpty(Collections.singletonList(1)));
        // Testing with empty map
        Assert.assertTrue(JieColl.isEmpty(Collections.emptyMap()));
        Assert.assertFalse(JieColl.isNotEmpty(Collections.emptyMap()));
        // Testing with empty map
        Assert.assertTrue(JieColl.isEmpty((Map<?, ?>) null));
        Assert.assertFalse(JieColl.isNotEmpty((Map<?, ?>) null));
        // Testing with non-empty map
        Assert.assertFalse(JieColl.isEmpty(Collections.singletonMap("key", "value")));
        Assert.assertTrue(JieColl.isNotEmpty(Collections.singletonMap("key", "value")));
    }

    @Test
    public void testAsList() {
        Assert.assertEquals(Jie.list(), Collections.emptyList());
        Assert.assertEquals(Jie.list(1, 2, 3), Arrays.asList(1, 2, 3));
        Assert.expectThrows(UnsupportedOperationException.class, () -> Jie.list(1, 2, 3).set(0, 2));
        Integer[] is = {2, 3, 4};
        Assert.assertEquals(Jie.list(is), Arrays.asList(2, 3, 4));
        is[0] = 666;
        Assert.assertEquals(Jie.list(is), Arrays.asList(666, 3, 4));
    }

    @Test
    public void testToArray() {
        // Testing toObjectArray method
        Assert.assertEquals(JieColl.toObjectArray(iterable()), new Object[0]);
        Assert.assertEquals(JieColl.toObjectArray(iterable(1, 2, 3)), Jie.array(1, 2, 3));
        Assert.assertEquals(JieColl.toObjectArray(Jie.list(1, 2, 3)), new Object[]{1, 2, 3});

        // Testing toArray method for Iterable
        Assert.expectThrows(UnsupportedOperationException.class, () -> JieColl.toArray(Collections.emptyList()));
        Assert.assertEquals(JieColl.toArray(Jie.list(1, 2, 3)), Jie.array(1, 2, 3));
        Assert.expectThrows(UnsupportedOperationException.class, () -> JieColl.toArray(Jie.list(null, null, null)));
        Assert.assertEquals(JieColl.toArray(Jie.list(null, 2, null)), Jie.array(null, 2, null));
        Assert.expectThrows(UnsupportedOperationException.class, () -> JieColl.toArray(iterable()));
        Assert.assertEquals(JieColl.toArray(iterable(1, 2, 3)), Jie.array(1, 2, 3));
        Assert.expectThrows(UnsupportedOperationException.class, () -> JieColl.toArray(iterable(null, null, null)));
        Assert.assertEquals(JieColl.toArray(iterable(null, 2, null)), Jie.array(null, 2, null));
        Assert.expectThrows(UnsupportedOperationException.class, () -> JieColl.toArray((Iterable<?>) Collections.emptyList()));
        Assert.assertEquals(JieColl.toArray((Iterable<?>) Jie.list(1, 2, 3)), Jie.array(1, 2, 3));
        Assert.expectThrows(UnsupportedOperationException.class, () -> JieColl.toArray((Iterable<?>) Jie.list(null, null, null)));
        Assert.assertEquals(JieColl.toArray((Iterable<?>) Jie.list(null, 2, null)), Jie.array(null, 2, null));

        Assert.assertEquals(JieColl.toArray(Jie.list(1, 2, 3), Integer.class), Jie.array(1, 2, 3));
        Assert.assertEquals(JieColl.toArray(Jie.list(), Integer.class), new Integer[0]);
        Assert.assertEquals(JieColl.toArray(Jie.list(1, 2, 3), Long.class, i -> (long) i), Jie.array(1L, 2L, 3L));
        Assert.assertEquals(JieColl.toArray(Jie.list(), Long.class, i -> (long) i), new Long[0]);
    }

    @Test
    public void testToCollection() {
        // list
        Assert.assertEquals(JieColl.toList(Jie.array()), Collections.emptyList());
        Assert.assertEquals(JieColl.toList(Jie.array(1, 2, 3)), Jie.list(1, 2, 3));
        Assert.assertEquals(JieColl.toList(Jie.list(1, 2, 3)), Jie.list(1, 2, 3));
        Assert.assertEquals(JieColl.toList(Collections.emptyList()), Collections.emptyList());
        Assert.expectThrows(UnsupportedOperationException.class, () -> JieColl.asImmutableList(Jie.array(1, 2, 3)).set(0, 2));
        Assert.assertEquals(JieColl.toList(Jie.array(1, 2, 3)).get(1), 2);
        Assert.assertEquals(JieColl.toStringList(Jie.list(1, 2, 3)), Jie.list("1", "2", "3"));
        Assert.assertEquals(JieColl.toStringList(Jie.array(1, 2, 3)), Jie.list("1", "2", "3"));
        Assert.assertEquals(JieColl.toStringList(Collections.emptyList()), Collections.emptyList());
        Assert.assertEquals(JieColl.toList(Jie.list(), Object::toString), Collections.emptyList());
        Assert.assertEquals(JieColl.toList(Jie.array(), Object::toString), Collections.emptyList());
        // set
        Assert.assertEquals(JieColl.toSet(Jie.array()), Collections.emptySet());
        Assert.assertEquals(JieColl.toSet(Jie.array(1, 2, 3)), new LinkedHashSet<>(Jie.list(1, 2, 3)));
        Assert.assertEquals(JieColl.toSet(Jie.list(1, 2, 3)), new LinkedHashSet<>(Jie.list(1, 2, 3)));
        Assert.assertEquals(JieColl.toSet(Collections.emptySet()), Collections.emptySet());
        Assert.expectThrows(UnsupportedOperationException.class, () -> JieColl.toSet(Jie.array(1, 2, 3)).add(1));
        Assert.assertEquals(JieColl.toSet(Jie.array(1, 2, 3)).iterator().next(), 1);
        Assert.assertEquals(JieColl.toStringSet(Jie.list(1, 2, 3)), new LinkedHashSet<>(Jie.list("1", "2", "3")));
        Assert.assertEquals(JieColl.toStringSet(Jie.array(1, 2, 3)), new LinkedHashSet<>(Jie.list("1", "2", "3")));
        Assert.assertEquals(JieColl.toStringSet(Collections.emptySet()), Collections.emptySet());
        Assert.assertEquals(JieColl.toSet(Jie.list(), Object::toString), Collections.emptyList());
        Assert.assertEquals(JieColl.toSet(Jie.array(), Object::toString), Collections.emptyList());
        // map
        Assert.assertEquals(JieColl.toMap(Jie.array()), Collections.emptyMap());
        Map<Integer, Integer> map = new LinkedHashMap<>();
        map.put(1, 2);
        Map<Integer, Integer> map2 = new LinkedHashMap<>(map);
        map2.put(3, 4);
        Assert.assertEquals(JieColl.toMap(Jie.array(1, 2, 3)), map);
        Assert.assertEquals(JieColl.toMap(Jie.array(1, 2, 3, 4)), map2);
        Assert.assertEquals(JieColl.toMap(Collections.emptyList()), Collections.emptyMap());
        Assert.assertEquals(JieColl.toMap(Jie.list(1, 2, 3)), map);
        Assert.assertEquals(JieColl.toMap(Jie.list(1, 2, 3, 4)), map2);
        Properties props = new Properties();
        props.put(1, 2);
        props.put(3, 4);
        Map<String, String> strMap = new LinkedHashMap<>();
        strMap.put("1", "2");
        Map<String, String> strMap2 = new LinkedHashMap<>(strMap);
        strMap2.put("3", "4");
        Assert.assertEquals(JieColl.toStringMap(props), strMap2);
        Assert.assertEquals(JieColl.toMap(map2, String::valueOf, String::valueOf), strMap2);
        Assert.assertEquals(JieColl.toMap(Collections.emptyMap(), String::valueOf, String::valueOf), Collections.emptyMap());
        // pairToMap
        Map<Long, Long> lmap = new LinkedHashMap<>();
        lmap.put(1L * 2, 1L * 3);
        lmap.put(2L * 2, 2L * 3);
        Assert.assertEquals(JieColl.toPairs(Jie.array(1, 2), i -> (long) i * 2, i -> (long) i * 3), lmap);
        Assert.assertEquals(JieColl.toPairs(Jie.array(), i -> (long) i * 2, i -> (long) i * 3), Collections.emptyMap());
        Assert.assertEquals(JieColl.toPairs(Jie.list(1, 2), i -> (long) i * 2, i -> (long) i * 3), lmap);
        Assert.assertEquals(JieColl.toPairs(Collections.emptyList(), i -> (long) i, i -> (long) i), Collections.emptyMap());
        // addAll
        Assert.assertEquals(JieColl.addAll(new ArrayList<>(), Jie.array()), Collections.emptyList());
        Assert.assertEquals(JieColl.addAll(new ArrayList<>(), Jie.list()), Collections.emptyList());
        Assert.assertEquals(JieColl.addAll(new ArrayList<>(), Jie.array(), Object::toString), Collections.emptyList());
        Assert.assertEquals(JieColl.addAll(new ArrayList<>(), Jie.list(), Object::toString), Collections.emptyList());
        // putAll
        Assert.assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.array()), Collections.emptyMap());
        Assert.assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.list()), Collections.emptyMap());
        Assert.assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.array(1, 2, 3), String::valueOf), strMap);
        Assert.assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.array(1, 2, 3, 4), String::valueOf), strMap2);
        Assert.assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.list(1, 2, 3), String::valueOf), strMap);
        Assert.assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.list(1, 2, 3, 4), String::valueOf), strMap2);
        Assert.assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.array(), String::valueOf), Collections.emptyMap());
        Assert.assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.list(), String::valueOf), Collections.emptyMap());
        Assert.assertEquals(JieColl.putAll(new LinkedHashMap<>(), Collections.emptyMap(), String::valueOf, String::valueOf), Collections.emptyMap());
        Assert.assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.array(), String::valueOf, String::valueOf), Collections.emptyMap());
        Assert.assertEquals(JieColl.putAll(new LinkedHashMap<>(), Jie.list(), String::valueOf, String::valueOf), Collections.emptyMap());
    }

    @Test
    public void testOr() {
        List<Integer> list = Jie.list(1, 2, 3);
        Assert.assertSame(JieColl.orList(list), list);
        Assert.assertNotSame(JieColl.orList(iterable(1, 2, 3)), list);
        Assert.assertEquals(JieColl.orList(iterable(1, 2, 3)), list);
        Set<Integer> set = Jie.set(1, 2, 3);
        Assert.assertSame(JieColl.orSet(set), set);
        Assert.assertNotSame(JieColl.orSet(iterable(1, 2, 3)), set);
        Assert.assertEquals(JieColl.orSet(iterable(1, 2, 3)), set);
        Collection<Integer> collection = Jie.set(1, 2, 3);
        Assert.assertSame(JieColl.orCollection(collection), collection);
        Assert.assertNotSame(JieColl.orCollection(iterable(1, 2, 3)), collection);
        Assert.assertEquals(JieColl.orCollection(iterable(1, 2, 3)), collection);
    }

    @Test
    public void testEnumeration() {
        Assert.assertEquals(JieColl.asIterable(null),Collections.emptyList());
        Enumeration<Integer> enumeration = JieColl.asEnumeration(Jie.list(1, 2, 3));
        Assert.assertEquals(enumeration.nextElement(),1);
        Assert.assertEquals(enumeration.nextElement(),2);
        Assert.assertEquals(enumeration.nextElement(),3);
        Assert.assertFalse(enumeration.hasMoreElements());
        Iterable<Integer> iterable = JieColl.asIterable(JieColl.asEnumeration(Jie.list(1, 2, 3)));
        Iterator<Integer> iterator = iterable.iterator();
        Assert.assertEquals(iterator.next(),1);
        Assert.assertEquals(iterator.next(),2);
        Assert.assertEquals(iterator.next(),3);
        Assert.assertFalse(iterator.hasNext());
    }

    private <T> Iterable<T> iterable(T... array) {
        return () -> new Iterator<T>() {

            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < array.length;
            }

            @Override
            public T next() {
                return array[i++];
            }
        };
    }
}
