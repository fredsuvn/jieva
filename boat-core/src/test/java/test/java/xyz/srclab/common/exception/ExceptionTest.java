package test.java.xyz.srclab.common.exception;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.exception.ExceptionStatus;
import xyz.srclab.common.exception.ShouldNotException;
import xyz.srclab.common.exception.StatusException;

/**
 * @author sunqian
 */
public class ExceptionTest {

    @Test
    public void test() {
        ShouldNotException shouldNotException = new ShouldNotException();
        TestException testException = new TestException(ExceptionStatus.INTERNAL, shouldNotException);
        Assert.assertEquals(testException.description(), ExceptionStatus.INTERNAL.description());
        Assert.assertEquals(testException.getCause(), shouldNotException);

        TestException testException2 = new TestException("8", "888", shouldNotException);
        ExceptionStatus exceptionStatus = testException2.withMoreDescription("999");
        Assert.assertEquals(exceptionStatus.description(), "888[999]");
        ExceptionStatus exceptionStatus2 = testException2.withNewDescription("999");
        Assert.assertEquals(exceptionStatus2.description(), "999");
    }

    public static class TestException extends StatusException {

        public TestException(@NotNull ExceptionStatus exceptionStatus, @Nullable Throwable cause) {
            super(exceptionStatus, cause);
        }

        public TestException() {
        }

        public TestException(@Nullable Throwable cause) {
            super(cause);
        }

        public TestException(@Nullable String message) {
            super(message);
        }

        public TestException(@Nullable String message, @Nullable Throwable cause) {
            super(message, cause);
        }

        public TestException(@NotNull String code, @Nullable String description, @Nullable Throwable cause) {
            super(code, description, cause);
        }
    }
}
