package test.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Counter;

public class CounterTest {

    @Test
    public void testCounter() {
        Counter counter = Counter.startsAt(100);
        Assert.assertEquals(counter.getAndIncrementInt(), 100);
        Assert.assertEquals(counter.incrementAndGetInt(), 102);
        Counter atomicCounter = Counter.startsAt(100, true);
        Assert.assertEquals(atomicCounter.getAndIncrementInt(), 100);
        Assert.assertEquals(atomicCounter.incrementAndGetInt(), 102);
    }
}
