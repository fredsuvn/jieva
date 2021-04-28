package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Counter;
import xyz.srclab.common.base.Lazy;
import xyz.srclab.common.base.LazyString;
import xyz.srclab.common.test.TestLogger;

import java.time.Duration;

/**
 * @author sunqian
 */
public class LazyTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testLazy() throws InterruptedException {
        Lazy<String> lazy = Lazy.of(() -> "123");
        String value = lazy.get();
        Assert.assertEquals(value, "123");

        Counter counter = Counter.startsAt(0);
        Lazy<Integer> pLazy = Lazy.of(Duration.ofMillis(1), counter::getAndIncrementInt);
        int pi1 = pLazy.get();
        Assert.assertEquals(pi1, 0);
        Thread.sleep(2);
        int pi2 = pLazy.get();
        Assert.assertEquals(pi2, 1);
        pLazy.refresh();
        int pi3 = pLazy.get();
        Assert.assertEquals(pi3, 2);

        counter.setInt(0);
        Lazy<Integer> ppLazy = Lazy.of(i -> Duration.ofMillis(1), counter::getAndIncrementInt);
        int ppi1 = ppLazy.get();
        Assert.assertEquals(ppi1, 0);
        Thread.sleep(2);
        int ppi2 = ppLazy.get();
        Assert.assertEquals(ppi2, 1);
        ppLazy.refresh();
        int ppi3 = ppLazy.get();
        Assert.assertEquals(ppi3, 2);
    }

    @Test
    public void testLazyString() {
        Counter counter = Counter.startsAt(0);
        LazyString<Integer> lazyString = LazyString.of(Lazy.of(counter::getAndIncrementInt));
        logger.log("lazyString: {}", lazyString);
        Assert.assertEquals(
                lazyString.get(),
                new Integer(0)
        );
    }
}
