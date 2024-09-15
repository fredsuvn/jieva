package xyz.fslabo.common.invoke;

import xyz.fslabo.common.base.JieException;

/**
 * Exception for invoking.
 *
 * @author fredsuvn
 */
public class InvokingException extends JieException {

    /**
     * Empty constructor.
     */
    public InvokingException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public InvokingException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public InvokingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public InvokingException(Throwable cause) {
        super(cause);
    }
}
