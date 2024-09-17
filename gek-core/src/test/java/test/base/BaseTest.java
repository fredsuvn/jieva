package test.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.Tuple;

import java.util.*;

public class BaseTest {

    @Test
    public void testConvenient() {
        Assert.assertEquals(Jie.array(1, 2, 3), new Integer[]{1, 2, 3});
        Assert.assertEquals(Jie.list(1, 2, 3), Arrays.asList(1, 2, 3));
        Assert.assertEquals(Jie.list(1, 2, 3).get(1), 2);
        Assert.expectThrows(UnsupportedOperationException.class, () -> Jie.list(1, 2, 3).set(1, 2));
        Integer[] is = new Integer[]{1, 2, 3};
        List<Integer> list = Jie.list(is);
        Assert.assertEquals(list, Arrays.asList(1, 2, 3));
        is[1] = 888;
        Assert.assertEquals(list, Arrays.asList(1, 888, 3));
        Assert.assertEquals(Jie.set(1, 2, 3), new LinkedHashSet<>(Jie.list(1, 2, 3)));
        Assert.assertEquals(Jie.set(1, 2, 3, 3, 2), new LinkedHashSet<>(Jie.list(1, 2, 3)));
        Map<Integer, Integer> map = new LinkedHashMap<>();
        map.put(1, 2);
        Map<Integer, Integer> map2 = new LinkedHashMap<>(map);
        map2.put(3, 4);
        Assert.assertEquals(Jie.map(1, 2, 3), map);
        Assert.assertEquals(Jie.map(1, 2, 3, 4), map2);
    }

    @Test
    public void testTuple() {
        Tuple tuple = Tuple.of(0, "1", 2L);
        Assert.assertEquals(tuple.size(), 3);
        Assert.assertEquals(tuple.set(0, 0), tuple);
        Assert.assertEquals((Integer) tuple.get(0), 0);
        Assert.assertEquals(tuple.get(1), "1");
        Assert.assertEquals((Long) tuple.get(2), 2L);
        Assert.assertEquals(tuple, Tuple.of(0, "1", 2L));
        Assert.assertNotEquals(tuple, Tuple.of(0, "1", 22L));
        Assert.assertNotSame(tuple, Tuple.of(0, "1", 2L));
        tuple.set(1, "11");
        Assert.assertEquals(tuple.get(1), "11");
        Assert.assertEquals(tuple, Tuple.of(0, "11", 2L));
        tuple.set(1, new int[]{1, 2});
        tuple.set(2, Jie.arrayList("22", "33"));
        Assert.assertEquals(tuple, Tuple.of(0, new int[]{1, 2}, Jie.arrayList("22", "33")));
        Assert.assertEquals(tuple.hashCode(), Tuple.of(0, new int[]{1, 2}, Jie.arrayList("22", "33")).hashCode());

        Assert.assertEquals(Tuple.of(0, "1", 2L).toString(), "[0, 1, 2]");
        Assert.assertFalse(tuple.equals(""));
        Assert.assertFalse(tuple.equals(null));
    }
}
