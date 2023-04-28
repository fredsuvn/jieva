package test.java.xyz.srclab.common.bean;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.bean.BBean;
import xyz.srclab.common.reflect.BType;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class GenericBeanTest {

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
        a.setP5(Arrays.asList("7", "8", "9"));
        S1 s1 = new S1();
        s1.setS1("123");
        s1.setS2(234);
        a.setS(s1);

        B<Long> b = BBean.copyProperties(a, new B(), BType.parameterizedType(B.class, Long.class));
        Assert.assertEquals(b.getI1(), Arrays.asList(1, 2, 3));
        Assert.assertEquals(b.getI2(), new BigDecimal(666));
        Assert.assertEquals(b.getI3(), new List[]{Arrays.asList(1, 2, 3)});
        Assert.assertEquals(b.getP1(), new Long(9L));
        Assert.assertEquals(b.getP2(), Collections.singleton(1));
        Assert.assertEquals(b.getP3(), "pppppp");
        Assert.assertEquals(b.getP4(), "bbbbbb");
        Assert.assertEquals(b.getP5(), new long[]{7, 8, 9});
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

    public static abstract class C1<U> implements I1<List<U>> {
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

        private List<String> i1;
        private Integer i2;
        private List<? super Integer>[] i3;

        private String p1;
        private String[] p2;
        private char[] p3;
        private byte[] p4;

        private List<String> p5;

        private S1 s;

        @Override
        public List<String> getI1() {
            return i1;
        }

        @Override
        public void setI1(List<String> i1) {
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

        public List<String> getP5() {
            return p5;
        }

        public void setP5(List<String> p5) {
            this.p5 = p5;
        }

        public S1 getS() {
            return s;
        }

        public void setS(S1 s) {
            this.s = s;
        }
    }

    public static class B<T> extends C3 implements I2<BigDecimal>, I3<List<? super String>[]> {

        private List<Integer> i1;
        private BigDecimal i2;
        private List<? super String>[] i3;

        private T p1;
        private Set<Integer> p2;
        private String p3;
        private String p4;

        private long[] p5;

        private S2 s;

        @Override
        public List<Integer> getI1() {
            return i1;
        }

        @Override
        public void setI1(List<Integer> i1) {
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

        public long[] getP5() {
            return p5;
        }

        public void setP5(long[] p5) {
            this.p5 = p5;
        }

        public S2 getS() {
            return s;
        }

        public void setS(S2 s) {
            this.s = s;
        }
    }
}
