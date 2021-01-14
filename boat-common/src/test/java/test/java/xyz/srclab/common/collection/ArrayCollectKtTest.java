package test.java.xyz.srclab.common.collection;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.collection.ArrayKit;
import xyz.srclab.common.collection.ListOps;
import xyz.srclab.common.test.TestLogger;

/**
 * @author sunqian
 */
public class ArrayCollectKtTest {

    private static final TestLogger testLogger = TestLogger.DEFAULT;

    @Test
    public void testArray() {
        String[] stringArray = ArrayKit.newArray("1", "2", "3");
        testLogger.log(ArrayKit.arrayJoinToString(stringArray));
        Assert.assertEquals(
                ArrayKit.arrayJoinToString(stringArray),
                "1, 2, 3"
        );
    }

    @Test
    public void testList() {
        String[] strings = ArrayKit.newArray("1", "2", "3");
        ArrayKit.asList(strings).set(0, "111");
        ArrayKit.asList(strings).set(1, "222");
        ArrayKit.asList(strings).set(2, "333");
        Assert.assertEquals(
                ArrayKit.arrayJoinToString(strings),
                "111, 222, 333"
        );
        Assert.assertEquals(
                ListOps.joinToString(ArrayKit.asList(strings)),
                "111, 222, 333"
        );

        int[] ints = {1, 2, 3};
        ArrayKit.asList(ints).set(0, 111);
        ArrayKit.asList(ints).set(1, 222);
        ArrayKit.asList(ints).set(2, 333);
        Assert.assertEquals(
                ArrayKit.arrayJoinToString(ints),
                "111, 222, 333"
        );
        Assert.assertEquals(
                ListOps.joinToString(ArrayKit.asList(ints)),
                "111, 222, 333"
        );
    }
}
