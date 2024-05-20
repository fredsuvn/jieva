package xyz.fsgek.common.base;

/**
 * Base exception for Gek.
 *
 * @author fredsuvn
 */
public class GekRuntimeException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public GekRuntimeException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public GekRuntimeException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public GekRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public GekRuntimeException(Throwable cause) {
        super(cause);
    }
}
