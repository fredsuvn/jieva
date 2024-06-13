package xyz.fslabo.common.invoke;

/**
 * Exception for invocation.
 *
 * @author fredsuvn
 */
public class GekInvokeException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public GekInvokeException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public GekInvokeException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public GekInvokeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public GekInvokeException(Throwable cause) {
        super(cause);
    }
}
