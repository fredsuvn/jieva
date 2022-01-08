package test.java.xyz.srclab.common.exception;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.exception.ImpossibleException;
import xyz.srclab.common.exception.StatusException;
import xyz.srclab.common.status.Status;
import xyz.srclab.common.status.StringStatus;

/**
 * @author sunqian
 */
public class ExceptionTest {

    @Test
    public void test() {
        ImpossibleException impossibleException = new ImpossibleException();
        TestException testException = new TestException(StatusException.INTERNAL_STATUS, impossibleException);
        Assert.assertEquals(testException.getDescription(), StatusException.INTERNAL_STATUS.getDescription());
        Assert.assertEquals(testException.getCause(), impossibleException);

        TestException testException2 = new TestException("8", "888", impossibleException);
        Status<String, String, StringStatus> exceptionStatus = testException2.withMoreDescription("999");
        Assert.assertEquals(exceptionStatus.getDescription(), "888[999]");
        Status<String, String, StringStatus> exceptionStatus2 = testException2.withNewDescription("999");
        Assert.assertEquals(exceptionStatus2.getDescription(), "999");
    }

    public static class TestException extends StatusException {

        public TestException(@NotNull Status<String, String, StringStatus> exceptionStatus, @Nullable Throwable cause) {
            super(exceptionStatus, cause);
        }

        public TestException(@NotNull String code, @Nullable String description, @Nullable Throwable cause) {
            super(code, description, cause);
        }

        @Override
        public String getCode() {
            return super.getCode();
        }

        @Nullable
        @Override
        public String getDescription() {
            return super.getDescription();
        }
    }
}
