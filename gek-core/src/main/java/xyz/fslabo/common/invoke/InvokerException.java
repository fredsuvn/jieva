package xyz.fslabo.common.invoke;

/**
 * Exception for invocation.
 *
 * @author fredsuvn
 */
public class InvokerException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public InvokerException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public InvokerException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public InvokerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public InvokerException(Throwable cause) {
        super(cause);
    }
}
