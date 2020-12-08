package test.java.xyz.srclab.common.bean;

import kotlin.jvm.functions.Function1;
import org.apache.commons.beanutils.BeanUtils;
import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.bean.BeanKit;
import xyz.srclab.common.bean.BeanResolver;
import xyz.srclab.common.test.TestTask;
import xyz.srclab.common.test.Tester;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sunqian
 */
public class BeanTest {

    @Test
    public void testSimpleBean() {
        SimpleBean a = new SimpleBean();
        a.setP1("123");
        a.setP2(6);
        a.setP3(Arrays.asList("1", "2", "3"));

        SimpleBean b = new SimpleBean();
        BeanKit.copyProperties(a, b);
        Assert.assertEquals(b.getP1(), a.getP1());
        Assert.assertEquals(b.getP2(), a.getP2());
        Assert.assertEquals(b.getP3(), a.getP3());

        a.setP1(null);
        BeanKit.copyProperties(a, b);
        Assert.assertEquals(b.getP1(), a.getP1());
        Assert.assertEquals(b.getP2(), a.getP2());
        Assert.assertEquals(b.getP3(), a.getP3());

        a.setP1(null);
        b.setP1("234");
        BeanKit.copyPropertiesIgnoreNull(a, b);
        Assert.assertEquals(b.getP1(), "234");
        Assert.assertEquals(b.getP2(), a.getP2());
        Assert.assertEquals(b.getP3(), a.getP3());

        a.setP1("111");
        b.setP1("567");
        BeanKit.copyProperties(a, b, new BeanResolver.CopyOptions() {
            @NotNull
            @Override
            public Function1<Object, Boolean> propertyNameFilter() {
                return name -> !name.equals("p1");
            }
        });
        Assert.assertEquals(b.getP1(), "567");
        Assert.assertEquals(b.getP2(), a.getP2());
        Assert.assertEquals(b.getP3(), a.getP3());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("p1", "123");
        map.put("p2", 6);
        map.put("p3", Arrays.asList("1", "2", "3"));
        map.put("class", SimpleBean.class);
        a.setP1("123");
        Map<String, Object> aMap = BeanKit.asMap(a);
        Assert.assertEquals(aMap, map);
        aMap.put("p1", "555");
        Assert.assertEquals(a.getP1(), "555");
        Assert.expectThrows(UnsupportedOperationException.class, () -> aMap.put("p4", "p4"));
    }

    /*
     * Task BeanKit was accomplished, cost: PT1M59.624S
     * Task Beanutils was accomplished, cost: PT13M2.245S
     * All tasks were accomplished, await cost: PT13M2.248S, total cost: PT15M1.869S, average cost: PT7M30.9345S
     */
    @Test(enabled = false)
    public void testPerformance() {
        PerformanceBean a = new PerformanceBean();
        a.setS1("s1");
        a.setS2("s2");
        a.setS3("s3");
        a.setS4("s4");
        a.setS5("s5");
        a.setS6("s6");
        a.setS7("s7");
        a.setS8("s8");
        a.setI1(1);
        a.setI2(2);
        a.setI3(3);
        a.setI4(4);
        a.setI5(5);
        a.setI6(6);
        a.setI7(7);
        a.setI8(8);
        long times = 50000000;
        Tester.testTasksParallel(
                TestTask.newTask("BeanKit", times, () -> {
                    PerformanceBean b = new PerformanceBean();
                    BeanKit.copyProperties(a, b);
                    return null;
                }),
                TestTask.newTask("Beanutils", times, () -> {
                    PerformanceBean b = new PerformanceBean();
                    try {
                        BeanUtils.copyProperties(b, a);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                    return null;
                })
        );
    }

    @Test
    public void testGeneric() {
        GenericBeanA a = new GenericBeanA();
        a.setA1("123");
        a.setA2(Arrays.asList("1", "2"));
        a.setI1(1);
        a.setI2(Arrays.asList(3, 4));
        GenericBeanB b = new GenericBeanB();
        BeanKit.copyProperties(a, b);
        Assert.assertEquals(b.getA1(), new Integer(123));
        Assert.assertEquals(b.getA2(), Arrays.asList(1, 2));
        Assert.assertEquals(b.getI1(), "1");
        Assert.assertEquals(b.getI2(), Arrays.asList("3", "4"));
    }

    public static class SimpleBean {

        private String p1;
        private int p2;
        private List<String> p3;

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

        public List<String> getP3() {
            return p3;
        }

        public void setP3(List<String> p3) {
            this.p3 = p3;
        }
    }

    public static class PerformanceBean {

        private String s1;
        private String s2;
        private String s3;
        private String s4;
        private String s5;
        private String s6;
        private String s7;
        private String s8;

        private int i1;
        private int i2;
        private int i3;
        private int i4;
        private int i5;
        private int i6;
        private int i7;
        private int i8;

        public String getS1() {
            return s1;
        }

        public void setS1(String s1) {
            this.s1 = s1;
        }

        public String getS2() {
            return s2;
        }

        public void setS2(String s2) {
            this.s2 = s2;
        }

        public String getS3() {
            return s3;
        }

        public void setS3(String s3) {
            this.s3 = s3;
        }

        public String getS4() {
            return s4;
        }

        public void setS4(String s4) {
            this.s4 = s4;
        }

        public String getS5() {
            return s5;
        }

        public void setS5(String s5) {
            this.s5 = s5;
        }

        public String getS6() {
            return s6;
        }

        public void setS6(String s6) {
            this.s6 = s6;
        }

        public String getS7() {
            return s7;
        }

        public void setS7(String s7) {
            this.s7 = s7;
        }

        public String getS8() {
            return s8;
        }

        public void setS8(String s8) {
            this.s8 = s8;
        }

        public int getI1() {
            return i1;
        }

        public void setI1(int i1) {
            this.i1 = i1;
        }

        public int getI2() {
            return i2;
        }

        public void setI2(int i2) {
            this.i2 = i2;
        }

        public int getI3() {
            return i3;
        }

        public void setI3(int i3) {
            this.i3 = i3;
        }

        public int getI4() {
            return i4;
        }

        public void setI4(int i4) {
            this.i4 = i4;
        }

        public int getI5() {
            return i5;
        }

        public void setI5(int i5) {
            this.i5 = i5;
        }

        public int getI6() {
            return i6;
        }

        public void setI6(int i6) {
            this.i6 = i6;
        }

        public int getI7() {
            return i7;
        }

        public void setI7(int i7) {
            this.i7 = i7;
        }

        public int getI8() {
            return i8;
        }

        public void setI8(int i8) {
            this.i8 = i8;
        }
    }

    public interface GenericInterface<I1, I2> {

        I1 getI1();

        void setI1(I1 i1);

        I2 getI2();

        void setI2(I2 i2);
    }

    public static class GenericClass<A1, A2> {

        private A1 a1;
        private A2 a2;

        public A1 getA1() {
            return a1;
        }

        public void setA1(A1 a1) {
            this.a1 = a1;
        }

        public A2 getA2() {
            return a2;
        }

        public void setA2(A2 a2) {
            this.a2 = a2;
        }
    }

    public static class GenericBeanA
            extends GenericClass<String, List<String>>
            implements GenericInterface<Integer, List<Integer>> {

        private Integer i1;
        private List<Integer> i2;

        @Override
        public Integer getI1() {
            return i1;
        }

        @Override
        public void setI1(Integer i1) {
            this.i1 = i1;
        }

        @Override
        public List<Integer> getI2() {
            return i2;
        }

        @Override
        public void setI2(List<Integer> i2) {
            this.i2 = i2;
        }
    }

    public static class GenericBeanB
            extends GenericClass<Integer, List<Integer>>
            implements GenericInterface<String, List<String>> {

        private String i1;
        private List<String> i2;

        @Override
        public String getI1() {
            return i1;
        }

        @Override
        public void setI1(String i1) {
            this.i1 = i1;
        }

        @Override
        public List<String> getI2() {
            return i2;
        }

        @Override
        public void setI2(List<String> i2) {
            this.i2 = i2;
        }
    }
}
