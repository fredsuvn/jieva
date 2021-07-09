package sample.java.xyz.srclab.core.run;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.Current;
import xyz.srclab.common.lang.IntRef;
import xyz.srclab.common.run.*;
import xyz.srclab.common.test.TestLogger;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

public class RunSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testRunner() {
        Runner runner = Runner.SYNC_RUNNER;
        IntRef intRef = IntRef.with(0);
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
        IntRef intRef = IntRef.with(0);
        Scheduling<?> scheduling = scheduler.scheduleFixedDelay(Duration.ZERO, Duration.ofMillis(1000), () -> {
            intRef.set(intRef.get() + 100);
            return null;
        });
        Current.sleep(2500);
        scheduling.cancel(false);
        //300
        logger.log("int: {}", intRef.get());
    }

    @Test
    public void testRunContext() throws Exception {
        RunContext runContext = RunContext.current();
        runContext.set("1", "666");
        Assert.assertEquals("666", runContext.get("1"));
        RunContext.Attach attach = runContext.attach();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Runner.ASYNC_RUNNER.run(() -> {
            RunContext detach = RunContext.current();
            detach.detach(attach);
            Assert.assertEquals("666", detach.get("1"));
            countDownLatch.countDown();
        });
        countDownLatch.await();
    }
}
