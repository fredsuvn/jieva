package test.java.xyz.srclab.common.run;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.Counter;
import xyz.srclab.common.lang.Current;
import xyz.srclab.common.lang.IntRef;
import xyz.srclab.common.run.*;
import xyz.srclab.common.test.TestLogger;

import java.time.Duration;

/**
 * @author sunqian
 */
public class RunTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testRunSync() {
        Runner runner = Runner.SYNC_RUNNER;
        IntRef intRef = IntRef.of(0);
        Running<?> running = runner.run(() -> {
            Current.sleep(2000);
            intRef.set(666);
            return null;
        });
        Assert.assertTrue(running.isEnd());
        Assert.assertEquals(intRef.get(), 666);
    }

    @Test
    public void testRunAsync() {
        Runner runner = Runner.ASYNC_RUNNER;
        doTestAsync(runner);
    }

    @Test
    public void testThreadPoolRunner() {
        Runner runner = ThreadPoolRunner.newBuilder().build();
        doTestAsync(runner);
    }

    private void doTestAsync(Runner runner) {
        IntRef intRef = IntRef.of(0);
        Running<?> running = runner.run(() -> {
            Current.sleep(2000);
            intRef.set(666);
            return null;
        });
        Assert.assertEquals(intRef.get(), 0);
        Assert.assertFalse(running.isEnd());
        Current.sleep(2500);
        Assert.assertTrue(running.isEnd());
        Assert.assertEquals(intRef.get(), 666);
    }

    @Test
    public void testScheduledRunSync() {
        Scheduler scheduler = Scheduler.DEFAULT_THREAD_SCHEDULER;
        doTestScheduledRunSync(scheduler);
    }

    @Test
    public void testScheduledThreadPoolRunSync() {
        Scheduler scheduler = ScheduledThreadPoolScheduler.newBuilder().build();
        doTestScheduledRunSync(scheduler);
    }

    private void doTestScheduledRunSync(Scheduler scheduler) {
        Counter counter = Counter.startsAt(0);
        Scheduling<?> scheduling = scheduler.schedule(Duration.ofMillis(1000), () -> {
            Current.sleep(1000);
            logger.log(counter.incrementAndGetInt());
            return null;
        });
        Assert.assertFalse(scheduling.isEnd());
        Current.sleep(2500);
        Assert.assertTrue(scheduling.isEnd());
        Assert.assertEquals(counter.getInt(), 1);

        counter.reset();
        scheduling = scheduler.scheduleFixedRate(Duration.ZERO, Duration.ofMillis(1000), () -> {
            Current.sleep(1000);
            logger.log(counter.incrementAndGetInt());
            return null;
        });
        Assert.assertFalse(scheduling.isEnd());
        Current.sleep(2500);
        Assert.assertFalse(scheduling.isEnd());
        scheduling.cancel(false);
        Assert.assertTrue(scheduling.isEnd());

        counter.reset();
        scheduling = scheduler.scheduleFixedDelay(Duration.ZERO, Duration.ofMillis(1000), () -> {
            Current.sleep(1000);
            logger.log(counter.incrementAndGetInt());
            return null;
        });
        Assert.assertFalse(scheduling.isEnd());
        Current.sleep(3000);
        Assert.assertFalse(scheduling.isEnd());
        scheduling.cancel(false);
        Assert.assertTrue(scheduling.isEnd());
    }
}
