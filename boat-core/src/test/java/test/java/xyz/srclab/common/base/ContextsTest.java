package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Contexts;
import xyz.srclab.common.run.RunContext;
import xyz.srclab.common.run.Runner;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author sunqian
 */
public class ContextsTest {

    @Test
    public void testContext() throws Exception {
        Assert.assertNull(Contexts.getPropertyOrNull("1"));
        Contexts.setProperty("1", "666");
        Assert.assertEquals("666", Contexts.getProperty("1"));
        Map<Object, Object> attach = Contexts.attachProperties();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Runner.runAsync(() -> {
            RunContext context = RunContext.current();
            context.detach(attach);
            Assert.assertEquals("666", context.get("1"));
            countDownLatch.countDown();
        });
        countDownLatch.await();
    }
}
