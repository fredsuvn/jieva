package sample.xyz.srclab.common.run;

import org.testng.annotations.Test;
import xyz.srclab.common.base.Current;
import xyz.srclab.common.base.IntRef;
import xyz.srclab.common.run.Runner;
import xyz.srclab.common.run.Running;
import xyz.srclab.common.run.ScheduledRunner;
import xyz.srclab.common.run.ScheduledRunning;
import xyz.srclab.common.test.TestLogger;

import java.time.Duration;

public class RunSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testRunner() {
        Runner runner = Runner.SYNC_RUNNER;
        IntRef intRef = IntRef.of(0);
        Running<?> running = runner.run(() -> {
            intRef.set(666);
            return null;
        });
        running.get();
        //666
        logger.log("int: {}", intRef.get());
    }

    @Test
    public void testScheduledRunner() {
        ScheduledRunner runner = ScheduledRunner.SINGLE_THREAD_RUNNER;
        IntRef intRef = IntRef.of(0);
        ScheduledRunning<?> running = runner.scheduleWithFixedDelay(Duration.ZERO, Duration.ofMillis(1000), () -> {
            intRef.set(intRef.get() + 100);
            return null;
        });
        Current.sleep(2500);
        running.cancel(false);
        //300
        logger.log("int: {}", intRef.get());
    }
}
