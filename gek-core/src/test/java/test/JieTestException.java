package test;

import xyz.fslabo.common.base.JieException;

public class JieTestException extends JieException {

    public JieTestException() {
    }

    public JieTestException(String message) {
        super(message);
    }

    public JieTestException(String message, Throwable cause) {
        super(message, cause);
    }

    public JieTestException(Throwable cause) {
        super(cause);
    }
}
