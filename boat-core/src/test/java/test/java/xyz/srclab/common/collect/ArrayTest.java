package test.java.xyz.srclab.common.collect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.collect.BArray;
import xyz.srclab.common.collect.BCollect;

/**
 * @author sunqian
 */
public class ArrayTest {

    @Test
    public void testArray() {
        String[] stringArray = BArray.arrayOf("1", "2", "3");
        BLog.info(BArray.joinToString(stringArray));
        Assert.assertEquals(
            BArray.joinToString(stringArray),
            "1, 2, 3"
        );
    }

    @Test
    public void testJoinToString() {
        String[] strings = BArray.arrayOf("1", "2", "3");
        Assert.assertEquals(BArray.joinToString(strings), "1, 2, 3");
        BArray.asList(strings).set(0, "111");
        BArray.asList(strings).set(1, "222");
        BArray.asList(strings).set(2, "333");
        Assert.assertEquals(BCollect.joinToString(BArray.asList(strings)), "111, 222, 333");

        int[] ints = {1, 2, 3};
        Assert.assertEquals(BArray.joinToString(ints), "1, 2, 3");
        BArray.asList(ints).set(0, 111);
        BArray.asList(ints).set(1, 222);
        BArray.asList(ints).set(2, 333);
        Assert.assertEquals(BCollect.joinToString(BArray.asList(ints)), "111, 222, 333");
    }
}
