package test.java.xyz.srclab.common.bean;

import org.apache.commons.beanutils.BeanUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import xyz.srclab.common.bean.Beans;

import java.util.concurrent.TimeUnit;

/**
 * @author sunqian
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 3)
@Measurement(iterations = 3, time = 3)
@Threads(16)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BeanBenchmark {

    private Bean initBean;

    @Setup
    public void init() {
        initBean = new Bean();
        initBean.setS1("s1");
        initBean.setS2("s2");
        initBean.setS3("s3");
        initBean.setS4("s4");
        initBean.setS5("s5");
        initBean.setS6("s6");
        initBean.setS7("s7");
        initBean.setS8("s8");
        initBean.setI1(1);
        initBean.setI2(2);
        initBean.setI3(3);
        initBean.setI4(4);
        initBean.setI5(5);
        initBean.setI6(6);
        initBean.setI7(7);
        initBean.setI8(8);
    }

    @Benchmark
    public void withBeans() {
        Beans.copyProperties(initBean, new Bean());
    }

    @Benchmark
    public void withBeanUtils() throws Exception {
        BeanUtils.copyProperties(new Bean(), initBean);
    }

    @Benchmark
    public void withSetDirectly() throws Exception {
        Bean bean = new Bean();
        bean.setS1(initBean.getS1());
        bean.setS2(initBean.getS2());
        bean.setS3(initBean.getS3());
        bean.setS4(initBean.getS4());
        bean.setS5(initBean.getS5());
        bean.setS6(initBean.getS6());
        bean.setS7(initBean.getS7());
        bean.setS8(initBean.getS8());
        bean.setI1(initBean.getI1());
        bean.setI2(initBean.getI2());
        bean.setI3(initBean.getI3());
        bean.setI4(initBean.getI4());
        bean.setI5(initBean.getI5());
        bean.setI6(initBean.getI6());
        bean.setI7(initBean.getI7());
        bean.setI8(initBean.getI8());
    }

    /*
     * Benchmark                       Mode  Cnt       Score       Error   Units
     * BeanBenchmark.withBeanUtils    thrpt    3     366.501 ±   266.764  ops/ms
     * BeanBenchmark.withBeans        thrpt    3    7508.884 ±  5385.327  ops/ms
     * BeanBenchmark.withSetDirectly  thrpt    3  152606.863 ± 32203.076  ops/ms
     *
     * 2021-7-11/12 Convert use ConvertChain:
     * Benchmark                       Mode  Cnt       Score       Error   Units
     * BeanBenchmark.withBeanUtils    thrpt    3     381.610 ±     0.972  ops/ms
     * BeanBenchmark.withBeans        thrpt    3    5070.789 ±    16.408  ops/ms
     * BeanBenchmark.withSetDirectly  thrpt    3  153512.701 ± 14054.397  ops/ms
     */
    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder().include(BeanBenchmark.class.getSimpleName()).build();
        new Runner(options).run();
    }

    public static class Bean {

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
}
