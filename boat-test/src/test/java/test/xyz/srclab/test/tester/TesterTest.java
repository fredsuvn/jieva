package test.xyz.srclab.test.tester;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.srclab.test.tester.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author sunqian
 */
public class TesterTest {

    @Test
    public void testTester() {
        Tester.testTasks(Arrays.asList(
                new TestTask() {
                    @NotNull
                    @Override
                    public String name() {
                        return "1";
                    }

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new TestTask() {
                    @NotNull
                    @Override
                    public String name() {
                        return "2";
                    }

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new TestTask() {
                    @NotNull
                    @Override
                    public String name() {
                        return "3";
                    }

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                ),
                new TestListener() {

                    @Override
                    public void beforeRun(@NotNull List<? extends TestTask> testTasks) {

                    }

                    @Override
                    public void beforeEachRun(@NotNull TestTask testTask) {
                        System.out.println("Run " + testTask.name());
                    }

                    @Override
                    public void afterEachRun(@NotNull TestTask testTask, @NotNull TestTaskExpense testExpense) {
                        System.out.println(
                                "Run " + testTask.name() + " complete, cost: " + testExpense.cost().toMillis() + "ms.");
                    }

                    @Override
                    public void afterRun(
                            @NotNull List<? extends TestTask> testTasks, @NotNull TestTasksExpense testExpenses) {
                        System.out.println(
                                "Total cost " + testExpenses.cost().toMillis() + "ms, " +
                                        "average: " + testExpenses.average().toMillis() + "ms");
                    }
                });
    }
}
