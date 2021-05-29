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
        FastConverter fastConverter = FastConverter.newFastConverter(
            Arrays.asList(new IntToStringConvertHandler(), new NumberToStringConvertHandler()));
        logger.log("IntToStringConvertHandler: {}", fastConverter.convert(123, String.class));
        Assert.assertEquals(fastConverter.convert(123, String.class), "I123");
        logger.log("NumberToStringConvertHandler: {}", fastConverter.convert(123L, String.class));
        Assert.assertEquals(fastConverter.convert(123L, String.class), "N123");

        Assert.expectThrows(UnsupportedOperationException.class, () ->
            fastConverter.convert(new Object(), Object.class));
    }

    private static class IntToStringConvertHandler implements FastConvertHandler<Integer, String> {

        @NotNull
        @Override
        public Class<?> fromType() {
            return Integer.class;
        }

        @NotNull
        @Override
        public Class<?> toType() {
            return String.class;
        }

        @NotNull
        @Override
        public String convert(@NotNull Integer from) {
            return "I" + from;
        }
    }

    private static class NumberToStringConvertHandler implements FastConvertHandler<Number, String> {

        @NotNull
        @Override
        public Class<?> fromType() {
            return Number.class;
        }

        @NotNull
        @Override
        public Class<?> toType() {
            return String.class;
        }

        @NotNull
        @Override
        public String convert(@NotNull Number from) {
            return "N" + from;
        }
    }
}
