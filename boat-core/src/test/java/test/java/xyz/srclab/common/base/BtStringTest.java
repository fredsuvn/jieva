package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.*;

/**
 * @author sunqian
 */
public class BtStringTest {

    @Test
    public void testString() {
        Assert.assertTrue(BtString.isNumeric("31312424242"));
        Assert.assertFalse(BtString.isNumeric("3131242s4242"));
        Assert.assertTrue(BtString.anyEquals("1", "", "2", "1", "3"));
        Assert.assertFalse(BtString.anyEquals("1", "", "2", "2", "3"));
        Assert.assertTrue(BtString.allEquals("1", "1", "1", "1"));
        Assert.assertFalse(BtString.anyEquals("1", "2", "3", "4"));
    }

    @Test
    public void testCharsRef() {
        char[] cs = {'0', '1', '2', '3'};
        CharsRef cr = CharsRef.of(cs);
        Assert.assertEquals(cr.charAt(1), '1');
        cs[1] = '6';
        Assert.assertEquals(cr.charAt(1), '6');
        Assert.assertEquals(cr.subSequence(2, 4).toString(), CharsRef.of(cs, 2).toString());
        Assert.assertEquals(cr.toString(), "0623");
        CharsRef cr2 = CharsRef.of(cr);
        Assert.assertEquals(cr2.charAt(1), '6');
        Assert.assertEquals(cr2.subSequence(2, 4).toString(), CharsRef.of(cr2, 2).toString());
        Assert.assertEquals(cr2.toString(), "0623");
    }

    //@Test
    //public void testLazyString() {
    //    Val<Integer> ref = Ref.of(1);
    //    LazyString ls = LazyString.of(() -> {
    //        ref.set(ref.get() + 1);
    //        return ref.get().toString();
    //    });
    //    Assert.assertEquals(ref.get().intValue(), 1);
    //    BLog.info("Value of laze string [1] is {}!", ls);
    //    Assert.assertEquals(ref.get().intValue(), 2);
    //    BLog.info("Value of laze string [2] is {}!", ls);
    //    Assert.assertEquals(ref.get().intValue(), 2);
    //}

    @Test
    public void test8Bit() {
        String randomString = BtRandom.randomString(100);
        byte[] bytes = BtString.getBytes8Bit(randomString);
        byte[] bytes2 = BtString.getBytes8Bit(randomString.toCharArray());
        byte[] expectedBytes = new byte[100];
        for (int i = 0; i < expectedBytes.length; i++) {
            expectedBytes[i] = (byte) randomString.charAt(i);
        }
        Assert.assertEquals(bytes, expectedBytes);
        Assert.assertEquals(bytes, bytes2);
        char[] expectedChars = new char[100];
        for (int i = 0; i < expectedBytes.length; i++) {
            expectedChars[i] = (char) (bytes[i] & 0xff);
        }
        Assert.assertEquals(randomString, new String(expectedChars));
        Assert.assertEquals(
            BtString.toString8Bit(bytes).toCharArray(),
            randomString.toCharArray()
        );
    }
}
