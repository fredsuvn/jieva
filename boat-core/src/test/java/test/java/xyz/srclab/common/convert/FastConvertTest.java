package test.java.xyz.srclab.common.convert;

import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.convert.FastConvertHandler;
import xyz.srclab.common.convert.FastConverter;
import xyz.srclab.common.test.TestLogger;

import java.util.Arrays;

/**
 * @author sunqian
 */
public class FastConvertTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testFastConvert() {
        FastConverter<String> fastConverter = FastConverter.newFastConverter(
            Arrays.asList(new CharsConvertHandler(), new StringConvertHandler()));
        logger.log(fastConverter.convert(new StringBuilder("123")));
        logger.log(fastConverter.convert("123"));
        Assert.assertEquals(fastConverter.convert(new StringBuilder("123")), "123");
        Assert.assertEquals(fastConverter.convert("123"), "123123");

        Assert.expectThrows(UnsupportedOperationException.class, () -> fastConverter.convert(new Object()));
    }

    private static class CharsConvertHandler implements FastConvertHandler<String> {

        @NotNull
        @Override
        public Class<?> supportedType() {
            return CharSequence.class;
        }

        @Override
        public String convert(@NotNull Object from) {
            logger.log("By {}", getClass());
            return from.toString();
        }
    }

    private static class StringConvertHandler implements FastConvertHandler<String> {

        @NotNull
        @Override
        public Class<?> supportedType() {
            return String.class;
        }

        @Override
        public String convert(@NotNull Object from) {
            logger.log("By {}", getClass());
            return from + from.toString();
        }
    }
}
