package test.java.xyz.srclab.common.convert;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.convert.FastConvertMethod;
import xyz.srclab.common.convert.FastConverter;
import xyz.srclab.common.convert.UnsupportedConvertException;
import xyz.srclab.common.logging.Logs;

/**
 * @author sunqian
 */
public class FastConvertTest {

    @Test
    public void testFastConvert() {
        FastConverter fastConverter = FastConverter.newFastConverter(new FastHandler());
        String intToString = fastConverter.convert(123, int.class, String.class);
        Logs.info("intToString: {}", intToString);
        Assert.assertEquals(intToString, "123");
        int stringToInt = fastConverter.convert("123", int.class);
        Logs.info("stringToInt: {}", stringToInt);
        Assert.assertEquals(stringToInt, 123);

        Assert.expectThrows(UnsupportedConvertException.class, () ->
            fastConverter.convert(new Object(), Object.class));
    }

    public static class FastHandler {

        @FastConvertMethod
        public String intToString(int i) {
            return String.valueOf(i);
        }

        @FastConvertMethod
        public int stringToInt(String str) {
            return Integer.parseInt(str);
        }
    }
}
