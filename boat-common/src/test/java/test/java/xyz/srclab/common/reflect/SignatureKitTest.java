package test.java.xyz.srclab.common.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.SignatureKit;
import xyz.srclab.common.test.TestLogger;

/**
 * @author sunqian
 */
public class SignatureKitTest {

    private static final TestLogger testLogger = TestLogger.DEFAULT;

    public SignatureKitTest() {
    }

    public SignatureKitTest(String a, int b) {
    }

    @Test
    public void testSignatureString() throws Exception {
        Assert.assertEquals(
                SignatureKit.toSignatureString(getClass().getMethod("testMethod", String.class, int.class)),
                "testMethod(java.lang.String, int)"
        );
        Assert.assertEquals(
                SignatureKit.toSignatureString(getClass().getConstructor(String.class, int.class)),
                getClass().getName() + "(java.lang.String, int)"
        );
    }

    @Test
    public void testContractSignatureName() {
        String className = "test.java.xyz.srclab.common.reflect.SignatureKitTest";
        Assert.assertEquals(
                SignatureKit.contractSignatureName(className),
                "t.j.x.s.c.r.SignatureKitTest"
        );
        Assert.assertEquals(
                SignatureKit.contractSignatureName(className, 100),
                "test.java.xyz.srclab.common.reflect.SignatureKitTest"
        );
        Assert.assertEquals(
                SignatureKit.contractSignatureName(className, className.length() - 1),
                "t.java.xyz.srclab.common.reflect.SignatureKitTest"
        );
    }

    public void testMethod(String a, int b) {
    }
}
