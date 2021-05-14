package test.java.xyz.srclab.common.bean;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.Anys;
import xyz.srclab.common.bean.*;
import xyz.srclab.common.reflect.Types;
import xyz.srclab.common.test.TestLogger;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author sunqian
 */
public class BeanTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testBeanResolve() {
        BeanResolver beanStyle = BeanResolver.newBeanResolver(BeanResolveHandler.DEFAULTS);
        BeanType beanType = beanStyle.resolve(SimpleBean.class);
        Assert.assertEquals(beanType.properties().size(), 4);
        Assert.assertEquals(beanType.getProperty("p1").type(), String.class);
        Assert.assertEquals(beanType.getProperty("p2").type(), int.class);
        Assert.assertEquals(beanType.getProperty("p3").type(), Types.parameterizedType(List.class, String.class));

        BeanResolver namingStyle = BeanResolver.newBeanResolver(Arrays.asList(NamingStyleBeanResolveHandler.INSTANCE));
        BeanType beanType2 = namingStyle.resolve(SimpleNamingBean.class);
        Assert.assertEquals(beanType2.properties().size(), 3);
        Assert.assertEquals(beanType2.getProperty("p1").type(), String.class);
        Assert.assertEquals(beanType2.getProperty("p2").type(), int.class);
        Assert.assertEquals(beanType2.getProperty("p3").type(), Types.parameterizedType(List.class, String.class));

        Assert.assertNotSame(beanType, beanType2);
    }

    @Test
    public void testSimpleBean() {
        SimpleBean a = new SimpleBean();
        a.setP1("123");
        a.setP2(6);
        a.setP3(Arrays.asList("1", "2", "3"));

        SimpleBean b = new SimpleBean();
        Beans.copyProperties(a, b);
        Assert.assertEquals(b.getP1(), a.getP1());
        Assert.assertEquals(b.getP2(), a.getP2());
        Assert.assertEquals(b.getP3(), a.getP3());

        a.setP1(null);
        Beans.copyProperties(a, b);
        Assert.assertEquals(b.getP1(), a.getP1());
        Assert.assertEquals(b.getP2(), a.getP2());
        Assert.assertEquals(b.getP3(), a.getP3());

        a.setP1(null);
        b.setP1("234");
        Beans.copyPropertiesIgnoreNull(a, b);
        Assert.assertEquals(b.getP1(), "234");
        Assert.assertEquals(b.getP2(), a.getP2());
        Assert.assertEquals(b.getP3(), a.getP3());

        a.setP1("111");
        b.setP1("567");
        Beans.copyProperties(a, b, BeanCopyOptions.DEFAULT.withNameFilter(name -> !name.equals("p1")));
        //Beans.copyProperties(a, b, new BeanResolver.CopyOptions() {
        //    @NotNull
        //    @Override
        //    public Function1<Object, Boolean> nameFilter() {
        //        return name -> !name.equals("p1");
        //    }
        //});
        Assert.assertEquals(b.getP1(), "567");
        Assert.assertEquals(b.getP2(), a.getP2());
        Assert.assertEquals(b.getP3(), a.getP3());

        a.setP1("123");
        a.setP2(222);
        a.setP3(Arrays.asList("1", "2", "3"));
        LinkedHashMap<String, Object> map = Beans.copyProperties(a, new LinkedHashMap<>());
        Assert.assertEquals(map.size(), 3);
        Assert.assertEquals(map.get("p1"), "123");
        Assert.assertEquals(map.get("p2"), 222);
        Assert.assertEquals(map.get("p3"), Arrays.asList("1", "2", "3"));

        map.put("p2", 999);
        Beans.copyProperties(map, a);
        Assert.assertEquals(a.getP2(), 999);

        LinkedHashMap<String, Object> map2 = Beans.copyProperties(map, new LinkedHashMap<>());
        Assert.assertEquals(map2.size(), 3);
        Assert.assertEquals(map2.get("p1"), "123");
        Assert.assertEquals(map2.get("p2"), 999);
        Assert.assertEquals(map2.get("p3"), Arrays.asList("1", "2", "3"));
    }

    @Test
    public void testBeanMap() {
        SimpleBean simpleBean = new SimpleBean();
        simpleBean.setP1("123");
        simpleBean.setP2(6);
        simpleBean.setP3(Arrays.asList("1", "2", "3"));
        Map<String, Object> simpleMap = Beans.asMap(simpleBean);
        Assert.assertEquals(simpleMap.get("p1"), "123");
        Assert.assertEquals(simpleMap.get("p2"), 6);
        Assert.assertEquals(simpleMap.get("p3"), Arrays.asList("1", "2", "3"));
        simpleMap.put("p1", "555");
        Assert.assertEquals(simpleBean.getP1(), "555");
        Assert.expectThrows(UnsupportedOperationException.class, () -> simpleMap.put("p4", "p4"));
        simpleBean.setP2(888);
        Assert.assertEquals(simpleMap.get("p2"), 888);

        BeanCopyOptions copyOptions = BeanCopyOptions.DEFAULT
            .withFromToTypes(SimpleBean.class, Types.parameterizedType(Map.class, String.class, int.class))
            .withNameFilter(n -> "p1".equals(n) || "p2".equals(n));
        //BeanResolver.CopyOptions copyOptions = BeanResolver.CopyOptions.DEFAULT
        //        .withTypes(SimpleBean.class, Types.parameterizedType(Map.class, String.class, int.class))
        //        .withNameFilter(n -> "p1".equals(n) || "p2".equals(n));
        Map<String, Integer> siMap = Anys.as(Beans.asMap(simpleBean, copyOptions));
        logger.log("siMap: {}", siMap);
        Assert.assertEquals(siMap.get("p1"), (Integer) 555);
        Assert.assertEquals(siMap.get("p2"), (Integer) 888);
        Assert.assertEquals(siMap.size(), 2);
    }

    @Test
    public void testGenericBean() {
        A a = new A();
        a.setI1(Arrays.asList("1", "2", "3"));
        a.setI2(666);
        a.setI3(new List[]{Arrays.asList(1, 2, 3)});
        a.setP1("9");
        a.setP2(new String[]{"1", "1", "1"});
        a.setP3("pppppp".toCharArray());
        a.setP4("bbbbbb".getBytes(Charset.defaultCharset()));
        S1 s1 = new S1();
        s1.setS1("123");
        s1.setS2(234);
        a.setS(s1);

        B<Long> b = Beans.copyProperties(a, new B(),
            BeanCopyOptions.DEFAULT.withToType(Types.parameterizedType(B.class, Long.class)));
        //B<Long> b = Beans.copyProperties(a, new B(),
        //        BeanResolver.CopyOptions.DEFAULT.withToType(Types.parameterizedType(B.class, Long.class)));
        Assert.assertEquals(b.getI1(), Arrays.asList(1, 2, 3));
        Assert.assertEquals(b.getI2(), new BigDecimal(666));
        Assert.assertEquals(b.getI3(), new List[]{Arrays.asList(1, 2, 3)});
        Assert.assertEquals(b.getP1(), new Long(9L));
        Assert.assertEquals(b.getP2(), Collections.singleton(1));
        Assert.assertEquals(b.getP3(), "pppppp");
        Assert.assertEquals(b.getP4(), "bbbbbb");
        Assert.assertEquals(b.getS().getS1(), 123);
        Assert.assertEquals(b.getS().getS2(), 234d);
    }

    public interface I1<T1> {

        T1 getI1();

        void setI1(T1 i1);
    }

    public interface I2<T2 extends Number> {

        T2 getI2();

        void setI2(T2 i2);
    }

    public interface I3<T3> {

        T3 getI3();

        void setI3(T3 i3);
    }

    public static abstract class C1<U> implements I1<List<? extends U>> {
    }

    public static abstract class C2 extends C1<String> {
    }

    public static abstract class C3 extends C1<Integer> {
    }

    public static class S1 {
        private String s1;
        private int s2;

        public String getS1() {
            return s1;
        }

        public void setS1(String s1) {
            this.s1 = s1;
        }

        public int getS2() {
            return s2;
        }

        public void setS2(int s2) {
            this.s2 = s2;
        }
    }

    public static class S2 {
        private int s1;
        private double s2;

        public int getS1() {
            return s1;
        }

        public void setS1(int s1) {
            this.s1 = s1;
        }

        public double getS2() {
            return s2;
        }

        public void setS2(double s2) {
            this.s2 = s2;
        }
    }

    public static class A extends C2 implements I2<Integer>, I3<List<? super Integer>[]> {

        private List<? extends String> i1;
        private Integer i2;
        private List<? super Integer>[] i3;

        private String p1;
        private String[] p2;
        private char[] p3;
        private byte[] p4;

        private S1 s;

        @Override
        public List<? extends String> getI1() {
            return i1;
        }

        @Override
        public void setI1(List<? extends String> i1) {
            this.i1 = i1;
        }

        @Override
        public Integer getI2() {
            return i2;
        }

        @Override
        public void setI2(Integer i2) {
            this.i2 = i2;
        }

        @Override
        public List<? super Integer>[] getI3() {
            return i3;
        }

        @Override
        public void setI3(List<? super Integer>[] i3) {
            this.i3 = i3;
        }

        public String getP1() {
            return p1;
        }

        public void setP1(String p1) {
            this.p1 = p1;
        }

        public String[] getP2() {
            return p2;
        }

        public void setP2(String[] p2) {
            this.p2 = p2;
        }

        public char[] getP3() {
            return p3;
        }

        public void setP3(char[] p3) {
            this.p3 = p3;
        }

        public byte[] getP4() {
            return p4;
        }

        public void setP4(byte[] p4) {
            this.p4 = p4;
        }

        public S1 getS() {
            return s;
        }

        public void setS(S1 s) {
            this.s = s;
        }
    }

    public static class B<T> extends C3 implements I2<BigDecimal>, I3<List<? super String>[]> {

        private List<? extends Integer> i1;
        private BigDecimal i2;
        private List<? super String>[] i3;

        private T p1;
        private Set<Integer> p2;
        private String p3;
        private String p4;

        private S2 s;

        @Override
        public List<? extends Integer> getI1() {
            return i1;
        }

        @Override
        public void setI1(List<? extends Integer> i1) {
            this.i1 = i1;
        }

        @Override
        public BigDecimal getI2() {
            return i2;
        }

        @Override
        public void setI2(BigDecimal i2) {
            this.i2 = i2;
        }

        @Override
        public List<? super String>[] getI3() {
            return i3;
        }

        @Override
        public void setI3(List<? super String>[] i3) {
            this.i3 = i3;
        }

        public T getP1() {
            return p1;
        }

        public void setP1(T p1) {
            this.p1 = p1;
        }

        public Set<Integer> getP2() {
            return p2;
        }

        public void setP2(Set<Integer> p2) {
            this.p2 = p2;
        }

        public String getP3() {
            return p3;
        }

        public void setP3(String p3) {
            this.p3 = p3;
        }

        public String getP4() {
            return p4;
        }

        public void setP4(String p4) {
            this.p4 = p4;
        }

        public S2 getS() {
            return s;
        }

        public void setS(S2 s) {
            this.s = s;
        }
    }
}
