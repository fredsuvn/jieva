package test.java.xyz.srclab.common.run;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Counter;
import xyz.srclab.common.base.Current;
import xyz.srclab.common.base.IntRef;
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
        ScheduledRunner runner = ScheduledRunner.SINGLE_THREAD_RUNNER;
        doTestScheduledRunSync(runner);
    }

    @Test
    public void testScheduledThreadPoolRunSync() {
        ScheduledRunner runner = ScheduledThreadPoolRunner.newBuilder().build();
        doTestScheduledRunSync(runner);
    }

    private void doTestScheduledRunSync(ScheduledRunner runner) {
        Counter counter = Counter.startsAt(0);
        ScheduledRunning<?> running = runner.schedule(Duration.ofMillis(1000), () -> {
            Current.sleep(1000);
            logger.log(counter.incrementAndGetInt());
            return null;
        });
        Assert.assertFalse(running.isEnd());
        Current.sleep(2500);
        Assert.assertTrue(running.isEnd());
        Assert.assertEquals(counter.getInt(), 1);

        counter.reset();
        running = runner.scheduleAtFixedRate(Duration.ZERO, Duration.ofMillis(1000), () -> {
            Current.sleep(1000);
            logger.log(counter.incrementAndGetInt());
            return null;
        });
        Assert.assertFalse(running.isEnd());
        Current.sleep(2500);
        Assert.assertFalse(running.isEnd());
        running.cancel(false);
        Assert.assertTrue(running.isEnd());

        counter.reset();
        running = runner.scheduleWithFixedDelay(Duration.ZERO, Duration.ofMillis(1000), () -> {
            Current.sleep(1000);
            logger.log(counter.incrementAndGetInt());
            return null;
        });
        Assert.assertFalse(running.isEnd());
        Current.sleep(3000);
        Assert.assertFalse(running.isEnd());
        running.cancel(false);
        Assert.assertTrue(running.isEnd());
    }
}
