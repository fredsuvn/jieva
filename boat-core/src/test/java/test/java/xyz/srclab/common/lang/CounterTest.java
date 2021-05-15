package test.java.xyz.srclab.common.lang;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.utils.Counter;

public class CounterTest {

    @Test
    public void testCounter() {
        Counter counter = Counter.startsAt(100);
        Assert.assertEquals(counter.getAndIncrementInt(), 100);
        Assert.assertEquals(counter.incrementAndGetInt(), 102);
        counter.reset();
        Assert.assertEquals(counter.incrementAndGetInt(), 101);
        Counter atomicCounter = Counter.startsAt(100, true);
        Assert.assertEquals(atomicCounter.getAndIncrementInt(), 100);
        Assert.assertEquals(atomicCounter.incrementAndGetInt(), 102);
        atomicCounter.reset();
        Assert.assertEquals(atomicCounter.incrementAndGetInt(), 101);
    }
}
