package sample.xyz.srclab.common.exception;

import org.testng.annotations.Test;
import xyz.srclab.common.exception.ExceptionStatus;
import xyz.srclab.common.exception.StatusException;
import xyz.srclab.common.test.TestLogger;

public class ExceptionSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testStatusException() {
        SampleException sampleException = new SampleException();
        //000001-Unknown Error[for sample]
        logger.log("Status: {}", sampleException.withMoreDescription("for sample"));
    }

    public static class SampleException extends StatusException {

        public SampleException() {
            super(ExceptionStatus.UNKNOWN);
        }
    }
}
