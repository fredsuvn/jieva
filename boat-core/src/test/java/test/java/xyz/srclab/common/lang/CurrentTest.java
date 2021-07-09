package test.java.xyz.srclab.common.lang;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.Current;
import xyz.srclab.common.run.RunContext;
import xyz.srclab.common.run.Runner;

import java.util.concurrent.CountDownLatch;

/**
 * @author sunqian
 */
public class CurrentTest {

    @Test
    public void testCurrent() throws Exception {
        Assert.assertNull(Current.getOrNull("1"));
        Current.set("1", "666");
        Assert.assertEquals("666", Current.get("1"));
        RunContext.Attach attach = Current.attach();
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
