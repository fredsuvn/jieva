package sample.java.xyz.srclab.core.jvm;

import org.testng.annotations.Test;
import xyz.srclab.common.jvm.Jvms;
import xyz.srclab.common.test.TestLogger;

public class JvmSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testJvms() {
        String jvmDescriptor = Jvms.jvmDescriptor(int.class);
        //I
        logger.log("jvmDescriptor: {}", jvmDescriptor);
    }
}
