package test.java.xyz.srclab.common.collect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.collect.ArrayCollects;
import xyz.srclab.common.collect.Collects;
import xyz.srclab.common.test.TestLogger;

/**
 * @author sunqian
 */
public class ArrayCollectsTest {

    private static final TestLogger testLogger = TestLogger.DEFAULT;

    @Test
    public void testArray() {
        String[] stringArray = ArrayCollects.newArray("1", "2", "3");
        testLogger.log(ArrayCollects.joinToString(stringArray));
        Assert.assertEquals(
                ArrayCollects.joinToString(stringArray),
                "1, 2, 3"
        );
    }

    @Test
    public void testList() {
        String[] strings = ArrayCollects.newArray("1", "2", "3");
        ArrayCollects.asList(strings).set(0, "111");
        ArrayCollects.asList(strings).set(1, "222");
        ArrayCollects.asList(strings).set(2, "333");
        Assert.assertEquals(
                ArrayCollects.joinToString(strings),
                "111, 222, 333"
        );
        Assert.assertEquals(
                Collects.joinToString(ArrayCollects.asList(strings)),
                "111, 222, 333"
        );

        int[] ints = {1, 2, 3};
        ArrayCollects.asList(ints).set(0, 111);
        ArrayCollects.asList(ints).set(1, 222);
        ArrayCollects.asList(ints).set(2, 333);
        Assert.assertEquals(
                ArrayCollects.joinToString(ints),
                "111, 222, 333"
        );
        Assert.assertEquals(
                Collects.joinToString(ArrayCollects.asList(ints)),
                "111, 222, 333"
        );
    }
}
