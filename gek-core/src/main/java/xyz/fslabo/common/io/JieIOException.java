package xyz.fslabo.common.io;

/**
 * Exception for IO.
 *
 * @author fredsuvn
 */
public class JieIOException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public JieIOException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public JieIOException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public JieIOException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public JieIOException(Throwable cause) {
        super(cause);
    }
}
