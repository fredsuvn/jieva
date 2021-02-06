package test.java.xyz.srclab.common.test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.test.TestKit;

import java.util.Arrays;

/**
 * @author sunqian
 */
public class TestKitTest {

    @Test
    public void testTestKit() {
        Assert.assertTrue(TestKit.equalsIgnoreOrder(Arrays.asList(1, 2, 3), Arrays.asList(3, 2, 1)));
        Assert.assertFalse(TestKit.equalsIgnoreOrder(Arrays.asList(1, 2, 3), Arrays.asList(3, 2, 1, 0)));
    }
}
