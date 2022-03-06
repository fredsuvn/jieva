package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.*;

/**
 * @author sunqian
 */
public class BStringTest {

    @Test
    public void testString() {
        Assert.assertTrue(BString.isNumeric("31312424242"));
        Assert.assertFalse(BString.isNumeric("3131242s4242"));
        Assert.assertTrue(BString.equalsAny("1", "", "2", "1", "3"));
        Assert.assertFalse(BString.equalsAny("1", "", "2", "2", "3"));
        Assert.assertTrue(BString.equalsAll("1", "1", "1", "1"));
        Assert.assertFalse(BString.equalsAny("1", "2", "3", "4"));
    }

    @Test
    public void testStringRef() {
        char[] cs = {'0', '1', '2', '3'};
        CharsRef sr = CharsRef.of(cs);
        Assert.assertEquals(sr.charAt(1), '1');
        cs[1] = '6';
        Assert.assertEquals(sr.charAt(1), '6');
        Assert.assertEquals(sr.subSequence(2, 4).toString(), CharsRef.of(cs, 2).toString());
        Assert.assertEquals(sr.toString(), "0623");
    }

    @Test
    public void testLazyString() {
        Ref<Integer> ref = Ref.of(1);
        LazyString ls = LazyString.of(() -> {
            ref.set(ref.get() + 1);
            return ref.get().toString();
        });
        Assert.assertEquals(ref.get().intValue(), 1);
        BLog.info("Value of laze string [1] is {}!", ls);
        Assert.assertEquals(ref.get().intValue(), 2);
        BLog.info("Value of laze string [2] is {}!", ls);
        Assert.assertEquals(ref.get().intValue(), 2);
    }

    @Test
    public void test8Bit() {
        String randomString = BRandom.randomString(100);
        byte[] bytes = BString.to8BitBytes(randomString);
        byte[] bytes2 = BString.to8BitBytes(randomString.toCharArray());
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
            BString.to8BitChars(bytes),
            randomString.toCharArray()
        );
    }
}
