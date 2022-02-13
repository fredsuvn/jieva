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

    @Test
    public void testRunLatch() throws Exception {
        RunLatch latch1 = RunLatch.newRunLatch();
        CountDownLatch countDownLatch1 = new CountDownLatch(THREAD_NUMBER);
        for (int i = 0; i < THREAD_NUMBER; i++) {
            TestThread testThread = new TestThread(latch1, countDownLatch1);
            testThread.start();
        }
        BLog.info("sum before latch1: {}", sum.get());
        BThread.sleep(1000);
        BLog.info("sum after 1000 millis sleep: {}", sum.get());
        Assert.assertEquals(sum.get(), 0);
        //BThread.sleep(5000);
        latch1.unlock();
        countDownLatch1.await();
        BLog.info("sum after latch1: {}", sum.get());
        Assert.assertEquals(sum.get(), THREAD_NUMBER);

        sum.set(0);
        RunLatch latch2 = RunLatch.newRunLatch();
        CountDownLatch countDownLatch2 = new CountDownLatch(THREAD_NUMBER);
        for (int i = 0; i < THREAD_NUMBER; i++) {
            TestThread testThread = new TestThread(latch2, countDownLatch2);
            testThread.start();
        }
        BLog.info("sum before latch2: {}", sum.get());
        BThread.sleep(1000);
        BLog.info("sum after 1000 millis sleep: {}", sum.get());
        Assert.assertEquals(sum.get(), 0);
        //BThread.sleep(5000);
        latch2.lockTo(0);
        countDownLatch2.await();
        BLog.info("sum after latch2: {}", sum.get());
        Assert.assertEquals(sum.get(), THREAD_NUMBER);
    }

    private static class TestThread extends Thread {

        private final RunLatch latch;
        private final CountDownLatch countDownLatch;

        public TestThread(RunLatch latch, CountDownLatch countDownLatch) {
            this.latch = latch;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            latch.await();
            sum.incrementAndGet();
            countDownLatch.countDown();
        }
    }
}
