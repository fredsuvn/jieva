package test.java.xyz.srclab.common.convert;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.convert.FastConvertHandler;
import xyz.srclab.common.convert.FastConverter;
import xyz.srclab.common.convert.UnsupportedConvertException;
import xyz.srclab.common.test.TestLogger;

/**
 * @author sunqian
 */
public class FastConvertTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testFastConvert() {
        FastConverter fastConverter = FastConverter.newFastConverter(new FastHandler());
        String intToString = fastConverter.convert(123, int.class, String.class);
        logger.log("intToString: {}", intToString);
        Assert.assertEquals(intToString, "123");
        int stringToInt = fastConverter.convert("123", int.class);
        logger.log("stringToInt: {}", stringToInt);
        Assert.assertEquals(stringToInt, 123);

        Assert.expectThrows(UnsupportedConvertException.class, () ->
            fastConverter.convert(new Object(), Object.class));
    }

    public static class FastHandler {

        @FastConvertHandler
        public String intToString(int i) {
            return String.valueOf(i);
        }

        @FastConvertHandler
        public int stringToInt(String str) {
            return Integer.parseInt(str);
        }
    }
}
