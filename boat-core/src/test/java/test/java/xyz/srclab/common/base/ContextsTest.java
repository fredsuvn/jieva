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

    @Test
    public void testContextValue() {
        Contexts.setProperty("123", "321");
        Assert.assertEquals(Contexts.getProperty("123"), "321");
        Assert.assertEquals(Contexts.getPropertyOrElse("1234", "4321"), "4321");
        Assert.assertEquals(Contexts.getPropertyOrNull("1234"), (Object) null);
        Assert.expectThrows(IllegalArgumentException.class, () ->
            Contexts.getPropertyOrThrow("1234", k -> new IllegalArgumentException(k.toString())));
    }
}
