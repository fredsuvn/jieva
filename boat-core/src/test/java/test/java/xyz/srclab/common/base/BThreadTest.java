package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BThread;
import xyz.srclab.common.collect.BMap;

import java.util.Map;

/**
 * @author sunqian
 */
public class BThreadTest {

    @Test
    public void testThread() {
        BThread.setLocal("a", "1");
        BThread.setLocal("b", "2");
        BThread.setLocal("c", "3");
        Assert.assertEquals(BThread.setLocal("c", "4"), "3");
        Map<Object, Object> locals = BThread.getLocals();
        locals.put("z", "9");
        locals.remove("c");
        BLog.info("Locals: {}", BThread.getLocals());
        Assert.assertEquals(BThread.getLocals(), BMap.newMap("a", "1", "b", "2", "z", "9"));
        Assert.assertEquals(BThread.getLocal("a"), "1");
        Assert.assertEquals(BThread.getLocal("b"), "2");
        Assert.assertEquals(BThread.getLocal("z"), "9");
        Assert.assertNull(BThread.getLocal("c"));
    }
}
