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
        String[] stringArray = BArray.newArray("1", "2", "3");
        BLog.info(BArray.joinToString(stringArray));
        Assert.assertEquals(
            BArray.joinToString(stringArray),
            "1, 2, 3"
        );
    }

    @Test
    public void testJoinToString() {
        String[] strings = BArray.newArray("1", "2", "3");
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

    @Test
    public void testIndexOfArray() {
        String[] array = new String[]{"1", "2", "3"};
        Assert.assertEquals(
            BArray.indexOf(array, new String[]{"1", "2", "3"}),
            0
        );
        Assert.assertEquals(
            BArray.indexOf(array, new String[]{"1", "2"}),
            0
        );
        Assert.assertEquals(
            BArray.indexOf(array, new String[]{"1", "3"}),
            -1
        );
        Assert.assertEquals(
            BArray.indexOf(array, new String[]{"2", "3"}),
            1
        );
        Assert.assertEquals(
            BArray.indexOf(array, new String[]{"2"}),
            1
        );
        Assert.assertEquals(
            BArray.indexOf(array, new String[]{"2", "4"}),
            -1
        );
        Assert.assertEquals(
            BArray.indexOf(array, new String[]{"3"}),
            2
        );
        Assert.assertEquals(
            BArray.indexOf(array, new String[]{"3", "4"}),
            -1
        );
        Assert.assertEquals(
            BArray.indexOf(array, new String[]{"4"}),
            -1
        );

        String[] array2 = new String[]{"1", "2", "3", "2", "3"};
        Assert.assertEquals(
            BArray.indexOf(array2, 1, new String[]{"2", "3"}),
            1
        );
        Assert.assertEquals(
            BArray.indexOf(array2, 2, new String[]{"2", "3"}),
            3
        );
    }

    @Test
    public void testAddRemove() {
        int[] a = {};
        int[] a2 = BArray.add(a, 1, 0);
        Assert.assertEquals(a2, new int[]{1});
        int[] a3 = BArray.add(a2, 2, 0);
        Assert.assertEquals(a3, new int[]{2, 1});
        int[] a4 = BArray.add(a3, 3, 2);
        Assert.assertEquals(a4, new int[]{2, 1, 3});
        int[] a5 = BArray.add(a4, 9, 1);

        Assert.assertEquals(a5, new int[]{2, 9, 1, 3});
        int[] a6 = BArray.remove(a5, 3);
        Assert.assertEquals(a6, new int[]{2, 9, 1});
        int[] a7 = BArray.remove(a6, 0);
        Assert.assertEquals(a7, new int[]{9, 1});
        int[] a8 = BArray.remove(a6, 1);
        Assert.assertEquals(a8, new int[]{2, 1});

        Assert.expectThrows(IndexOutOfBoundsException.class, () -> BArray.add(a, 1, -1));
        Assert.expectThrows(IndexOutOfBoundsException.class, () -> BArray.add(a, 1, 9));
    }
}
