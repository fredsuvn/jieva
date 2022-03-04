package test.java.xyz.srclab.common.base;

import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FinalObject;
import xyz.srclab.common.base.IntRef;

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
}
