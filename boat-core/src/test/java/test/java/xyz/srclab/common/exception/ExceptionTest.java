package test.java.xyz.srclab.common.exception;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.exception.ImpossibleException;
import xyz.srclab.common.exception.StatusException;
import xyz.srclab.common.status.Status;

import java.util.List;

/**
 * @author sunqian
 */
public class ExceptionTest {

    @Test
    public void test() {
        ImpossibleException impossibleException = new ImpossibleException();
        TestException testException = new TestException(StatusException.INTERNAL_STATUS, impossibleException);
        Assert.assertEquals(testException.description(), StatusException.INTERNAL_STATUS.description());
        Assert.assertEquals(testException.getCause(), impossibleException);

        TestException testException2 = new TestException("8", "888", impossibleException);
        Status<String, String> exceptionStatus = testException2.withMoreDescription("999");
        Assert.assertEquals(exceptionStatus.description(), "888[999]");
        Status<String, String> exceptionStatus2 = testException2.withNewDescription("999");
        Assert.assertEquals(exceptionStatus2.description(), "999");
    }

    public static class TestException extends StatusException {

        public TestException(@NotNull Status<String, String> exceptionStatus, @Nullable Throwable cause) {
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

        @Override
        public String code() {
            return super.getCode();
        }

        @Nullable
        @Override
        public String description() {
            return super.getDescription();
        }

        @NotNull
        @Override
        public List<String> descriptions() {
            return super.getDescriptions();
        }
    }
}
