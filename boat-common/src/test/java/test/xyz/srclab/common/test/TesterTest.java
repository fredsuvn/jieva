package test.xyz.srclab.common.test;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.srclab.common.test.TestListener;
import xyz.srclab.common.test.TestTask;
import xyz.srclab.common.test.Tester;

import java.util.Random;
import java.util.concurrent.Executors;

/**
 * @author sunqian
 */
public class TesterTest {

    @Test
    public void testTester() {
        Tester.testTasks(
                Executors.newCachedThreadPool(),
                TestListener.DEFAULT,
                new TestTaskImpl("task1"),
                new TestTaskImpl("task2"),
                new TestTaskImpl("task3"),
                new TestTaskImpl("task4"),
                new TestTaskImpl("task5")
        );
    }
}

class TestTaskImpl implements TestTask {

    private static final Random random = new Random();

    private final String name;

    TestTaskImpl(String name) {
        this.name = name;
    }

    @NotNull
    @Override
    public String name() {
        return name;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(random.nextInt(5) * (random.nextInt(400) + 800));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}