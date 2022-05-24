package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BObject;

import java.util.Arrays;

/**
 * @author sunqian
 */
public class BObjectTest {

    @Test
    public void testEqual() {
        Assert.assertTrue(BObject.equals("", ""));

        Assert.assertTrue(BObject.deepEquals(new int[]{1, 2, 3}, new int[]{1, 2, 3}));
        Assert.assertFalse(BObject.equals(new int[]{1, 2, 3}, new int[]{1, 2, 3}));

        Assert.assertTrue(BObject.deepEquals(
            new Object[]{1, new int[]{1, 2, 3}, 3}, new Object[]{1, new int[]{1, 2, 3}, 3}));
        Assert.assertFalse(BObject.equals(
            new Object[]{1, new int[]{1, 2, 3}, 3}, new Object[]{1, new int[]{1, 2, 3}, 3}));
    }

    @Test
    public void testHash() {
        Object obj = new Object();
        Assert.assertEquals(BObject.toHash(obj), obj.hashCode());
        Assert.assertNotEquals(BObject.toHash(new int[]{1, 2, 3}), BObject.toHash(new int[]{1, 2, 3}));

        Assert.assertEquals(BObject.arrayToHash(new int[]{1, 2, 3}), BObject.arrayToHash(new int[]{1, 2, 3}));
        Assert.assertNotEquals(
            BObject.hash(1, new int[]{1, 2, 3}, 3), BObject.hash(1, new int[]{1, 2, 3}, 3));

        Assert.assertEquals(
            BObject.deepHash(1, new int[]{1, 2, 3}, 3), BObject.deepHash(1, new int[]{1, 2, 3}, 3));
    }

    @Test
    public void testToString() {
        Object obj = new Object();
        Assert.assertEquals(BObject.toString(obj), obj.toString());
        BLog.info(BObject.arrayToString(new int[]{1, 2, 3}));
        Assert.assertEquals(BObject.arrayToString(new int[]{1, 2, 3}), Arrays.toString(new int[]{1, 2, 3}));
        BLog.info(BObject.deepToString(new Object[]{1, new int[]{1, 2, 3}, 3}));
        Assert.assertEquals(
            BObject.deepToString(new Object[]{1, new int[]{1, 2, 3}, 3}),
            Arrays.deepToString(new Object[]{1, new int[]{1, 2, 3}, 3})
        );
    }
}
