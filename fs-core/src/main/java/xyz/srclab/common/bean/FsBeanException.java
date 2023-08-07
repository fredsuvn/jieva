package xyz.srclab.common.bean;

/**
 * Exception for Fs bean.
 *
 * @author fredsuvn
 */
public class FsBeanException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public FsBeanException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public FsBeanException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public FsBeanException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public FsBeanException(Throwable cause) {
        super(cause);
    }
}
