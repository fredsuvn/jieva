package test.java.xyz.srclab.common.test;

import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.run.Runner;
import xyz.srclab.common.test.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author sunqian
 */
public class TesterTest {

    private static final Random random = new Random();

    @Test
    public void testTester() {
        TestMarker testMarker = TestMarker.newTestMarker();
        Tester.testTasks(
                Runner.ASYNC_RUNNER,
                new TestListener() {

                    @Override
                    public void beforeRunAll(@NotNull List<? extends TestTask> testTasks) {
                        TestListener.DEFAULT.beforeRunAll(testTasks);
                    }

                    @Override
                    public void beforeRunEach(@NotNull TestTask testTask) {
                        TestListener.DEFAULT.beforeRunEach(testTask);
                    }

                    @Override
                    public void afterRunEach(@NotNull TestTask testTask, @NotNull TestTaskResult testTaskResult) {
                        TestListener.DEFAULT.afterRunEach(testTask, testTaskResult);
                        long time = testMarker.getMark(testTask.name());
                        Assert.assertTrue(testTaskResult.cost().toMillis() >= time);
                    }

                    @Override
                    public void afterRunAll(
                            @NotNull List<? extends TestTask> testTasks, @NotNull TestResult testResult) {
                        TestListener.DEFAULT.afterRunAll(testTasks, testResult);
                        Map<Object, Object> map = testMarker.asMap();
                        long total = map.values().stream().mapToLong(v -> Long.parseLong(v.toString())).sum();
                        long average = map.values().stream().mapToLong(v -> Long.parseLong(v.toString())).sum() / 5;
                        long max = map.values().stream().mapToLong(v -> Long.parseLong(v.toString())).max()
                                .orElse(0);
                        Assert.assertTrue(testResult.totalCost().toMillis() >= total);
                        Assert.assertTrue(testResult.averageCost().toMillis() >= average);
                        Assert.assertTrue(testResult.awaitCost().toMillis() >= max);
                    }
                },
                newTask("task1", testMarker),
                newTask("task2", testMarker),
                newTask("task3", testMarker),
                newTask("task4", testMarker),
                newTask("task5", testMarker)
        );
    }

    private TestTask newTask(String name, TestMarker testMarker) {
        return TestTask.newTask(name, () -> {
            try {
                long time = random.nextInt(5) * (random.nextInt(400) + 800);
                testMarker.mark(name, time);
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}