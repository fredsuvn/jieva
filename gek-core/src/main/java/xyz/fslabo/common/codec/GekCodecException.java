package xyz.fslabo.common.codec;

/**
 * Exception for codec.
 *
 * @author fredsuvn
 */
public class GekCodecException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public GekCodecException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public GekCodecException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public GekCodecException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public GekCodecException(Throwable cause) {
        super(cause);
    }
}
