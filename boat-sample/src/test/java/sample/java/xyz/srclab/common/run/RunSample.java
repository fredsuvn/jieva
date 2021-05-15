package sample.java.xyz.srclab.common.run;

import org.testng.annotations.Test;
import xyz.srclab.common.lang.Current;
import xyz.srclab.common.lang.IntRef;
import xyz.srclab.common.run.Runner;
import xyz.srclab.common.run.Running;
import xyz.srclab.common.run.Scheduler;
import xyz.srclab.common.run.Scheduling;
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
        Scheduler scheduler = Scheduler.DEFAULT_THREAD_SCHEDULER;
        IntRef intRef = IntRef.of(0);
        Scheduling<?> scheduling = scheduler.scheduleFixedDelay(Duration.ZERO, Duration.ofMillis(1000), () -> {
            intRef.set(intRef.get() + 100);
            return null;
        });
        Current.sleep(2500);
        scheduling.cancel(false);
        //300
        logger.log("int: {}", intRef.get());
    }
}
