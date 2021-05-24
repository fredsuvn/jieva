package sample.java.xyz.srclab.core.test;

import org.testng.annotations.Test;
import xyz.srclab.common.test.TestLogger;
import xyz.srclab.common.test.TestTask;
import xyz.srclab.common.test.Tests;

import java.util.Arrays;

public class TestSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testTests() {
        Tests.testTasks(Arrays.asList(
            TestTask.newTask(() -> {
                logger.log("Run test task!");
            })
        ));
    }
}
