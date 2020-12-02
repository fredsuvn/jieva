package test.java.xyz.srclab.common.test;

import org.testng.annotations.Test;
import xyz.srclab.common.run.Runner;
import xyz.srclab.common.test.TestListener;
import xyz.srclab.common.test.TestTask;
import xyz.srclab.common.test.Tester;

import java.util.Random;

/**
 * @author sunqian
 */
public class TesterTest {

    private static final Random random = new Random();

    @Test
    public void testTester() {
        Tester.testTasks(
                Runner.ASYNC_RUNNER,
                TestListener.DEFAULT,
                newTask("task1"),
                newTask("task2"),
                newTask("task3"),
                newTask("task4"),
                newTask("task5")
        );
    }

    private TestTask newTask(String name) {
        return TestTask.newTask(name, () -> {
            try {
                Thread.sleep(random.nextInt(5) * (random.nextInt(400) + 800));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    private class A implements TestTask {

        @Override
        public void run() {

        }
    }
}