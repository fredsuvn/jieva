package test.xyz.srclab.test.tester;

import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;
import xyz.srclab.test.tester.TestListener;
import xyz.srclab.test.tester.TestTask;
import xyz.srclab.test.tester.Tester;

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
            Thread.sleep(new Random().nextInt(5) * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}