package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Anys;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author sunqian
 */
public class AnysTest {

    @Test
    public void testEqual() {
        Assert.assertTrue(Anys.equals("", ""));
        Assert.assertFalse(Anys.equals(new int[]{1, 2, 3}, new int[]{1, 2, 3}));
        Assert.assertTrue(Anys.anyOrArrayEquals(new int[]{1, 2, 3}, new int[]{1, 2, 3}));
        Assert.assertTrue(Anys.anyOrArrayDeepEquals(
            new Object[]{1, new int[]{1, 2, 3}, 3}, new Object[]{1, new int[]{1, 2, 3}, 3}));
    }

    @Test
    public void testHash() {
        int[] iArray = new int[]{1, 2, 3};
        Assert.assertEquals(Anys.anyOrArrayHash(iArray), Arrays.hashCode(iArray));
        Object[] oArray = new Object[]{1, new int[]{1, 2, 3}, 3};
        Assert.assertEquals(Anys.anyOrArrayDeepHash(oArray), Arrays.deepHashCode(oArray));
        Assert.assertEquals(Anys.hash(1, 2, iArray), Objects.hash(1, 2, iArray));
    }
}
