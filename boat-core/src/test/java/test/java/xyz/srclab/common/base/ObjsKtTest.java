package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Anys;
import xyz.srclab.common.logging.Logs;

import java.util.Arrays;

/**
 * @author sunqian
 */
public class ObjsKtTest {

    @Test
    public void testEqual() {
        Assert.assertTrue(Anys.equals("", ""));

        Assert.assertTrue(Anys.deepEquals(new int[]{1, 2, 3}, new int[]{1, 2, 3}));
        Assert.assertFalse(Anys.equals(new int[]{1, 2, 3}, new int[]{1, 2, 3}));

        Assert.assertTrue(Anys.deepEquals(
            new Object[]{1, new int[]{1, 2, 3}, 3}, new Object[]{1, new int[]{1, 2, 3}, 3}));
        Assert.assertFalse(Anys.equals(
            new Object[]{1, new int[]{1, 2, 3}, 3}, new Object[]{1, new int[]{1, 2, 3}, 3}));
    }

    @Test
    public void testHash() {
        Object obj = new Object();
        Assert.assertEquals(Anys.hash(obj), obj.hashCode());
        Assert.assertNotEquals(Anys.hash(new int[]{1, 2, 3}), Anys.hash(new int[]{1, 2, 3}));

        Assert.assertEquals(Anys.arrayHash(new int[]{1, 2, 3}), Anys.arrayHash(new int[]{1, 2, 3}));
        Assert.assertNotEquals(
            Anys.arrayHash(1, new int[]{1, 2, 3}, 3), Anys.arrayHash(1, new int[]{1, 2, 3}, 3));

        Assert.assertEquals(
            Anys.deepHash(1, new int[]{1, 2, 3}, 3), Anys.deepHash(1, new int[]{1, 2, 3}, 3));
    }

    @Test
    public void testToString() {
        Object obj = new Object();
        Assert.assertEquals(Anys.toString(obj), obj.toString());
        Logs.info(Anys.arrayToString(new int[]{1, 2, 3}));
        Assert.assertEquals(Anys.arrayToString(new int[]{1, 2, 3}), Arrays.toString(new int[]{1, 2, 3}));
        Logs.info(Anys.deepToString(new Object[]{1, new int[]{1, 2, 3}, 3}));
        Assert.assertEquals(
            Anys.deepToString(new Object[]{1, new int[]{1, 2, 3}, 3}),
            Arrays.deepToString(new Object[]{1, new int[]{1, 2, 3}, 3})
        );
    }
}
