package xyz.fs404.common.io;

/**
 * Exception for IO.
 *
 * @author fredsuvn
 */
public class FsIOException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public FsIOException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public FsIOException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public FsIOException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public FsIOException(Throwable cause) {
        super(cause);
    }
}
