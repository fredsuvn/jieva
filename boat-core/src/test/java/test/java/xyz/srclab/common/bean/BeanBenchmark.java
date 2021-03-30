package test.java.xyz.srclab.common.bean;

import org.apache.commons.beanutils.BeanUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import xyz.srclab.common.bean.Beans;
import xyz.srclab.common.test.TestLogger;

import java.util.concurrent.TimeUnit;

/**
 * @author sunqian
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3, time = 60)
@Measurement(iterations = 3, time = 60)
@Threads(16)
@Fork(1)
@State(value = Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BeanBenchmark {

    private static final TestLogger logger = TestLogger.DEFAULT;

    private PerformanceBean initBean;

    @Setup
    public void init() {
        initBean = new PerformanceBean();
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
    public void testBeans() {
        Beans.copyProperties(initBean, new PerformanceBean());
    }

    @Benchmark
    public void testBeanUtils() throws Exception {
        BeanUtils.copyProperties(new PerformanceBean(), initBean);
    }

    /*
     * Benchmark                           Mode  Cnt     Score     Error   Units
     * BeanPerformanceTest.testBeanUtils  thrpt    3   360.217 ±  53.893  ops/ms
     * BeanPerformanceTest.testBeans      thrpt    3  6751.077 ± 157.653  ops/ms
     */
    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder().include(BeanBenchmark.class.getSimpleName()).build();
        new Runner(options).run();
    }

    /*
     * Task BeanKit was accomplished, cost: PT1M59.624S
     * Task Beanutils was accomplished, cost: PT13M2.245S
     * All tasks were accomplished, await cost: PT13M2.248S, total cost: PT15M1.869S, average cost: PT7M30.9345S
     *
     * At 20201211165234499 prepare to run all tasks...
     * Task BeanKit was accomplished, cost: PT56.024S
     * Task Beanutils was accomplished, cost: PT12M13.491S
     *
     * At 20201216155802946 prepare to run all tasks...
     * Task BeanKit was accomplished, cost: PT54.811S
     * Task Beanutils was accomplished, cost: PT13M49.168S
     *
     * x.s.c.t.TestListener$Companion$withTestLogger$1(172 ): At 20210201151349655 prepare to run all tasks...
     * x.s.c.t.TestListener$Companion$withTestLogger$1(180 ): Task BeanKit was accomplished, cost: PT48.247S
     * x.s.c.t.TestListener$Companion$withTestLogger$1(180 ): Task Beanutils was accomplished, cost: PT11M19.115S
     */
    //@Test(enabled = false)
    //public void testPerformance() {
    //    PerformanceBean a = new PerformanceBean();
    //    a.setS1("s1");
    //    a.setS2("s2");
    //    a.setS3("s3");
    //    a.setS4("s4");
    //    a.setS5("s5");
    //    a.setS6("s6");
    //    a.setS7("s7");
    //    a.setS8("s8");
    //    a.setI1(1);
    //    a.setI2(2);
    //    a.setI3(3);
    //    a.setI4(4);
    //    a.setI5(5);
    //    a.setI6(6);
    //    a.setI7(7);
    //    a.setI8(8);
    //    long times = 50000000;
    //    Tests.testTasksParallel(
    //            TestTask.newTask("BeanKit", times, () -> {
    //                PerformanceBean b = new PerformanceBean();
    //                Beans.copyProperties(a, b);
    //                return null;
    //            }),
    //            TestTask.newTask("Beanutils", times, () -> {
    //                PerformanceBean b = new PerformanceBean();
    //                try {
    //                    BeanUtils.copyProperties(b, a);
    //                } catch (Exception e) {
    //                    throw new IllegalStateException(e);
    //                }
    //                return null;
    //            })
    //    );
    //}
}
