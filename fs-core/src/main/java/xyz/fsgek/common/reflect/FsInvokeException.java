package xyz.fsgek.common.reflect;

/**
 * Exception for invocation.
 *
 * @author fredsuvn
 */
public class FsInvokeException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public FsInvokeException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public FsInvokeException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public FsInvokeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public FsInvokeException(Throwable cause) {
        super(cause);
    }
}
