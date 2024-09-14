package test.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.reflect.StackCounter;

public class ReflectMiscTest {

    @Test
    public void testStackCounter() throws Exception {
        StackCounter counter = new StackCounter();
        testStackCounterValue(counter);
        counter.reset();
        testStackCounterValue(counter);
    }

    private void testStackCounterValue(StackCounter counter) {
        Assert.assertEquals(counter.getDepth(), 0);
        Assert.assertEquals(counter.getMaxDepth(), 0);
        counter.increment();
        Assert.assertEquals(counter.getDepth(), 1);
        Assert.assertEquals(counter.getMaxDepth(), 1);
        counter.increment();
        Assert.assertEquals(counter.getDepth(), 2);
        Assert.assertEquals(counter.getMaxDepth(), 2);
        counter.decrement();
        Assert.assertEquals(counter.getDepth(), 1);
        Assert.assertEquals(counter.getMaxDepth(), 2);
        counter.increment();
        Assert.assertEquals(counter.getDepth(), 2);
        Assert.assertEquals(counter.getMaxDepth(), 2);
    }
}
