package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.base.Tuple;

public class BaseTest {

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
