package sample.java.xyz.srclab.core.invoke;

import org.testng.annotations.Test;
import xyz.srclab.common.func.Invoker;
import xyz.srclab.common.test.TestLogger;

public class InvokeSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testInvoke() throws Exception {
        Invoker invoker = Invoker.forMethod(String.class.getMethod("getBytes"));
        byte[] bytes = invoker.invoke("10086");
        //[49, 48, 48, 56, 54]
        logger.log("bytes: {}", bytes);
    }
}
