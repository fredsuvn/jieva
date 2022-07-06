package test.java.xyz.srclab.common.base;

import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Bt;
import xyz.srclab.common.base.BtLog;
import xyz.srclab.common.base.FinalClass;
import xyz.srclab.common.base.IntVar;
import xyz.srclab.common.collect.BtList;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author sunqian
 */
public class BtTest {

    @Test
    public void testDefault() {
        System.out.println("Default radix: " + Bt.defaultRadix());
        System.out.println("Default charset: " + Bt.defaultCharset());
        System.out.println("Default bufferSize: " + Bt.defaultBufferSize());
        System.out.println("Default locale: " + Bt.defaultLocale());
        System.out.println("Default serialVersion: " + Bt.defaultSerialVersion());
        System.out.println("Default timestampPattern: " + Bt.defaultTimestampPattern());
        System.out.println("Default concurrency level: " + Bt.defaultConcurrencyLevel());
        System.out.println("Default null string: " + Bt.defaultNullString());
    }

    @Test
    public void testEqual() {
        Assert.assertTrue(Bt.equals("", ""));

        Assert.assertTrue(Bt.deepEquals(new int[]{1, 2, 3}, new int[]{1, 2, 3}));
        Assert.assertFalse(Bt.equals(new int[]{1, 2, 3}, new int[]{1, 2, 3}));

        Assert.assertTrue(Bt.deepEquals(
            new Object[]{1, new int[]{1, 2, 3}, 3}, new Object[]{1, new int[]{1, 2, 3}, 3}));
        Assert.assertFalse(Bt.equals(
            new Object[]{1, new int[]{1, 2, 3}, 3}, new Object[]{1, new int[]{1, 2, 3}, 3}));
    }

    @Test
    public void testHash() {
        Object obj = new Object();
        Assert.assertEquals(Bt.hash(obj), obj.hashCode());
        Assert.assertNotEquals(Bt.hash(new int[]{1, 2, 3}), Bt.hash(new int[]{1, 2, 3}));

        Assert.assertEquals(Bt.hash(new int[]{1, 2, 3}), Bt.hash(new int[]{1, 2, 3}));
        Assert.assertNotEquals(
            Bt.hash(1, new int[]{1, 2, 3}, 3), Bt.hash(1, new int[]{1, 2, 3}, 3));

        Assert.assertEquals(
            Bt.deepHash(1, new int[]{1, 2, 3}, 3), Bt.deepHash(1, new int[]{1, 2, 3}, 3));
    }

    @Test
    public void testToString() {
        Object obj = new Object();
        Assert.assertEquals(Bt.toString(obj), obj.toString());
        BtLog.info(Bt.arrayToString(new int[]{1, 2, 3}));
        Assert.assertEquals(Bt.arrayToString(new int[]{1, 2, 3}), Arrays.toString(new int[]{1, 2, 3}));
        BtLog.info(Bt.deepToString(new Object[]{1, new int[]{1, 2, 3}, 3}));
        Assert.assertEquals(
            Bt.deepToString(new Object[]{1, new int[]{1, 2, 3}, 3}),
            Arrays.deepToString(new Object[]{1, new int[]{1, 2, 3}, 3})
        );
    }

    @Test
    public void testProperties() {
        InputStream inputStream = Bt.loadStream("META-INF/test.properties");
        assert inputStream != null;
        Map<String, String> properties = Bt.readProperties(inputStream);
        Assert.assertEquals(properties.get("info"), "123");
    }

    @Test
    public void testLoadAll() {
        List<String> strings = Bt.loadStrings("META-INF/test.properties");
        Assert.assertEquals(strings, BtList.newList("info=123"));
    }

    @Test
    public void testFinalClass() {

        IntVar i = IntVar.of(1);

        class TestFinal extends FinalClass {

            @Override
            protected int hashCode0() {
                int r = i.getValue();
                i.setValue(r + 1);
                return r;
            }

            @NotNull
            @Override
            protected String toString0() {
                return hashCode() + "";
            }
        }

        TestFinal tf = new TestFinal();
        Assert.assertEquals(i.getValue(), 1);
        Assert.assertEquals(tf.hashCode(), 1);
        Assert.assertEquals(i.getValue(), 2);
        Assert.assertEquals(tf.toString(), "1");
        Assert.assertEquals(i.getValue(), 2);
        Assert.assertEquals(tf.hashCode(), 1);
        Assert.assertEquals(tf.toString(), "1");
        Assert.assertEquals(i.getValue(), 2);
        i.setValue(100);
        Assert.assertEquals(tf.hashCode(), 1);
        Assert.assertEquals(tf.toString(), "1");
        Assert.assertEquals(i.getValue(), 100);
    }
}
