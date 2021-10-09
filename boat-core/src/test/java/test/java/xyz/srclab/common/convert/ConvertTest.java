package test.java.xyz.srclab.common.convert;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.collect.Collects;
import xyz.srclab.common.convert.*;
import xyz.srclab.common.logging.Logs;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.util.*;

/**
 * @author sunqian
 */
public class ConvertTest {

    @Test
    public void testConverts() {
        E e = Converts.convert("b", E.class);
        Logs.info("e: {}", e);
        Assert.assertEquals(e, E.B);

        Map<Iterable<Long>, HashMap<Float, StringBuilder>> source = new LinkedHashMap<>();
        StringBuilder stringBuilder = new StringBuilder("BBB");
        source.put(Arrays.asList(10086L), Collects.putEntries(new HashMap<>(), 8.8, stringBuilder));
        Map<List<Double>, HashMap<Integer, CharSequence>> map = Converts.convert(
            source,
            new TypeRef<Map<Iterable<Long>, HashMap<Float, StringBuilder>>>() {
            },
            new TypeRef<Map<List<Double>, HashMap<Integer, CharSequence>>>() {
            });
        Logs.info("map: {}", map);
        Assert.assertEquals(
            map.get(Arrays.asList(10086.0)),
            Collects.putEntries(new HashMap<>(), 8, stringBuilder)
        );
    }

    @Test
    public void testCustomHandler() {
        Converter converter = Converter.newConverter(
            Arrays.asList(new IntToStringHandler(), new LongToStringHandler()));
        Logs.info("Convert int: {}", converter.convert(100, String.class));
        Assert.assertEquals(converter.convert(100, String.class), "I100");
        Logs.info("Convert long: {}", converter.convert(100L, String.class));
        Assert.assertEquals(converter.convert(100L, String.class), "L100");
    }

    @Test
    public void testNewConverter() {
        // Test possible cyclic dependence.
        Converter converter = Converter.newConverter(Collections.singletonList(CompatibleConvertHandler.INSTANCE));
    }

    public static class IntToStringHandler implements ConvertHandler {

        @Nullable
        @Override
        public Object convert(
            @Nullable Object from, @NotNull Type fromType, @NotNull Type toType, @NotNull ConvertContext context) {
            if (from == null || !from.getClass().equals(Integer.class)) {
                return null;
            }
            return "I" + from;
        }
    }

    public static class LongToStringHandler implements ConvertHandler {

        @Nullable
        @Override
        public Object convert(
            @Nullable Object from, @NotNull Type fromType, @NotNull Type toType, @NotNull ConvertContext context) {
            if (from == null || !from.getClass().equals(Long.class)) {
                return null;
            }
            return "L" + from;
        }
    }

    public static enum E {
        A, B, C
    }
}
