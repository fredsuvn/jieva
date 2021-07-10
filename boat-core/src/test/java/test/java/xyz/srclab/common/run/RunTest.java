package test.java.xyz.srclab.common.run;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.Current;
import xyz.srclab.common.lang.IntRef;
import xyz.srclab.common.run.*;
import xyz.srclab.common.test.TestLogger;
import xyz.srclab.common.utils.Counter;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

/**
 * @author sunqian
 */
public class RunTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testRunSync() {
        Runner runner = Runner.SYNC_RUNNER;
        IntRef intRef = IntRef.with(0);
        Running<?> running = runner.run(() -> {
            Current.sleep(2000);
            intRef.set(666);
        });
        Assert.assertTrue(running.isEnd());
        Assert.assertEquals(intRef.get(), 666);

        runner.fastRun(() -> intRef.set(888));
        Assert.assertEquals(intRef.get(), 888);
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
        IntRef intRef = IntRef.with(0);
        CountDownLatch currentLatch = new CountDownLatch(1);
        Running<?> running = runner.run(() -> {
            try {
                currentLatch.await();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            intRef.set(666);
        });
        Assert.assertEquals(intRef.get(), 0);
        Assert.assertFalse(running.isEnd());
        currentLatch.countDown();
        running.get();
        Assert.assertTrue(running.isEnd());
        Assert.assertEquals(intRef.get(), 666);

        runner.fastRun(() -> {
            intRef.set(888);
            Assert.assertEquals(intRef.get(), 888);
        });
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
        CountDownLatch currentLatch = new CountDownLatch(1);
        Scheduling<?> scheduling = scheduler.schedule(Duration.ofMillis(1000), () -> {
            Current.sleep(1000);
            logger.log(counter.incrementAndGetInt());
        });
        Assert.assertFalse(scheduling.isEnd());
        Current.sleep(2500);
        Assert.assertTrue(scheduling.isEnd());
        Assert.assertEquals(counter.getInt(), 1);

        counter.reset();
        scheduling = scheduler.scheduleFixedRate(Duration.ZERO, Duration.ofMillis(1000), () -> {
            Current.sleep(1000);
            logger.log(counter.incrementAndGetInt());
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
        });
        Assert.assertFalse(scheduling.isEnd());
        Current.sleep(3000);
        Assert.assertFalse(scheduling.isEnd());
        scheduling.cancel(false);
        Assert.assertTrue(scheduling.isEnd());
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
