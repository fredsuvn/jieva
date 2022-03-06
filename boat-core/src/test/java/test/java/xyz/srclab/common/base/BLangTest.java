package test.java.xyz.srclab.common.base;

import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BString;
import xyz.srclab.common.base.FinalObject;
import xyz.srclab.common.base.IndexedPredicate;
import xyz.srclab.common.base.IntRef;

import java.util.Arrays;

public class BLangTest {

    @Test
    public void testFinalObject() {

        IntRef i = IntRef.of(1);

        class TestFinal extends FinalObject {

            @Override
            protected int hashCode0() {
                int r = i.get();
                i.set(r + 1);
                return r;
            }

            @NotNull
            @Override
            protected String toString0() {
                return hashCode() + "";
            }
        }

        TestFinal tf = new TestFinal();
        Assert.assertEquals(i.get(), 1);
        Assert.assertEquals(tf.hashCode(), 1);
        Assert.assertEquals(i.get(), 2);
        Assert.assertEquals(tf.toString(), "1");
        Assert.assertEquals(i.get(), 2);
        Assert.assertEquals(tf.hashCode(), 1);
        Assert.assertEquals(tf.toString(), "1");
        Assert.assertEquals(i.get(), 2);
        i.set(100);
        Assert.assertEquals(tf.hashCode(), 1);
        Assert.assertEquals(tf.toString(), "1");
        Assert.assertEquals(i.get(), 100);
    }

    // @Test
    // public void testArraySeg() {
    //     int[] array = new int[]{31312, 31312, 3232, 454, 676, 8878};
    //     int offset = 2;
    //     ArraySeg<int[]> seg = new ArraySeg<>(array, offset);
    //     Assert.assertEquals(seg.absIndex(1), offset + 1);
    //     Assert.assertEquals(seg.absIndex(2), offset + 2);
    //     ArraySeg<int[]> seg2 = new ArraySeg<>(array, offset);
    //     Assert.assertEquals(seg2.absIndex(3), offset + 3);
    //     Assert.assertEquals(seg2, seg);
    //     Assert.assertEquals(seg2.array(), seg.array());
    //     Assert.assertEquals(seg2.copyOfRange(), seg.copyOfRange());
    //     Assert.assertEquals(seg2.copyOfRange(), Arrays.copyOfRange(seg.array(), seg.startIndex(), seg.endIndex()));
    //     Assert.assertEquals(seg2.copyOfRange(), Arrays.copyOfRange(array, offset, array.length));
    // }

    @Test
    public void testFun() {
        IndexedPredicate<String> predicate = (i, it) -> true;
    }
}
