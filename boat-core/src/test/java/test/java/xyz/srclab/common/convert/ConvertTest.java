package test.java.xyz.srclab.common.convert;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.collect.BList;
import xyz.srclab.common.collect.BMap;
import xyz.srclab.common.collect.BSet;
import xyz.srclab.common.convert.*;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sunqian
 */
public class ConvertTest {

    @Test
    public void testConvert() {
        String str = "9999";
        BigInteger bigInteger = BConvert.convert(str, BigInteger.class);
        Assert.assertEquals(bigInteger, new BigInteger("9999"));
    }

    @Test
    public void testConvertGeneric() {
        List<? super Map<String, Long>> list = BList.newList(BMap.newMap("777", 888));
        Set<? super ConcurrentHashMap<Integer, Double>> set = BConvert.convert(
            list, new TypeRef<Set<? super ConcurrentHashMap<Integer, Double>>>() {
            });
        Assert.assertEquals(set, BSet.newSet(new ConcurrentHashMap(BMap.newMap(777, 888.0))));
    }

    @Test
    public void testConvertEnum() {
        E e = BConvert.convert("b", E.class);
        BLog.info("e: {}", e);
        Assert.assertEquals(e, E.B);
    }

    @Test
    public void testConvertMap() {
        Map<Iterable<Long>, HashMap<Float, StringBuilder>> source = new LinkedHashMap<>();
        StringBuilder stringBuilder = new StringBuilder("BBB");
        source.put(Arrays.asList(10086L), BMap.collect(new HashMap<>(), 8.8, stringBuilder));
        Map<List<Double>, HashMap<Integer, CharSequence>> map = BConvert.convert(
            source,
            new TypeRef<Map<Iterable<Long>, HashMap<Float, StringBuilder>>>() {
            }.getType(),
            new TypeRef<Map<List<Double>, HashMap<Integer, CharSequence>>>() {
            }.getType());
        BLog.info("map: {}", map);
        Assert.assertEquals(
            map.get(Arrays.asList(10086.0)),
            BMap.collect(new HashMap<>(), 8, stringBuilder)
        );
    }

    @Test
    public void testConvertList() {
        List<String> list = BList.newList("1", "2", "3");
        byte[] bytes = BConvert.convert(list, byte[].class);
        Assert.assertEquals(bytes, new byte[]{1, 2, 3});
    }

    @Test
    public void testBean() {
        A a = new A();
        a.setP1("1");
        a.setP2(2);
        B b = BConvert.convert(a, B.class);
        B bb = new B();
        bb.setP1(1);
        bb.setP2(2.0f);
        Assert.assertEquals(b, bb);
    }

    @Test
    public void testCustomHandler() {
        Converter converter = Converter.newConverter(
            Arrays.asList(new IntToStringHandler(), new LongToStringHandler()));
        BLog.info("Convert int: {}", converter.convert(100, String.class));
        Assert.assertEquals(converter.convert(100, String.class), "I100");
        BLog.info("Convert long: {}", converter.convert(100L, String.class));
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

    public enum E {
        A, B, C
    }

    public static class A {

        private String p1;
        private int p2;

        public String getP1() {
            return p1;
        }

        public void setP1(String p1) {
            this.p1 = p1;
        }

        public int getP2() {
            return p2;
        }

        public void setP2(int p2) {
            this.p2 = p2;
        }
    }

    public static class B {

        private double p1;
        private Float p2;

        public double getP1() {
            return p1;
        }

        public void setP1(double p1) {
            this.p1 = p1;
        }

        public Float getP2() {
            return p2;
        }

        public void setP2(Float p2) {
            this.p2 = p2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            B b = (B) o;
            return Double.compare(b.p1, p1) == 0 && Objects.equals(p2, b.p2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(p1, p2);
        }
    }
}
