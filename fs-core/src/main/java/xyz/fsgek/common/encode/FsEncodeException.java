package xyz.fsgek.common.encode;

/**
 * Exception for encoding/decoding.
 *
 * @author fredsuvn
 */
public class FsEncodeException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public FsEncodeException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public FsEncodeException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public FsEncodeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public FsEncodeException(Throwable cause) {
        super(cause);
    }
}
