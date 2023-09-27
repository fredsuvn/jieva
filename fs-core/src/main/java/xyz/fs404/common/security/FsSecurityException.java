package xyz.fs404.common.security;

/**
 * Exception for security.
 *
 * @author fredsuvn
 */
public class FsSecurityException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public FsSecurityException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public FsSecurityException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public FsSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public FsSecurityException(Throwable cause) {
        super(cause);
    }
}
