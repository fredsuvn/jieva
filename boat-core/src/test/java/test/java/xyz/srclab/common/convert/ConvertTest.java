package test.java.xyz.srclab.common.convert;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.collect.Collects;
import xyz.srclab.common.convert.ConvertChain;
import xyz.srclab.common.convert.ConvertHandler;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.convert.Converts;
import xyz.srclab.common.reflect.TypeRef;
import xyz.srclab.common.test.TestLogger;

import java.lang.reflect.Type;
import java.util.*;

/**
 * @author sunqian
 */
public class ConvertTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testConverts() {
        E e = Converts.convert("b", E.class);
        logger.log("e: {}", e);
        Assert.assertEquals(e, E.B);

        Map<Iterable<Long>, HashMap<Float, StringBuilder>> source = new LinkedHashMap<>();
        StringBuilder stringBuilder = new StringBuilder("BBB");
        source.put(Arrays.asList(10086L), Collects.newMap(new HashMap<>(), 8.8, stringBuilder));
        Map<List<Double>, HashMap<Integer, CharSequence>> map = Converts.convert(
            source,
            new TypeRef<Map<Iterable<Long>, HashMap<Float, StringBuilder>>>() {
            },
            new TypeRef<Map<List<Double>, HashMap<Integer, CharSequence>>>() {
            });
        logger.log("map: {}", map);
        Assert.assertEquals(
            map.get(Arrays.asList(10086.0)),
            Collects.newMap(new HashMap<>(), 8, stringBuilder)
        );
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

        @Nullable
        @Override
        public Object convert(
            @Nullable Object from, @NotNull Type fromType, @NotNull Type toType, @NotNull ConvertChain chain) {
            if (from == null || !from.getClass().equals(Integer.class)) {
                return chain.next(from, fromType, toType);
            }
            return "I" + from;
        }
    }

    public static class LongToStringHandler implements ConvertHandler {

        @Nullable
        @Override
        public Object convert(
            @Nullable Object from, @NotNull Type fromType, @NotNull Type toType, @NotNull ConvertChain chain) {
            if (from == null || !from.getClass().equals(Long.class)) {
                return chain.next(from, fromType, toType);
            }
            return "L" + from;
        }
    }

    public static enum E {
        A, B, C
    }
}
