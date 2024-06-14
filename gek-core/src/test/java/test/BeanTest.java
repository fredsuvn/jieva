package test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Gek;
import xyz.fslabo.common.base.Flag;
import xyz.fslabo.common.base.GekLog;
import xyz.fslabo.common.bean.handlers.JavaBeanResolverHandler;
import xyz.fslabo.common.bean.handlers.NonGetterPrefixResolverHandler;
import xyz.fslabo.common.mapper.JieMapper;
import xyz.fslabo.common.bean.GekBeanInfo;
import xyz.fslabo.common.bean.GekBeanResolver;
import xyz.fslabo.common.bean.GekPropertyInfo;
import xyz.fslabo.common.bean.GekPropertyBase;
import xyz.fslabo.common.reflect.TypeRef;

import java.lang.annotation.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class BeanTest {

    @Test
    public void testTypeBean() throws Exception {
        Type ccType = new TypeRef<Cc<Double>>() {
        }.getType();
        GekBeanInfo ccBean = GekBeanInfo.get(ccType);
        GekLog.getInstance().info("ccBean: ", ccBean);
        GekPropertyInfo cc = ccBean.getProperty("cc");
        GekPropertyInfo c1 = ccBean.getProperty("c1");
        GekPropertyInfo c2 = ccBean.getProperty("c2");
        GekPropertyInfo i1 = ccBean.getProperty("i1");
        GekPropertyInfo i2 = ccBean.getProperty("i2");
        Assert.assertEquals(cc.getType(), Double.class);
        Assert.assertEquals(c2.getType(), Long.class);
        Assert.assertEquals(i1.getType(), String.class);
        Assert.assertEquals(i2.getType(), Integer.class);
        Assert.assertNull(c1);
        Assert.assertEquals(
            c2.getFieldAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
            Arrays.asList(C2.class.getDeclaredField("c2").getAnnotations()[0].toString())
        );
        Assert.assertEquals(
            c2.getGetterAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
            Arrays.asList(C2.class.getMethod("getC2").getAnnotations()[0].toString())
        );
        Assert.assertEquals(
            c2.getSetterAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
            Arrays.asList(C2.class.getMethod("setC2", Object.class).getAnnotations()[0].toString())
        );
        Assert.assertEquals(
            c2.getAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
            Arrays.asList(
                C2.class.getMethod("getC2").getAnnotations()[0].toString(),
                C2.class.getMethod("setC2", Object.class).getAnnotations()[0].toString(),
                C2.class.getDeclaredField("c2").getAnnotations()[0].toString()
            ));
    }

    @Test
    public void testClassBean() throws Exception {
        Type ccType = Cc.class;
        GekBeanInfo ccBean = GekBeanInfo.get(ccType);
        GekLog.getInstance().info("ccBean: ", ccBean);
        GekPropertyInfo cc = ccBean.getProperty("cc");
        GekPropertyInfo c1 = ccBean.getProperty("c1");
        GekPropertyInfo c2 = ccBean.getProperty("c2");
        GekPropertyInfo i1 = ccBean.getProperty("i1");
        GekPropertyInfo i2 = ccBean.getProperty("i2");
        GekPropertyInfo e1 = ccBean.getProperty("e1");
        GekPropertyInfo e2 = ccBean.getProperty("e2");
        Assert.assertEquals(cc.getType().toString(), "T");
        Assert.assertEquals(c2.getType(), Long.class);
        Assert.assertEquals(i1.getType(), String.class);
        Assert.assertEquals(i2.getType(), Integer.class);
        Assert.assertEquals(e1.getType(), E1.class);
        Assert.assertEquals(e2.getType(), E2.class);
        Assert.assertNull(c1);
        Assert.assertEquals(
            c2.getFieldAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
            Arrays.asList(C2.class.getDeclaredField("c2").getAnnotations()[0].toString())
        );
        Assert.assertEquals(
            c2.getGetterAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
            Arrays.asList(C2.class.getMethod("getC2").getAnnotations()[0].toString())
        );
        Assert.assertEquals(
            c2.getSetterAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
            Arrays.asList(C2.class.getMethod("setC2", Object.class).getAnnotations()[0].toString())
        );
        Assert.assertEquals(
            c2.getAnnotations().stream().map(Annotation::toString).collect(Collectors.toList()),
            Arrays.asList(
                C2.class.getMethod("getC2").getAnnotations()[0].toString(),
                C2.class.getMethod("setC2", Object.class).getAnnotations()[0].toString(),
                C2.class.getDeclaredField("c2").getAnnotations()[0].toString()
            ));
    }

    @Test
    public void testMapBean() {
        Type mapType = new TypeRef<Map<String, Long>>() {
        }.getType();
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("1", 10086L);
        map.put("2", 10010L);
        map.put("3", 10000L);
        GekBeanInfo mapBean = GekBeanInfo.wrap(map, mapType);
        GekLog.getInstance().info("mapBean: ", mapBean);
        GekPropertyInfo p1 = mapBean.getProperty("1");
        GekPropertyInfo p2 = mapBean.getProperty("2");
        GekPropertyInfo p3 = mapBean.getProperty("3");
        GekPropertyInfo p4 = mapBean.getProperty("4");
        Assert.assertEquals(p1.getType(), Long.class);
        Assert.assertEquals(p2.getType(), Long.class);
        Assert.assertEquals(p3.getType(), Long.class);
        Assert.assertNull(p4);
        Map<String, GekPropertyInfo> properties = mapBean.getProperties();
        Assert.assertSame(properties, mapBean.getProperties());
        map.put("4", 12345L);
        Assert.assertEquals(properties, mapBean.getProperties());
        Assert.assertNull(p4);
        GekPropertyInfo p42 = mapBean.getProperty("4");
        Assert.assertEquals(p42.getType(), Long.class);
        Assert.assertEquals(p1, mapBean.getProperties().get("1"));
        Assert.assertEquals(p1, mapBean.getProperty("1"));
        map.remove("2");
        Assert.assertNull(mapBean.getProperty("2"));
        GekLog.getInstance().info("mapBean: ", mapBean);

        GekBeanInfo mapObjBean = GekBeanInfo.wrap(map);
        GekPropertyInfo p1Obj = mapObjBean.getProperty("1");
        Assert.assertEquals(p1Obj.getType(), Object.class);
        Assert.assertEquals(
            p1.getValue(map),
            p1Obj.getValue(map)
        );

        Assert.assertThrows(IllegalArgumentException.class, () -> {
            GekBeanInfo.wrap(map, new TypeRef<Map<Object, Long>>() {
            }.getType());
        });
    }

    @Test
    public void testBeanResolver() {
        Type ccType = new TypeRef<Cc<Double>>() {
        }.getType();
        GekBeanInfo ccBean1 = GekBeanInfo.get(ccType);
        GekBeanInfo ccBean2 = GekBeanInfo.get(ccType);
        Assert.assertSame(ccBean1, ccBean2);
        GekBeanResolver resolver = GekBeanResolver.withHandlers(
            Collections.singletonList(new JavaBeanResolverHandler()),
            null
        );
        GekBeanInfo ccBean3 = resolver.resolve(ccType);
        Assert.assertNotSame(ccBean1, ccBean3);
        Assert.assertEquals(ccBean1, ccBean3);
        GekBeanInfo ccBean4 = resolver.resolve(ccType);
        Assert.assertNotSame(ccBean4, ccBean3);
        Assert.assertEquals(ccBean4, ccBean3);
    }

    @Test
    public void testBeanResolveHandler() {
        GekBeanInfo aaa = GekBeanInfo.get(TestHandler.class);
        Assert.assertEquals(aaa.getProperties().size(), 3);
        Assert.assertNotNull(aaa.getProperty("aaa"));
        Assert.assertNotNull(aaa.getProperty("bbb"));
        Assert.assertTrue(aaa.getProperty("aaa").isReadable());
        Assert.assertTrue(aaa.getProperty("aaa").isWriteable());
        Assert.assertFalse(aaa.getProperty("bbb").isReadable());
        Assert.assertTrue(aaa.getProperty("bbb").isWriteable());
        GekBeanInfo bbb = GekBeanResolver.withHandlers(NonGetterPrefixResolverHandler.INSTANCE).resolve(TestHandler.class);
        Assert.assertEquals(bbb.getProperties().size(), 3);
        Assert.assertNotNull(bbb.getProperty("aaa"));
        Assert.assertNotNull(bbb.getProperty("bbb"));
        Assert.assertNotNull(bbb.getProperty("getAaa"));
        Assert.assertFalse(bbb.getProperty("aaa").isReadable());
        Assert.assertTrue(bbb.getProperty("aaa").isWriteable());
        Assert.assertTrue(bbb.getProperty("bbb").isReadable());
        Assert.assertTrue(bbb.getProperty("bbb").isWriteable());
        Assert.assertTrue(bbb.getProperty("getAaa").isReadable());
        Assert.assertFalse(bbb.getProperty("getAaa").isWriteable());
    }

    @Test
    public void testCopyProperties() {
        //bean -> bean
        Cc<Long> cc1 = new Cc<>();
        cc1.setI1("i1");
        cc1.setI2(2);
        cc1.setC2(22L);
        cc1.setCc(33L);
        cc1.setE1(E1.E2);
        cc1.setE2(E2.E3);
        Cc<Long> cc2 = Gek.copyProperties(cc1, new Cc<>());
        Assert.assertEquals(cc2, cc1);
        cc1.setI1(null);
        cc2.setI1("888");
        Gek.copyProperties(cc1, cc2);
        Assert.assertEquals(cc2, cc1);
        Assert.assertEquals(cc2.getI1(), cc1.getI1());
        Assert.assertNull(cc2.getI1());
        Assert.assertSame(cc2.getE1(), E1.E2);
        Assert.assertSame(cc2.getE2(), E2.E3);
        cc2.setI1("888");
        Gek.copyProperties(cc1, cc2, false);
        Assert.assertEquals("888", cc2.getI1());
        cc1.setI1("aaaa");
        cc2 = Gek.copyProperties(cc1, new Cc<>());
        Assert.assertNotNull(cc2.getC2());
        cc2 = Gek.copyProperties(cc1, new Cc<>(), "c2");
        Assert.assertEquals(cc1.getI1(), cc2.getI1());
        Assert.assertEquals(cc1.getI2(), cc2.getI2());
        Assert.assertEquals(cc1.getCc(), cc2.getCc());
        Assert.assertNull(cc2.getC2());
        Assert.assertEquals(cc1.getC2().longValue(), 22);
        cc1.setI1(null);
        cc2 = Gek.copyProperties(cc1, new Cc<>(), false, "c2");
        cc2.setI1("qqqq");
        Gek.copyProperties(cc1, new Cc<>(), false, "c2");
        Assert.assertEquals("qqqq", cc2.getI1());
        Assert.assertEquals(cc1.getI2(), cc2.getI2());
        Assert.assertEquals(cc1.getCc(), cc2.getCc());
        Assert.assertNull(cc2.getC2());
        Assert.assertEquals(cc1.getC2().longValue(), 22);
        Assert.expectThrows(ClassCastException.class, () -> {
            Cc<Long> ccl = new Cc<>();
            Gek.copyProperties(cc1, new TypeRef<Cc<Double>>() {
                }.getType(),
                ccl, new TypeRef<Cc<String>>() {
                }.getType());
            Long l = ccl.getCc();
            System.out.println(l);
        });
        Cc<String> ccs = Gek.copyProperties(cc1, new TypeRef<Cc<Double>>() {
            }.getType(),
            new Cc<>(), new TypeRef<Cc<String>>() {
            }.getType());
        Assert.assertEquals(ccs.getCc(), cc1.getCc().toString());

        JieMapper kConverter = JieMapper.defaultMapper().insertFirstMiddleHandler(new JieMapper.Handler() {
            @Override
            public @Nullable Object map(
                @Nullable Object source, Type sourceType, Type targetType, JieMapper mapper) {
                if (Objects.equals(targetType, Kk.class)) {
                    return new Kk(String.valueOf(source));
                }
                return null;
            }
        });
        GekBeanCopier copier = GekBeanCopier.defaultCopier();

        //bean -> map
        Map<Kk, String> map1 = copier.toBuilder()
            .converter(kConverter)
            .build().copyProperties(
                cc1,
                new TypeRef<Cc<Long>>() {
                }.getType(),
                new HashMap<>(),
                new TypeRef<Map<Kk, String>>() {
                }.getType()
            );
        Assert.assertEquals(map1.get(new Kk("i1")), Gek.orNull(cc1.getI1(), String::valueOf));
        Assert.assertEquals(map1.get(new Kk("i2")), Gek.orNull(cc1.getI2(), String::valueOf));
        Assert.assertEquals(map1.get(new Kk("cc")), Gek.orNull(cc1.getCc(), String::valueOf));
        Assert.assertEquals(map1.get(new Kk("c2")), Gek.orNull(cc1.getC2(), String::valueOf));

        // map -> bean
        map1.put(new Kk("i1"), "88888");
        Cc<String> cs2 = copier.toBuilder()
            .converter(kConverter)
            .build().copyProperties(
                map1,
                new TypeRef<Map<Kk, String>>() {
                }.getType(),
                new Cc<>(),
                new TypeRef<Cc<String>>() {
                }.getType()
            );
        Assert.assertEquals(map1.get(new Kk("i1")), Gek.orNull(cs2.getI1(), String::valueOf));
        Assert.assertEquals(map1.get(new Kk("i2")), Gek.orNull(cs2.getI2(), String::valueOf));
        Assert.assertEquals(map1.get(new Kk("cc")), Gek.orNull(cs2.getCc(), String::valueOf));
        Assert.assertEquals(map1.get(new Kk("c2")), Gek.orNull(cs2.getC2(), String::valueOf));

        // map -> map
        Map<String, Kk> map2 = copier.toBuilder()
            .converter(kConverter)
            .build().copyProperties(
                map1,
                new TypeRef<Map<Kk, String>>() {
                }.getType(),
                new HashMap<>(),
                new TypeRef<Map<String, Kk>>() {
                }.getType()
            );
        Assert.assertEquals(map1.get(new Kk("i1")), Gek.orNull(map2.get("i1"), String::valueOf));
        Assert.assertEquals(map1.get(new Kk("i2")), Gek.orNull(map2.get("i2"), String::valueOf));
        Assert.assertEquals(map1.get(new Kk("cc")), Gek.orNull(map2.get("cc"), String::valueOf));
        Assert.assertEquals(map1.get(new Kk("c2")), Gek.orNull(map2.get("c2"), String::valueOf));
    }

    @Test
    public void testCopyPropertiesComplex() {
        Map<? extends Number, ? extends String> pm = new HashMap<>();
        pm.put(Gek.as(1), Gek.as("11"));
        CopyP1 p1 = new CopyP1("22", new List[]{Arrays.asList(pm)}, E1.E1);
        Map<String, ? extends CopyP1> cm = new HashMap<>();
        cm.put("33", Gek.as(p1));
        CopyA ca = new CopyA("44", new List[]{Arrays.asList(55)}, cm, p1);
        CopyB cb = Gek.copyProperties(ca, new CopyB());
        Assert.assertEquals(cb.getS(), new Long(44L));
        Assert.assertEquals(cb.getList().get(0), new int[]{55});
        Assert.assertSame(cb.getP().getE(), E2.E1);
        Map<? super BigDecimal, ? super BigInteger> bm = new LinkedHashMap<>();
        bm.put(new BigDecimal("1"), new BigInteger("11"));
        CopyP2 p2 = new CopyP2(22L, new List[]{Arrays.asList(bm)}, E2.E1);
        Assert.assertEquals(cb.getP(), p2);
        Map<Integer, ? super CopyP2> cm2 = new LinkedHashMap<>();
        cm2.put(33, p2);
        Assert.assertEquals(cb.getMap(), cm2);
    }

    @Test
    public void testResolverAsHandler() {
        int[] x = {0};
        GekBeanResolver.Handler handler = GekBeanResolver.defaultResolver()
            .withFirstHandler(new GekBeanResolver.Handler() {
                @Override
                public @Nullable Flag resolve(GekBeanResolver.Context context) {
                    if (Objects.equals(context.getType(), Integer.class)) {
                        x[0]++;
                        return null;
                    }
                    return Flag.BREAK;
                }
            })
            .asHandler();
        GekBeanResolver.Context context1 = new GekBeanResolver.Context() {

            @Override
            public Type getType() {
                return Integer.class;
            }

            @Override
            public Map<String, GekPropertyBase> getProperties() {
                return new HashMap<>();
            }
        };
        handler.resolve(context1);
        Assert.assertEquals(x[0], 1);
        GekBeanResolver.Context context2 = new GekBeanResolver.Context() {

            @Override
            public Type getType() {
                return String.class;
            }

            @Override
            public Map<String, GekPropertyBase> getProperties() {
                return new HashMap<>();
            }
        };
        handler.resolve(context2);
        Assert.assertEquals(x[0], 1);
    }

    public enum E1 {
        E1, E2, E3,
        ;
    }

    public enum E2 {
        E1, E2, E3,
        ;
    }

    public interface I2<T> {

        T getI2();

        void setI2(T t);
    }

    public interface I1 {

        String getI1();

        void setI1(String i1);
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public @interface Ann {
        String value();
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Cc<T> extends C2<Long> implements I1, I2<Integer> {

        private String i1;
        private T cc;
        private Integer i2;
        private E1 e1;
        private E2 e2;

        @Override
        public String getI1() {
            return i1;
        }

        @Override
        public void setI1(String i1) {
            this.i1 = i1;
        }
    }

    @EqualsAndHashCode
    public static class C2<T> {

        @Ann("c2")
        private T c2;

        @Ann("getC2")
        public T getC2() {
            return c2;
        }

        @Ann("setC2")
        public void setC2(T c2) {
            this.c2 = c2;
        }
    }

    @Data
    public static class C1 {
        private String c1;
    }

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Kk {

        private String value;

        @Override
        public String toString() {
            return value;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CopyP1 {
        private String p;
        private List<Map<? extends Number, ? extends String>>[] list;
        private E1 e;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class CopyP2 {
        private Long p;
        private List<Map<? super BigDecimal, ? super BigInteger>>[] list;
        private E2 e;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CopyA {
        private String s;
        private List<? extends Number>[] list;
        private Map<String, ? extends CopyP1> map;
        private CopyP1 p;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CopyB {
        private Long s;
        private List<int[]> list;
        private Map<Integer, ? super CopyP2> map;
        private CopyP2 p;
    }

    public static class TestHandler {

        public String getAaa() {
            return null;
        }

        public String setAaa(String aaa) {
            return null;
        }

        public String bbb() {
            return null;
        }

        public String setBbb(String bbb) {
            return null;
        }
    }
}
