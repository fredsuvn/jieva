package test.java.xyz.srclab.common.collect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.collect.BCollect;
import xyz.srclab.common.collect.BSet;

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
}
