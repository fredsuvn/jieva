package test.java.xyz.srclab.common.run;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BThread;
import xyz.srclab.common.base.IntRef;
import xyz.srclab.common.run.*;
import xyz.srclab.common.utils.Counter;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author sunqian
 */
public class RunTest {

    @Test
    public void testRunSync() {
        Runner runner = SyncRunner.INSTANCE;
        IntRef intRef = IntRef.of(0);
        RunWork<?> work = runner.submit(() -> {
            BThread.sleep(2000);
            intRef.set(666);
        });
        Assert.assertTrue(work.isDone());
        Assert.assertEquals(intRef.get(), 666);

        runner.submit(() -> intRef.set(888));
        Assert.assertEquals(intRef.get(), 888);
    }

    @Test
    public void testRunAsync() {
        Runner runner = AsyncRunner.INSTANCE;
        doTestAsync(runner);
    }

    @Test
    public void testThreadPoolRunner() {
        Runner runner = ThreadPoolRunner.newBuilder().build();
        doTestAsync(runner);
    }

    private void doTestAsync(Runner runner) {
        IntRef intRef = IntRef.of(0);
        CountDownLatch currentLatch = new CountDownLatch(1);
        RunWork<?> work = runner.submit(() -> {
            try {
                currentLatch.await();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            intRef.set(666);
        });
        Assert.assertEquals(intRef.get(), 0);
        Assert.assertFalse(work.isDone());
        currentLatch.countDown();
        work.getResult();
        Assert.assertTrue(work.isDone());
        Assert.assertEquals(intRef.get(), 666);

        runner.submit(() -> {
            intRef.set(888);
            Assert.assertEquals(intRef.get(), 888);
        });
    }

    @Test
    public void testScheduledRunSync() {
        Scheduler scheduler = SingleThreadScheduler.INSTANCE;
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
        ScheduleWork<?> scheduling = scheduler.schedule(Duration.ofMillis(1000), () -> {
            BThread.sleep(1000);
            BLog.info("count: {}", counter.incrementAndGetInt());
        });
        Assert.assertFalse(scheduling.isDone());
        BThread.sleep(2500);
        Assert.assertTrue(scheduling.isDone());
        Assert.assertEquals(counter.getInt(), 1);

        counter.reset();
        scheduling = scheduler.scheduleFixedRate(Duration.ZERO, Duration.ofMillis(1000), () -> {
            BThread.sleep(1000);
            BLog.info("count: {}", counter.incrementAndGetInt());
        });
        Assert.assertFalse(scheduling.isDone());
        BThread.sleep(2500);
        Assert.assertFalse(scheduling.isDone());
        scheduling.cancel(false);
        Assert.assertTrue(scheduling.isDone());

        counter.reset();
        scheduling = scheduler.scheduleFixedDelay(Duration.ZERO, Duration.ofMillis(1000), () -> {
            BThread.sleep(1000);
            BLog.info("count: {}", counter.incrementAndGetInt());
        });
        Assert.assertFalse(scheduling.isDone());
        BThread.sleep(3000);
        Assert.assertFalse(scheduling.isDone());
        scheduling.cancel(false);
        Assert.assertTrue(scheduling.isDone());
    }

    @Test
    public void testRunContext() throws Exception {
        RunContext runContext = RunContext.current();
        runContext.set("1", "666");
        Assert.assertEquals("666", runContext.get("1"));
        Map<Object, Object> attach = runContext.asMap();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AsyncRunner.INSTANCE.run(() -> {
            RunContext detach = RunContext.current();
            detach.asMap().putAll(attach);
            Assert.assertEquals("666", detach.get("1"));
            countDownLatch.countDown();
        });
        countDownLatch.await();
    }
}
