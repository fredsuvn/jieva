package xyz.fsgek.common.io;

/**
 * Exception for IO.
 *
 * @author fredsuvn
 */
public class GekIOException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public GekIOException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public GekIOException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public GekIOException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public GekIOException(Throwable cause) {
        super(cause);
    }
}
