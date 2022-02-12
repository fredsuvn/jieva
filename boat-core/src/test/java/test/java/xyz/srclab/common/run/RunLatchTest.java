package test.java.xyz.srclab.common.run;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BThread;
import xyz.srclab.common.run.RunLatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class RunLatchTest {

    private static final int THREAD_NUMBER = 88;
    private static final AtomicInteger sum = new AtomicInteger(0);
    private static final CountDownLatch countDownLatch = new CountDownLatch(THREAD_NUMBER);

    @Test
    public void testRunLatch() throws Exception {
        RunLatch latch = RunLatch.newRunLatch();
        for (int i = 0; i < THREAD_NUMBER; i++) {
            TestThread testThread = new TestThread(latch);
            testThread.start();
        }
        BLog.info("sum before latch: {}", sum.get());
        BThread.sleep(1000);
        BLog.info("sum after 1000 millis sleep: {}", sum.get());
        Assert.assertEquals(sum.get(), 0);
        latch.unlock();
        countDownLatch.await();
        BLog.info("sum after latch: {}", sum.get());
        Assert.assertEquals(sum.get(), THREAD_NUMBER);
    }

    private static class TestThread extends Thread {

        private final RunLatch latch;

        public TestThread(RunLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            latch.await();
            sum.incrementAndGet();
            countDownLatch.countDown();
        }
    }
}
