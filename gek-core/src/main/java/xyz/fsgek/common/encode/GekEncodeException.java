package xyz.fsgek.common.encode;

/**
 * Exception for encoding/decoding.
 *
 * @author fredsuvn
 */
public class GekEncodeException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public GekEncodeException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public GekEncodeException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public GekEncodeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public GekEncodeException(Throwable cause) {
        super(cause);
    }
}
