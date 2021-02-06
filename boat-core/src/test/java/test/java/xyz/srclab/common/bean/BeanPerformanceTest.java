package test.java.xyz.srclab.common.bean;

import org.apache.commons.beanutils.BeanUtils;
import org.testng.annotations.Test;
import xyz.srclab.common.bean.Beans;
import xyz.srclab.common.test.TestLogger;
import xyz.srclab.common.test.TestTask;
import xyz.srclab.common.test.Tester;

/**
 * @author sunqian
 */
public class BeanPerformanceTest {

    private static final TestLogger testLogger = TestLogger.DEFAULT;

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
                    Beans.copyProperties(a, b);
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
}
