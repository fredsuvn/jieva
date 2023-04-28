package test.java.xyz.srclab.common.run;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BThread;
import xyz.srclab.common.base.BooleanRef;
import xyz.srclab.common.base.IntRef;
import xyz.srclab.common.run.*;
import xyz.srclab.common.utils.Counter;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sunqian
 */
public class RunTest {

    @Test
    public void testRunSync() {
        Runner runner = SyncExecutorService.INSTANCE;
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
        Runner runner = Runner.of(
            new ThreadPoolExecutor(5, 5, 0, TimeUnit.MILLISECONDS, new SynchronousQueue<>())
        );
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
        Scheduler scheduler = Scheduler.of(
            Executors.newScheduledThreadPool(5)
        );
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
    public void testSyncRunner() {
        Runner runner = SyncExecutorService.INSTANCE;
        Thread thread = Thread.currentThread();
        int totalThread = 100;
        IntRef count = IntRef.of(0);
        BooleanRef sync = BooleanRef.of(true);
        for (int j = 0; j < totalThread; j++) {
            runner.run(() -> {
                count.set(count.get() + 1);
                if (!Thread.currentThread().equals(thread)) {
                    sync.set(false);
                }
            });
        }
        Assert.assertEquals(count.get(), totalThread);
        Assert.assertTrue(sync.get());
    }

    @Test
    public void testAsyncRunner() throws Exception {
        Runner runner = AsyncRunner.INSTANCE;
        int totalThread = 100;
        AtomicInteger count = new AtomicInteger(0);
        CountDownLatch mainLatch = new CountDownLatch(1);
        CountDownLatch threadLatch = new CountDownLatch(totalThread);
        for (int j = 0; j < totalThread; j++) {
            runner.run(() -> {
                try {
                    mainLatch.await();
                    count.incrementAndGet();
                    threadLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        Assert.assertEquals(count.get(), 0);
        mainLatch.countDown();
        threadLatch.await();
        Assert.assertEquals(count.get(), totalThread);
    }
}
