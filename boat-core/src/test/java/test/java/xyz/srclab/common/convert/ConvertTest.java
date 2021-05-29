package test.java.xyz.srclab.common.convert;

import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.convert.ConvertHandler;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.convert.Converts;
import xyz.srclab.common.lang.Next;
import xyz.srclab.common.test.TestLogger;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author sunqian
 */
public class ConvertTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testConverts() {
        E e = Converts.convert("b", E.class);
        logger.log("e: {}", e);
    }

    @Test
    public void testSameToTypeFastHit() {
        Converter converter = Converter.newConverter(
            Arrays.asList(new IntToStringHandler(), new LongToStringHandler()));
        logger.log("Convert int: {}", converter.convert(100, String.class));
        Assert.assertEquals(converter.convert(100, String.class), "I100");
        logger.log("Convert long: {}", converter.convert(100L, String.class));
        Assert.assertEquals(converter.convert(100L, String.class), "L100");
    }

    public static class IntToStringHandler implements ConvertHandler {

        @NotNull
        @Override
        public List<Type> toTypeFastHit() {
            return Collections.singletonList(String.class);
        }

        public <T> Object convert(Object from, @NotNull Class<T> toTye, @NotNull Converter converter) {
            if (from == null || !from.getClass().equals(Integer.class)) {
                return Next.CONTINUE;
            }
            return "I" + from;
        }

        public Object convert(Object from, @NotNull Type Type, @NotNull Converter converter) {
            if (from == null || !from.getClass().equals(Integer.class)) {
                return Next.CONTINUE;
            }
            return "I" + from;
        }

        public Object convert(Object from, @NotNull Type fromType, @NotNull Type toType, @NotNull Converter converter) {
            if (from == null || !from.getClass().equals(Integer.class)) {
                return Next.CONTINUE;
            }
            return "I" + from;
        }
    }

    public static class LongToStringHandler implements ConvertHandler {

        @NotNull
        @Override
        public List<Type> toTypeFastHit() {
            return Collections.singletonList(String.class);
        }

        public <T> Object convert(Object from, @NotNull Class<T> toTye, @NotNull Converter converter) {
            if (from == null || !from.getClass().equals(Long.class)) {
                return Next.CONTINUE;
            }
            return "L" + from;
        }

        public Object convert(Object from, @NotNull Type Type, @NotNull Converter converter) {
            if (from == null || !from.getClass().equals(Long.class)) {
                return Next.CONTINUE;
            }
            return "L" + from;
        }

        public Object convert(Object from, @NotNull Type fromType, @NotNull Type toType, @NotNull Converter converter) {
            if (from == null || !from.getClass().equals(Long.class)) {
                return Next.CONTINUE;
            }
            return "L" + from;
        }
    }

    public static enum E {
        A, B, C
    }
}
