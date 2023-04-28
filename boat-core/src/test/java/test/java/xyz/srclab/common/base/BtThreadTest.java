package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BtLog;
import xyz.srclab.common.base.BtThread;
import xyz.srclab.common.collect.BtMap;

import java.util.Map;

/**
 * @author sunqian
 */
public class BtThreadTest {

    @Test
    public void testThread() {
        BtThread.set("a", "1");
        BtThread.set("b", "2");
        BtThread.set("c", "3");
        Assert.assertEquals(BtThread.set("c", "4"), "3");
        Map<Object, Object> locals = BtThread.getAll();
        locals.put("z", "9");
        locals.remove("c");
        BtLog.info("Locals: {}", BtThread.getAll());
        Assert.assertEquals(BtThread.getAll(), BtMap.of("a", "1", "b", "2", "z", "9"));
        Assert.assertEquals(BtThread.get("a"), "1");
        Assert.assertEquals(BtThread.get("b"), "2");
        Assert.assertEquals(BtThread.get("z"), "9");
        Assert.assertNull(BtThread.get("c"));
    }
}
