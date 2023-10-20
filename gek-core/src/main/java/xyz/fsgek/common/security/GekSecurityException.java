package xyz.fsgek.common.security;

/**
 * Exception for security.
 *
 * @author fredsuvn
 */
public class GekSecurityException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public GekSecurityException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public GekSecurityException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public GekSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public GekSecurityException(Throwable cause) {
        super(cause);
    }
}
